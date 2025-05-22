package com.nemislimus.tratometr.common.util

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TratometrTextWatcher(
    private val fieldType: FieldType,
    private val onValidationChanged: () -> Unit
) : TextWatcher {

    sealed class FieldType {
        data class Email(
            val emailField: TextInputLayout,
            val emailText: TextInputEditText
        ) : FieldType()

        data class Password(
            val passwordField: TextInputLayout,
            val passwordText: TextInputEditText
        ) : FieldType()

        data class PasswordMatch(
            val passwordText: TextInputEditText,
            val repeatPasswordField: TextInputLayout,
            val repeatPasswordText: TextInputEditText
        ) : FieldType()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        when (fieldType) {
            is FieldType.Email -> {
                if (!fieldType.emailText.text.isNullOrEmpty()) {
                    FieldValidator.validateEmail(fieldType.emailField, fieldType.emailText)
                } else {
                    fieldType.emailField.error = null
                    fieldType.emailField.isErrorEnabled = false
                }
            }

            is FieldType.Password -> {
                if (!fieldType.passwordText.text.isNullOrEmpty()) {
                    FieldValidator.validatePassword(fieldType.passwordField, fieldType.passwordText)
                } else {
                    fieldType.passwordField.error = null
                    fieldType.passwordField.isErrorEnabled = false
                }
            }

            is FieldType.PasswordMatch -> {
                FieldValidator.validatePasswordMatch(
                    fieldType.passwordText,
                    fieldType.repeatPasswordField,
                    fieldType.repeatPasswordText
                )
            }
        }
        onValidationChanged()
    }
}