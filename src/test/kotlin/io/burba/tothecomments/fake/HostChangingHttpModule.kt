package io.burba.tothecomments.fake

import dagger.Module
import dagger.Provides
import io.burba.tothecomments.logging.HttpLoggingInterceptor
import io.burba.tothecomments.support.RedirectToMockInterceptor
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Module
class HostChangingHttpModule {
    @Provides @Singleton fun httpClient(mockInterceptor: RedirectToMockInterceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor(LoggerFactory.getLogger("http.requests"), System::currentTimeMillis))
        .addInterceptor(mockInterceptor)
        .build()
}