package com.hkrtbse2.cab_hkr.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.hkrtbse2.cab_hkr.data.UserPreferencesRepository
import com.hkrtbse2.cab_hkr.data.remote.CabApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpSendPipeline
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import kotlin.io.encoding.ExperimentalEncodingApi

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("commons")
@Module
@InstallIn(SingletonComponent::class)
object Common {
    @Singleton
    @OptIn(ExperimentalEncodingApi::class)
    @Provides
    fun providesHttpClient(): HttpClient {
        return HttpClient {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }.apply {
            this.sendPipeline.intercept(HttpSendPipeline.State) {
                // Logic for Auth ...

                context.headers.append("Authorization", "some hash here")
            }
        }
    }

    @Singleton
    @Provides
    fun providesCabApiService(
        scope: CoroutineScope,
        userPrefRepo: UserPreferencesRepository,
        client: HttpClient
    ): CabApiService {
        return CabApiService(scope, userPrefRepo, client)
    }

    @Singleton
    @Provides
    fun providesUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository {
        return UserPreferencesRepository(context.dataStore)
    }

    @Singleton
    @Provides
    fun providesDefaultCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}