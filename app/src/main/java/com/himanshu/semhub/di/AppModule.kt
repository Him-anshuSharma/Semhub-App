package com.himanshu.semhub.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.room.Room
import com.himanshu.semhub.R
import com.himanshu.semhub.data.local.AppDatabase
import com.himanshu.semhub.data.local.chat.ChatDao
import com.himanshu.semhub.data.local.timetable.TimetableDao
import com.himanshu.semhub.data.remote.ApiService
import com.himanshu.semhub.data.repository.AuthRepository
import com.himanshu.semhub.data.repository.ChatRepository
import com.himanshu.semhub.data.repository.TimetableRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Logs request and response body
    }

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
    fun provideTimetableRepository(apiService: ApiService,timetableDao: TimetableDao): TimetableRepository {
        return TimetableRepository(apiService,timetableDao)
    }

    @Provides
    @Singleton
    fun provideChatRepository(apiService: ApiService,chatDao: ChatDao,timetableDao: TimetableDao): ChatRepository {
        return ChatRepository(apiService,chatDao,timetableDao)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // Change to your FastAPI backend
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(interceptor = logging).build())
            .build()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_db").fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideTimetableDao(db: AppDatabase): TimetableDao = db.timetableDao()

    @Provides
    @Singleton
    fun provideChatDao(db: AppDatabase): ChatDao = db.chatDao()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }


}
