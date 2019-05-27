package io.burba.tothecomments.di

import dagger.Component
import io.burba.tothecomments.fake.HostChangingHttpModule
import io.burba.tothecomments.support.RedirectToMockInterceptor
import javax.inject.Singleton

@Component(modules = [AppModule::class, HostChangingHttpModule::class])
@Singleton
interface TestDI : DI {
    fun changeHostInterceptor(): RedirectToMockInterceptor
}