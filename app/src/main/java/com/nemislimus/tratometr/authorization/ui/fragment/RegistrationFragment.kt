package com.nemislimus.tratometr.authorization.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.authorization.domain.models.Resource
import com.nemislimus.tratometr.authorization.ui.viewmodel.RegistrationViewModel
import com.nemislimus.tratometr.common.appComponent
import com.nemislimus.tratometr.common.util.BindingFragment
import com.nemislimus.tratometr.common.util.FieldValidator
import com.nemislimus.tratometr.common.util.TratometrTextWatcher
import com.nemislimus.tratometr.databinding.FragmentRegistrationBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationFragment : BindingFragment<FragmentRegistrationBinding>() {

    @Inject
    lateinit var vmFactory: RegistrationViewModel.Factory
    lateinit var viewModel: RegistrationViewModel
    var privacyAccepted = false

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegistrationBinding {
        return FragmentRegistrationBinding.inflate(inflater, container, false)
    }

    override fun onAttach(context: Context) {
        requireActivity().appComponent.inject(this)
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity(), vmFactory)[RegistrationViewModel::class]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailText.addTextChangedListener(
            TratometrTextWatcher(
                TratometrTextWatcher.FieldType.Email(binding.emailField, binding.emailText),
                ::updateRegistrationButtonState
            )
        )

        binding.passwordText.addTextChangedListener(
            TratometrTextWatcher(
                TratometrTextWatcher.FieldType.Password(
                    binding.passwordField,
                    binding.passwordText
                ),
                ::updateRegistrationButtonState
            )
        )

        binding.passwordText.addTextChangedListener(
            TratometrTextWatcher(
                TratometrTextWatcher.FieldType.PasswordMatch(
                    binding.passwordText,
                    binding.repeatPasswordField,
                    binding.repeatPasswordText
                ), ::updateRegistrationButtonState
            )
        )

        binding.repeatPasswordText.addTextChangedListener(
            TratometrTextWatcher(
                TratometrTextWatcher.FieldType.PasswordMatch(
                    binding.passwordText,
                    binding.repeatPasswordField,
                    binding.repeatPasswordText
                ),
                ::updateRegistrationButtonState
            )
        )

        binding.checkbox.setOnCheckedChangeListener { checkBox, isChecked ->
            if (isChecked) {
                privacyAccepted = true
                updateRegistrationButtonState()
            } else {
                privacyAccepted = false
                updateRegistrationButtonState()
            }
        }

        binding.registrationButton.setOnClickListener {
            val email = binding.emailText.text.toString()
            val password = binding.passwordText.text.toString()

            viewModel.registration(email, password)
        }

        binding.privacy.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_privacyPolicyFragment)
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        observeRegState()
    }

    private fun observeRegState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.regState.collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.value?.let {
                                viewModel.putTokensToStorage(it)
                                findNavController().navigate(R.id.action_registrationFragment_to_expensesFragment)
                            }
                        }

                        is Resource.Error -> {
                            result.message?.let {
                                showError(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorText.visibility = View.VISIBLE
    }

    private fun updateRegistrationButtonState() {
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()
        val repeatPassword = binding.repeatPasswordText.text.toString()

        val isEmailValid = FieldValidator.isValidEmail(email)
        val isPasswordValid = FieldValidator.isValidPassword(password)
        val arePasswordsMatch = FieldValidator.doPasswordsMatch(password, repeatPassword)

        binding.registrationButton.isEnabled =
            isEmailValid && isPasswordValid && arePasswordsMatch && privacyAccepted
    }
}