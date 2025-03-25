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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
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

    companion object {
        private const val TAG = "GoogleActivity"
    }
}
