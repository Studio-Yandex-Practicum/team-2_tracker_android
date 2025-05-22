package com.nemislimus.tratometr.authorization.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.authorization.ui.viewmodel.NewPassViewModel
import com.nemislimus.tratometr.common.appComponent
import com.nemislimus.tratometr.common.util.BindingFragment
import com.nemislimus.tratometr.common.util.FieldValidator
import com.nemislimus.tratometr.common.util.TratometrTextWatcher
import com.nemislimus.tratometr.databinding.FragmentNewPassBinding
import javax.inject.Inject

class NewPassFragment : BindingFragment<FragmentNewPassBinding>() {

    @Inject
    lateinit var vmFactory: NewPassViewModel.Factory
    lateinit var viewModel: NewPassViewModel


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewPassBinding {
        return FragmentNewPassBinding.inflate(inflater, container, false)
    }

    override fun onAttach(context: Context) {
        requireActivity().appComponent.inject(this)
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity(), vmFactory)[NewPassViewModel::class]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.passwordText.addTextChangedListener(
            TratometrTextWatcher(
                TratometrTextWatcher.FieldType.Password(
                    binding.passwordField,
                    binding.passwordText
                ), ::updateConfirmButton
            )
        )

        binding.repeatPasswordText.addTextChangedListener(
            TratometrTextWatcher(
                TratometrTextWatcher.FieldType.PasswordMatch(
                    binding.passwordText,
                    binding.repeatPasswordField,
                    binding.repeatPasswordText
                ), ::updateConfirmButton
            )
        )

        binding.confirmButtonButton.setOnClickListener {
            showToast()
            findNavController().navigate(R.id.action_newPassFragment_to_expensesFragment)
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_newPassFragment_to_authorizationFragment)
        }
    }

    private fun updateConfirmButton() {
        val password = binding.passwordText.text.toString()
        val repeatPassword = binding.repeatPasswordText.text.toString()

        val isPasswordValid = FieldValidator.isValidPassword(password)
        val arePasswordsMatch = FieldValidator.doPasswordsMatch(password, repeatPassword)

        binding.confirmButtonButton.isEnabled = isPasswordValid && arePasswordsMatch
    }

    private fun showToast() {
        Toast.makeText(requireContext(), getString(R.string.new_pass_is_set), Toast.LENGTH_LONG)
            .show()
    }
}