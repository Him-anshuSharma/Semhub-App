package com.himanshu.semhub.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val credentialManager: CredentialManager,
    @Named("webClientId") private val webClientId: String
) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Token expiration buffer (5 minutes in milliseconds)
    private val tokenExpirationBuffer = 5 * 60 * 1000

    // Cache for the last token and its expiration time
    private var cachedIdToken: String? = null
    private var tokenExpirationTime: Long = 0

    fun getCachedToken():String?{
        return cachedIdToken
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun signOut() {
        firebaseAuth.signOut()
        // Clear token cache when signing out
        cachedIdToken = null
        tokenExpirationTime = 0
    }

    suspend fun login(): AuthCredential? {
        val request = getCredentialRequest()
        return try {
            val response = credentialManager.getCredential(context, request)
            getAuthCredential(response.credential)
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Couldn't retrieve user's credentials: ${e.localizedMessage}")
            null
        }
    }

    private fun getCredentialRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(true)
            .setFilterByAuthorizedAccounts(false)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    private fun getAuthCredential(credential: Credential): AuthCredential? {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            return GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        }
        Log.w(TAG, "Credential is not of type Google ID!")
        return null
    }

    suspend fun signInWithFirebase(authCredential: AuthCredential): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            Firebase.auth.signInWithCredential(authCredential)
                .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
                .addOnFailureListener { continuation.resume(Result.failure(it)) }
        }

    /**
     * Get the current authentication token, refreshing if necessary
     * @return The ID token string or null if not authenticated
     */
    private suspend fun getIdToken(forceRefresh: Boolean = false): String? {
        val currentUser = getCurrentUser() ?: return null

        // Check if we need to refresh the token
        if (forceRefresh || isTokenExpired()) {
            try {
                // Force refresh the token
                val tokenResult = currentUser.getIdToken(true).await()
                cachedIdToken = tokenResult.token

                // Calculate expiration time (Firebase tokens typically last 1 hour)
                // We subtract 5 minutes to ensure we refresh before actual expiration
                tokenExpirationTime = Date().time + (tokenResult.expirationTimestamp - tokenExpirationBuffer)

                Log.d(TAG, "Token refreshed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to refresh token: ${e.message}")
                return null
            }
        } else if (cachedIdToken == null) {
            // If we have no cached token but the token isn't expired, get it without forcing refresh
            try {
                val tokenResult = currentUser.getIdToken(false).await()
                cachedIdToken = tokenResult.token
                tokenExpirationTime = Date().time + (tokenResult.expirationTimestamp - tokenExpirationBuffer)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get token: ${e.message}")
                return null
            }
        }

        return cachedIdToken
    }

    /**
     * Check if the current token is expired or about to expire
     * @return true if token is expired or will expire soon, false otherwise
     */
    private fun isTokenExpired(): Boolean {
        val currentTime = Date().time
        return cachedIdToken == null || currentTime >= tokenExpirationTime
    }

    /**
     * Get authorization header with Bearer token
     * @return Authorization header string or null if not authenticated
     */
    suspend fun getAuthorizationHeader(): String? {
        val token = getIdToken() ?: return null
        return "Bearer $token"
    }

    companion object {
        private const val TAG = "AuthRepository"
    }
}
