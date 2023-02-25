package com.example.musicplayerproject.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerproject.R
import com.example.musicplayerproject.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class SignInActivity : AppCompatActivity() {
    private lateinit var signInBinding: ActivitySignInBinding
    private lateinit var txtEmail: EditText
    private  lateinit var txtPassWord: EditText
    private lateinit var auth: FirebaseAuth
    private  lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var signInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions

    companion object
    {
        const val SIGN_IN_GOOGLE_REQUEST_CODE = 12
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        signInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(signInBinding.root)

        txtEmail = signInBinding.txtSignInEmail
        txtPassWord = signInBinding.txtSignInPassword

        auth = FirebaseAuth.getInstance()

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(this, gso)

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    fun signInWithEmail(view: View) {
        var valid = (Patterns.EMAIL_ADDRESS.matcher(txtEmail.text.toString()).matches()
                || Patterns.PHONE.matcher(txtEmail.text.toString()).matches())
        if (valid)
        {
            auth.signInWithEmailAndPassword(txtEmail.text.toString(), txtPassWord.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        Toast.makeText(this,"Sign In successfully", Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        Toast.makeText(this,"Sign In failed: "+ task.exception?.message, Toast.LENGTH_LONG).show()
                    }

                }
        }
        else
        {
            Toast.makeText(this, "Invalid password or email or username", Toast.LENGTH_LONG).show()
        }

    }
    fun signInWithGoogle(view: View) {
        var signUpWithGoogleIntent = signInClient.signInIntent
        startActivityForResult(signUpWithGoogleIntent, SIGN_IN_GOOGLE_REQUEST_CODE)

    }
    fun signInWithFacebook(view: View) {

    }
    fun switchToSignUp(view: View) {
        var onSignUp = Intent(this, SignUpActivity::class.java)
        startActivity(onSignUp)
    }

    fun onForgotPassword(view: View) {
        var forgotPasswordIntent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(forgotPasswordIntent)
    }
}