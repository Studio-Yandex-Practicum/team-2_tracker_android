package com.nemislimus.tratometr.settings.ui.fragment

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.common.App
import com.nemislimus.tratometr.common.alarmPermissionDeniedOnCurrentSession
import com.nemislimus.tratometr.common.appComponent
import com.nemislimus.tratometr.common.notificationPermissionDeniedOnCurrentSession
import com.nemislimus.tratometr.common.util.BindingFragment
import com.nemislimus.tratometr.databinding.FragmentSettingsBinding
import com.nemislimus.tratometr.expenses.domain.model.Expense
import com.nemislimus.tratometr.settings.domain.model.SettingsParams
import com.nemislimus.tratometr.settings.ui.viewmodel.SettingsFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    @Inject
    lateinit var vmFactory: SettingsFragmentViewModel.Factory
    private val viewModel: SettingsFragmentViewModel by viewModels { vmFactory }
    private var themeChangingJob: Job? = null
    private var reminderChangingJob: Job? = null
    private var timePicker: MaterialTimePicker? = null
    private val systemVersionWithPermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionIsGranted: Boolean ->
            if (permissionIsGranted) {
                checkExactAlarmPermission()
            } else {
                binding.swReminder.isEnabled = false

                if (systemVersionWithPermissions) {
                    val shouldShowRequestPermission =
                        shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)

                    if (!shouldShowRequestPermission) {
                        showToast(requireContext().getString(R.string.alarm_permission_request))
                        requireContext().notificationPermissionDeniedOnCurrentSession = true
                        getPermissionOnNotificationAtSettings()
                    }
                }
            }
        }

    private val csvDocumentLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
        uri?.let {
            val writeFileStream = requireActivity().contentResolver.openOutputStream(uri)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getExpensesFlow().flowOn(Dispatchers.IO).collect { expenses ->
                    val content = expensesListToCsvString(expenses)
                    writeFileStream?.write(content.toByteArray())
                    withContext(Dispatchers.Main) {
                        showToast("Файл сохранен: $uri")
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        requireActivity().appComponent.inject(this)
        super.onAttach(context)
    }

    override fun createBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeSettingsParams().observe(viewLifecycleOwner) { settingsPrams ->
            stateProcessing(settingsPrams)
        }

        setUiListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            delay(500)
            checkNotificationPermission()
        }

    }

    override fun onResume() {
        super.onResume()
        if (checkAlarmPermissionOnResume()) binding.swReminder.isEnabled = true
    }

    override fun onDestroyFragment() {
        timePicker?.dismiss()
        timePicker = null

        themeChangingJob?.cancel()
        themeChangingJob = null

        reminderChangingJob?.cancel()
        reminderChangingJob = null
    }

    private fun setUiListeners() {
        binding.swReminder.setOnClickListener {
            clickOnReminderSwitch(binding.swReminder.isChecked)
        }

        binding.swTheme.setOnClickListener {
            clickOnThemeSwitch(binding.swTheme.isChecked)
        }

        binding.btnExport.setOnClickListener {
            createCsvFile()
        }

        binding.btnLogOut.setOnClickListener {
            viewModel.logOut()
            findNavController().navigate(R.id.action_settingsFragment_to_authorizationFragment)
        }

        binding.tbSettings.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun stateProcessing(params: SettingsParams) {
        themeSwitchRender(params.isDarkMode)
        reminderSwitchRender(params.remindTime)
    }

    private suspend fun updateSettingsParams(
        reminderTime: String = binding.tvTimeRemind.text.toString(),
        isDarkMode: Boolean = binding.swTheme.isChecked,
    ) {
        val params: SettingsParams = withContext(Dispatchers.Main) {
            pickSettingsParams(reminderTime, isDarkMode)
        }
        viewModel.saveSettings(params)
        viewModel.getSettingsParams()
    }

    private fun clickOnReminderSwitch(checked: Boolean) {
        reminderChangingJob?.cancel()
        reminderChangingJob = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            delay(600)
            withContext(Dispatchers.Main) {
                if (binding.tvTimeRemind.text.isBlank()) showTimePickerDialog(checked)
                if (!checked) reminderStop()
            }
        }
    }

    private fun clickOnThemeSwitch(checked: Boolean) {
        themeChangingJob?.cancel()
        themeChangingJob = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            updateSettingsParams()
            delay(600)
            withContext(Dispatchers.Main) {
                switchAppTheme(checked)
            }
        }
    }

    private fun themeSwitchRender(isDarkTheme: Boolean) {
        with(binding) {
            binding.swTheme.isChecked = isDarkTheme
            ivDarkThemeIcon.isVisible = isDarkTheme
            ivLightThemeIcon.isVisible = !isDarkTheme
        }
    }

    private fun reminderSwitchRender(timeStringValue: String) {
        binding.tvTimeRemind.text = timeStringValue
        binding.swReminder.isChecked = timeStringValue.isNotBlank()
    }

    private fun pickSettingsParams(reminderTime: String, isDarkMode: Boolean): SettingsParams {
        return SettingsParams(
            remindTime = reminderTime,
            isDarkMode = isDarkMode,
        )
    }

    private fun switchAppTheme(isDarkMode: Boolean) {
        (requireActivity().applicationContext as App).switchTheme(isDarkMode)
    }

    private fun showTimePickerDialog(checked: Boolean) {
        if (checked) {
            val title = getString(R.string.select_time)
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute).setTitleText(title)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK).build()

            setTimePickerListeners(timePicker)
            timePicker?.show(parentFragmentManager, TIME_PICKER_TAG)

        }
    }

    private fun setTimePickerListeners(picker: MaterialTimePicker?) {
        picker?.addOnPositiveButtonClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                reminderStart(picker.hour, picker.minute)
            }
        }

        picker?.addOnNegativeButtonClickListener {
            binding.swReminder.isChecked = false
        }

        picker?.addOnCancelListener {
            binding.swReminder.isChecked = false
        }
    }

    private suspend fun reminderStart(hours: Int, minutes: Int) {
        val timeStringValue = viewModel.correctTimeString(hours, minutes)
        withContext(Dispatchers.IO) { updateSettingsParams(timeStringValue) }
        viewModel.setNotification(requireContext(), hours, minutes)
    }

    private suspend fun reminderStop() {
        withContext(Dispatchers.IO) { updateSettingsParams("") }
        viewModel.cancelNotification(requireContext())
    }

    private fun checkNotificationPermission(context: Context = requireContext()) {
        if (systemVersionWithPermissions) {
            val permissionProvided = ContextCompat
                .checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)

            if (permissionProvided == PackageManager.PERMISSION_GRANTED) {
                checkExactAlarmPermission()
            } else if (permissionProvided == PackageManager.PERMISSION_DENIED) {
                binding.swReminder.isEnabled = false
                if (!requireContext().notificationPermissionDeniedOnCurrentSession) {
                    requireContext().notificationPermissionDeniedOnCurrentSession = true
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun checkExactAlarmPermission(context: Context = requireContext()) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                binding.swReminder.isEnabled = false
                if (!requireContext().alarmPermissionDeniedOnCurrentSession) {
                    showToast(context.getString(R.string.notification_permission_request))
                    requireContext().alarmPermissionDeniedOnCurrentSession = true
                    getPermissionOnExactAlarmAtSettings(context)
                }
            } else {
                binding.swReminder.isEnabled = true
            }
        }
    }

    private fun checkAlarmPermissionOnResume(context: Context = requireContext()): Boolean {
        var alarm = false
        if (systemVersionWithPermissions) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm = alarmManager.canScheduleExactAlarms()
        }
        return alarm
    }

    private fun getPermissionOnNotificationAtSettings(context: Context = requireContext()) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(intent)
    }

    private fun getPermissionOnExactAlarmAtSettings(context: Context = requireContext()) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun expensesListToCsvString(expenses: List<Expense>): String {
        var result = ContextCompat.getString(requireContext(), R.string.csv_header)
        val decimalFormat = DecimalFormat("#,##0.00")
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        expenses.forEach { expense ->
            val dateFormatted = dateFormat.format(Date(expense.date))
            val amountFormatted = decimalFormat.format(expense.amount)
            result += "$dateFormatted;${expense.category};$amountFormatted\n"
        }
        return result
    }

    private fun createCsvFile() {
        val dateFormat = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val fileName = currentDate + ContextCompat.getString(requireContext(), R.string.csv_name_last_part)
        csvDocumentLauncher.launch(fileName)
    }

    companion object {
        const val TIME_PICKER_TAG = "timepicker_tag"
    }

}