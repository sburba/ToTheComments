package io.burba.tothecomments.di

import dagger.Module
import dagger.Provides
import io.burba.tothecomments.fake.FakeHnApi
import io.burba.tothecomments.fake.FakeMicrolinkApi
import io.burba.tothecomments.source.meta.MicrolinkApi
import io.burba.tothecomments.source.post.HnApi

@Module
class FakeSourcesModule {
    @Provides fun hnApi(): HnApi = FakeHnApi()
    @Provides fun microlinkApi(): MicrolinkApi = FakeMicrolinkApi()
}