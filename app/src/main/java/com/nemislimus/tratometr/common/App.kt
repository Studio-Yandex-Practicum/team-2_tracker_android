package com.nemislimus.tratometr.common

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.nemislimus.tratometr.di.AppComponent
import com.nemislimus.tratometr.di.DaggerAppComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class App : Application() {

    private val appScope = CoroutineScope(Dispatchers.IO)
    private var isDarkMode = false

    lateinit var appComponent: AppComponent

    var alarmPermissionDeniedOnCurrentSession = false
    var notificationPermissionDeniedOnCurrentSession = false

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .setContextToComponent(this)
            .build()

        appScope.launch {
            val setDarkModeJob = launch {
                isDarkMode = appComponent.getSettingsRepository().getSettings().isDarkMode
            }
            setDarkModeJob.join()

            withContext(Dispatchers.Main){
                switchTheme()
            }
        }
    }

    override fun onTerminate() {
        appScope.cancel()
        super.onTerminate()
    }

    fun switchTheme(darkThemeEnabled: Boolean = isDarkMode) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}

/** Поле расширения для удобного получения доступа к appComponent
 */
@Suppress("RecursivePropertyAccessor")
val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }

/** Если пользователь не дал разрешения на alarm, больше не запрашивать на этой сессии
 */
@Suppress("RecursivePropertyAccessor")
var Context.alarmPermissionDeniedOnCurrentSession: Boolean
    get() = when (this) {
        is App -> alarmPermissionDeniedOnCurrentSession
        else -> this.applicationContext.alarmPermissionDeniedOnCurrentSession
    }
    set(value) = when (this) {
        is App -> alarmPermissionDeniedOnCurrentSession = value
        else -> this.applicationContext.alarmPermissionDeniedOnCurrentSession = value
    }

/** Если пользователь не дал разрешения на notification, больше не запрашивать на этой сессии
 */
@Suppress("RecursivePropertyAccessor")
var Context.notificationPermissionDeniedOnCurrentSession: Boolean
    get() = when (this) {
        is App -> notificationPermissionDeniedOnCurrentSession
        else -> this.applicationContext.notificationPermissionDeniedOnCurrentSession
    }
    set(value) = when (this) {
        is App -> notificationPermissionDeniedOnCurrentSession = value
        else -> this.applicationContext.notificationPermissionDeniedOnCurrentSession = value
    }