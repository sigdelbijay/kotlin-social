package com.example.kotlin_social.di

import com.example.kotlin_social.repositories.AuthRepository
import com.example.kotlin_social.repositories.DefaultAuthRepository
import com.example.kotlin_social.repositories.DefaultMainRepository
import com.example.kotlin_social.repositories.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object MainModule {

    @ActivityScoped
    @Provides
    fun provideMainRepository() = DefaultMainRepository() as MainRepository
}