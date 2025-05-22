package com.nemislimus.tratometr.di

import android.content.Context
import com.nemislimus.tratometr.analytics.ui.fragment.AnalyticsFragment
import com.nemislimus.tratometr.settings.domain.api.SettingsRepository
import com.nemislimus.tratometr.settings.ui.fragment.SettingsFragment
import com.nemislimus.tratometr.authorization.ui.fragment.AuthorizationFragment
import com.nemislimus.tratometr.authorization.ui.fragment.NewPassFragment
import com.nemislimus.tratometr.authorization.ui.fragment.PassRecoveryFragment
import com.nemislimus.tratometr.authorization.ui.fragment.RegistrationFragment
import com.nemislimus.tratometr.authorization.ui.fragment.SplashFragment
import com.nemislimus.tratometr.expenses.ui.fragment.CreateCategoryFragment
import com.nemislimus.tratometr.expenses.ui.fragment.CreateExpenseFragment
import com.nemislimus.tratometr.expenses.ui.fragment.ExpensesFragment
import com.nemislimus.tratometr.expenses.ui.fragment.SelectCategoryFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, DomainModule::class])
interface AppComponent {
    fun inject(fragment: SplashFragment)
    fun inject(fragment: AuthorizationFragment)
    fun inject(fragment: RegistrationFragment)
    fun inject(fragment: CreateExpenseFragment)
    fun inject(fragment: ExpensesFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: CreateCategoryFragment)
    fun inject(fragment: SelectCategoryFragment)
    fun inject(fragment: AnalyticsFragment)
    fun inject(fragment: PassRecoveryFragment)
    fun inject(fragment: NewPassFragment)

    fun getSettingsRepository(): SettingsRepository

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun setContextToComponent(context: Context): Builder
        fun build(): AppComponent
    }

}