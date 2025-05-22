package com.nemislimus.tratometr.authorization.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.authorization.domain.models.Resource
import com.nemislimus.tratometr.authorization.ui.viewmodel.AuthorizationViewModel
import com.nemislimus.tratometr.common.appComponent
import com.nemislimus.tratometr.common.util.BindingFragment
import com.nemislimus.tratometr.common.util.FieldValidator
import com.nemislimus.tratometr.common.util.TratometrTextWatcher
import com.nemislimus.tratometr.databinding.FragmentAuthorizationBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthorizationFragment : BindingFragment<FragmentAuthorizationBinding>() {

    @Inject
    lateinit var vmFactory: AuthorizationViewModel.Factory
    private lateinit var viewModel: AuthorizationViewModel

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAuthorizationBinding {
        return FragmentAuthorizationBinding.inflate(inflater, container, false)
    }

    override fun onAttach(context: Context) {
        requireActivity().appComponent.inject(this)
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity(), vmFactory)[AuthorizationViewModel::class]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailText.addTextChangedListener(
            TratometrTextWatcher(
                TratometrTextWatcher.FieldType.Email(binding.emailField, binding.emailText),
                ::updateLoginButtonState
            )
        )

        binding.passwordText.addTextChangedListener(
            TratometrTextWatcher(
                TratometrTextWatcher.FieldType.Password(
                    binding.passwordField,
                    binding.passwordText
                ),
                ::updateLoginButtonState
            )
        )

        binding.createAccount.setOnClickListener {
            findNavController().navigate(R.id.action_authorizationFragment_to_registrationFragment)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailText.text.toString()
            val password = binding.passwordText.text.toString()

            viewModel.authorize(email, password)

        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_authorizationFragment_to_passRecoveryFragment)
        }

        observeAuthState()
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.value?.let {
                                viewModel.putTokensToStorage(result.value)
                                findNavController().navigate(R.id.action_authorizationFragment_to_expensesFragment)
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
        binding.emailText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.error
            )
        )
        binding.emailField.error = message
    }

    private fun updateLoginButtonState() {
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        val isEmailValid = FieldValidator.isValidEmail(email)
        val isPasswordValid = FieldValidator.isValidPassword(password)

        binding.loginButton.isEnabled =
            isEmailValid && isPasswordValid
    }
}