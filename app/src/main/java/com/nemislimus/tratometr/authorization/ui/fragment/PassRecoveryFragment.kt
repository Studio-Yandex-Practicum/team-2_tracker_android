package com.nemislimus.tratometr.authorization.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.authorization.domain.models.Resource
import com.nemislimus.tratometr.authorization.ui.viewmodel.PassRecoveryViewModel
import com.nemislimus.tratometr.common.appComponent
import com.nemislimus.tratometr.common.util.BindingFragment
import com.nemislimus.tratometr.common.util.FieldValidator
import com.nemislimus.tratometr.common.util.TratometrTextWatcher
import com.nemislimus.tratometr.databinding.FragmentPassrecoveryBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class PassRecoveryFragment : BindingFragment<FragmentPassrecoveryBinding>() {

    @Inject
    lateinit var vmFactory: PassRecoveryViewModel.Factory
    lateinit var viewModel: PassRecoveryViewModel

    private var flowCollectJob: Job? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPassrecoveryBinding {
        return FragmentPassrecoveryBinding.inflate(inflater, container, false)
    }

    override fun onAttach(context: Context) {
        requireActivity().appComponent.inject(this)
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity(), vmFactory)[PassRecoveryViewModel::class]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailText.addTextChangedListener(
            TratometrTextWatcher(
                TratometrTextWatcher.FieldType.Email(binding.emailField, binding.emailText),
                ::updateSendButtonState
            )
        )

        binding.recoverButton.setOnClickListener {
            val email = binding.emailText.text.toString()
            viewModel.recoverPass(email)
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        observeEvents()
    }

    override fun onPause() {
        super.onPause()
        flowCollectJob?.cancel()
        flowCollectJob = null
    }

    private fun observeEvents() {
        flowCollectJob = lifecycleScope.launch {
            viewModel.recoveryResult.collect { result ->
                when (result) {
                    is Resource.Success -> {
                        showToast(getString(R.string.success))
                        findNavController().navigate(R.id.action_passRecoveryFragment_to_newPassFragment)
                    }

                    is Resource.Error -> {
                        result.message?.let {
                            showToast(it)
                        }
                    }
                }
            }
        }
    }

    private fun updateSendButtonState() {
        val email = binding.emailText.text.toString()
        val isEmailValid = FieldValidator.isValidEmail(email)

        binding.recoverButton.isEnabled = isEmailValid
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}