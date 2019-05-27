package io.burba.tothecomments.di

import dagger.Module
import dagger.Provides
import io.burba.tothecomments.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Module
class HttpModule {
    @Provides @Singleton fun httpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor(LoggerFactory.getLogger("http.requests"), System::currentTimeMillis))
        .build()
}