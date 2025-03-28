package com.himanshu.semhub.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.himanshu.semhub.R
import com.himanshu.semhub.data.remote.ApiService
import com.himanshu.semhub.data.repository.AuthRepository
import com.himanshu.semhub.data.repository.TimetableRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // Change to your FastAPI backend
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTimetableRepository(apiService: ApiService): TimetableRepository {
        return TimetableRepository(apiService)
    }

}
