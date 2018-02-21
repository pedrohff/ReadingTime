package com.readingtime.ui.auth

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.readingtime.R
import com.readingtime.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_auth.*


class AuthActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, AuthContract.View  {

    private val presenter = AuthPresenter()
    private var RC_SIGN_IN = 1234


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        presenter.fetchApis(this, this, this)

        btGoogleSignIn.setOnClickListener { signIn() }
    }

    public override fun onStart() {
        super.onStart()
        if(presenter.userIsLoggedIn()) {
            goToMainActivity()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            presenter.handleIntentCallback(data, this, {
                goToMainActivity()
            }, {
                Toast.makeText(this, "Could not login", Toast.LENGTH_SHORT) //TODO strings
            })
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT) //TODO strings
    }


    private fun signIn() {
        val signInIntent = presenter.getGoogleSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
