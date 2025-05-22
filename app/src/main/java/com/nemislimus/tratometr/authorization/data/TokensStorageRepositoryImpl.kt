package com.nemislimus.tratometr.authorization.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nemislimus.tratometr.authorization.domain.TokensStorageRepository
import com.nemislimus.tratometr.authorization.domain.models.Tokens
import javax.inject.Inject

class TokensStorageRepositoryImpl @Inject constructor(context: Context) :
    TokensStorageRepository {
    companion object {
        private const val TOKENS_REPOSITORY_KEY = "tokens"
        private const val EMPTY = ""
    }

    private val tokensPrefs = context.getSharedPreferences(TOKENS_REPOSITORY_KEY, MODE_PRIVATE)

    override fun clearTokens() {
        tokensPrefs.edit() { clear() }
    }

    override fun putTokens(tokens: Tokens) {
        val data = tokensToJson(tokens)
        tokensPrefs.edit() { putString(TOKENS_REPOSITORY_KEY, data) }
    }

    override fun getTokens(): Tokens {
        val data = tokensPrefs.getString(TOKENS_REPOSITORY_KEY, EMPTY)
        return tokensFromJson(data)
    }

    private fun tokensToJson(tokens: Tokens): String {
        val gson = Gson()
        return gson.toJson(tokens)
    }

    private fun tokensFromJson(json: String?): Tokens {
        val gson = Gson()
        val type = object : TypeToken<Tokens>() {}.type
        return gson.fromJson(json, type) ?: Tokens(null, null)
    }
}