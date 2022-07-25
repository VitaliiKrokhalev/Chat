package com.sibsutis.chat.modules

import android.app.Application
import com.sibsutis.chat.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UserRepositoryModule {

    @Provides
    fun provideUserRepository(application: Application): UserRepository {
        return UserRepository(application)
    }
}