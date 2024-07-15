package tk.vhhg.todoyandex.di

import javax.inject.Qualifier
import javax.inject.Scope

@Qualifier
annotation class Connectivity

@Qualifier
annotation class DeviceId

@Qualifier
annotation class LastRevisionPreferences

@Scope
annotation class TodoAppScope