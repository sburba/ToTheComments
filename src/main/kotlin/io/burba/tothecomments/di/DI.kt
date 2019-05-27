package io.burba.tothecomments.di

import dagger.Component
import io.burba.tothecomments.http.App
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, HttpModule::class])
interface DI {
    fun app(): App
}