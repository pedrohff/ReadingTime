package com.readingtime.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInApi
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.readingtime.ApplicationContextProvider.Companion.context

/**
 * Created by phffeitosa on 21/02/18.
 */

class AuthPresenter : AuthContract.Presenter{
    private lateinit var fbAuth: FirebaseAuth
    private lateinit var googleApiClient: GoogleApiClient


    override fun fetchApis(context: Context, fragment: FragmentActivity, api: GoogleApiClient.OnConnectionFailedListener) {
        fbAuth = FirebaseAuth.getInstance()
        googleApiClient = GoogleApiClient.Builder(context)
                .enableAutoManage(fragment, api)
                .addApi(Auth.GOOGLE_SIGN_IN_API, getGoogleSignin())
                .build()
    }

    override fun getGoogleSignin(): GoogleSignInOptions {
        val clientId = "927053083700-kv6apu8vre35lvvi4dqjmplvrq6ap7n9.apps.googleusercontent.com"
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .build()
    }

    override fun getFbAuth(): FirebaseAuth {
        return fbAuth
    }

    override fun getGoogleSignInIntent(): Intent {
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
    }

    override fun signOut(resultCallback: () -> Unit) {
        fbAuth.signOut()
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback{resultCallback}
    }

    override fun revokeAccess(resultCallback: () -> Unit) {
        fbAuth.signOut()
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback { resultCallback }
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount, activity: Activity, onSuccess: () -> Unit, onError: () -> Unit) {
        Log.e("FBLOGIN:", "firebaseAuthWithGoogle():" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val user = fbAuth!!.currentUser
                        onSuccess
                    } else {
                        onError
                    }
                }
    }

    override fun handleIntentCallback(data: Intent, activity: Activity, onSuccess: () -> Unit, onError: () -> Unit) {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        if (result.isSuccess) {
            // Google Sign In was successful, authenticate with Firebase
            val account = result.signInAccount
            firebaseAuthWithGoogle(account!!, activity, onSuccess, onError)
        } else {
            // Google Sign In failed, update UI appropriately
            onError
        }
    }

    override fun userIsLoggedIn(): Boolean {
        return false
        TODO("Validate if user is in, either on firebase or localdb")
    }

}