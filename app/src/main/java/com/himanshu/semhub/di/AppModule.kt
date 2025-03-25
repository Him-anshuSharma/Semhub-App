package com.himanshu.semhub.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.himanshu.semhub.R
import com.himanshu.semhub.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCredentialManager(
        @ApplicationContext context: Context
    ): CredentialManager = CredentialManager.create(context)

    @Provides
    @Singleton
    @Named("webClientId")
    fun provideWebClientId(@ApplicationContext context: Context): String =
        context.getString(R.string.default_web_client_id)

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        credentialManager: CredentialManager,
        @Named("webClientId") webClientId: String
    ): AuthRepository = AuthRepository(context, credentialManager, webClientId)
}
