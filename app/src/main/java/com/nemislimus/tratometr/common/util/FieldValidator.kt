package com.nemislimus.tratometr.common.util

import android.content.res.Configuration
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nemislimus.tratometr.R

object FieldValidator {

    const val INVALID_EMAIL = "Некорректный e-mail"
    const val REQUIRED_FIELD = "Обязательное поле!"
    const val NOT_MATCH = "Пароли не совпадают"
    const val SHORT_PASSWORD = "Пароль должен содержать не менее 7 символов"
    const val MIN_PASSWORD_LENGTH = 7

    fun isValidEmail(email: String): Boolean {
        val emailRegexPattern = "^[A-Za-z0-9_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return Regex(emailRegexPattern).matches(email)
    }

    fun validateEmail(
        emailField: TextInputLayout,
        emailText: TextInputEditText
    ) {
        return if (emailText.text.toString().trim().isEmpty()) {
            emailField.error = REQUIRED_FIELD
            setValidColor(emailText, false)

        } else if (!isValidEmail(emailText.text.toString())) {
            emailField.error = INVALID_EMAIL
            setValidColor(emailText, false)

        } else {
            emailField.isErrorEnabled = false
            setValidColor(emailText, true)

        }
    }

    fun validatePassword(
        passwordField: TextInputLayout,
        passwordText: TextInputEditText
    ) {
        return if (passwordText.text.toString().trim().isEmpty()) {
            passwordField.error = REQUIRED_FIELD
            setValidColor(passwordText, false)

        } else if (passwordText.text.toString().length < MIN_PASSWORD_LENGTH) {
            passwordField.error = SHORT_PASSWORD
            setValidColor(passwordText, false)

        } else {
            passwordField.isErrorEnabled = false
            setValidColor(passwordText, true)
        }
    }

    fun validatePasswordMatch(
        passwordText: TextInputEditText,
        repeatPasswordField: TextInputLayout,
        repeatPasswordText: TextInputEditText
    ) {
        val password = passwordText.text.toString()
        val repeatPassword = repeatPasswordText.text.toString()

        return when {
            password.isEmpty() || repeatPassword.isEmpty() -> {
                repeatPasswordField.error = null
                setValidColor(repeatPasswordText, false)
            }

            password != repeatPassword -> {
                repeatPasswordField.error = NOT_MATCH
                setValidColor(repeatPasswordText, false)
            }

            else -> {
                repeatPasswordField.error = null
                setValidColor(repeatPasswordText, true)
            }
        }
    }

    private fun setValidColor(text: TextInputEditText, isValid: Boolean){

        val isDarkTheme = (text.context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val validColor = if (isDarkTheme){
            ContextCompat.getColor(text.context, R.color.dark_text_primary)
        } else {
            ContextCompat.getColor(text.context, R.color.text_primary)
        }

        val errorColor = if (isDarkTheme){
            ContextCompat.getColor(text.context, R.color.dark_error)
        } else {
            ContextCompat.getColor(text.context, R.color.error)
        }

        if (isValid){
            text.setTextColor(validColor)
        } else {
            text.setTextColor(errorColor)
        }
    }


    fun isValidPassword(password: String): Boolean =
        password.length >= MIN_PASSWORD_LENGTH

    fun doPasswordsMatch(password: String, repeatPassword: String): Boolean =
        password == repeatPassword

}