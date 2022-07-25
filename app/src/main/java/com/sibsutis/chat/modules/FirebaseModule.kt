package com.sibsutis.chat.modules

import com.sibsutis.chat.firebase.FirebaseSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ViewModelComponent::class)
object FirebaseModule {
    @Provides
    fun provideFirebaseSource(): FirebaseSource {
        return FirebaseSource()
    }
}