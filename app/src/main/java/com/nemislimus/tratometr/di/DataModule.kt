package com.nemislimus.tratometr.di

import android.content.Context
import com.nemislimus.tratometr.authorization.data.RetrofitNetworkClient
import com.nemislimus.tratometr.authorization.data.network.NetworkClient
import com.nemislimus.tratometr.expenses.data.database.DBConverter
import com.nemislimus.tratometr.expenses.data.database.DBHelper
import com.nemislimus.tratometr.settings.data.storage.SettingsStorage
import com.nemislimus.tratometr.settings.data.storage.impl.SharedPrefsSettingsStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    // DATABASE SECTION
    @Singleton
    @Provides
    fun provideDataBaseHelper(context: Context): DBHelper {
        return DBHelper(context)
    }

    @Singleton
    @Provides
    fun provideDataBaseConverter(context: Context): DBConverter = DBConverter(context)

    // NETWORK SECTION
    @Singleton
    @Provides
    fun provideNetworkClient(context: Context): NetworkClient {
        return RetrofitNetworkClient(context)
    }

    // SETTINGS SECTION
    @Singleton
    @Provides
    fun provideSettingStorage(context: Context): SettingsStorage {
        return SharedPrefsSettingsStorage(context)
    }

}