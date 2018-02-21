package com.readingtime.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInApi
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by phffeitosa on 21/02/18.
 */
interface AuthContract {

    interface View

    interface Presenter {
        fun fetchApis(context: Context, fragment: FragmentActivity, api: GoogleApiClient.OnConnectionFailedListener)
        fun signOut(resultCallback: () -> Unit = {})
        fun revokeAccess(resultCallback: () -> Unit = {})
        fun handleIntentCallback(data: Intent, activity: Activity, onSuccess: () -> Unit = {}, onError: () -> Unit = {})

        fun getGoogleSignin(): GoogleSignInOptions
        fun getFbAuth(): FirebaseAuth
        fun getGoogleSignInIntent(): Intent

        fun userIsLoggedIn(): Boolean
    }
}