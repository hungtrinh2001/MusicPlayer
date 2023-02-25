package com.example.musicplayerproject.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerproject.R
import com.example.musicplayerproject.databinding.ActivitySignUpBinding
import com.example.musicplayerproject.models.data.User
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*


class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpRequest: BeginSignInRequest
    private  lateinit var signUpBinding: ActivitySignUpBinding
    private lateinit var signUpClient: GoogleSignInClient
    private val firebaseDatabase = Firebase.database
    private  lateinit var callbackManager: CallbackManager
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var accessTokenTracker: AccessTokenTracker
    private lateinit var tokeListener: FirebaseAuth.IdTokenListener

    private lateinit var txtEmail: EditText
    private lateinit var txtPassWord: EditText
    private lateinit var txtUserName: EditText


    companion object {
        const val SIGN_UP_GOOGLE_REQUEST_CODE = 123
        const val TAG = "SIGN UP ACTIVITY"
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signUpBinding.root)
        supportActionBar?.hide()

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        txtEmail = signUpBinding.txtSignupEmail
        txtPassWord = signUpBinding.txtSignupPassword
        txtUserName = signUpBinding.txtSignupUsername

        auth = FirebaseAuth.getInstance()
        signUpClient = GoogleSignIn.getClient(this, gso)

        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {
                Log.i(TAG, "Login fail")
            }

            override fun onSuccess(result: LoginResult) {
                Log.i(TAG, "Login successfully")
                firebaseAuthWithFacebook(result.accessToken)
            }
        })

        authStateListener = FirebaseAuth.AuthStateListener {    authen ->
            val user = authen.currentUser
            user?.uid
            if (user != null)
            {
                switchToMainScene()
            }

        }

        tokeListener = FirebaseAuth.IdTokenListener { auth ->
        }

    }

    private fun firebaseAuthWithFacebook(accessToken: AccessToken) {
        var credential = FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Facebook auth successfully", Toast.LENGTH_LONG).show()
                    var user = auth.currentUser
                    var uid = user!!.uid
                    var userName = user!!.displayName
                    var mail = user!!.email
                    var profilePicture = user!!.photoUrl.toString()


                    var usr = User(uid)
                    firebaseDatabase.getReference().child("User").child(uid).setValue(usr)
                    switchToMainScene()
                }
                else
                {
                    Toast.makeText(this, "Facebook auth failed", Toast.LENGTH_LONG).show()
                }

            }

    }

    fun signUpWithEmail(view: View) {
        var valid = (Patterns.EMAIL_ADDRESS.matcher(txtEmail.text.toString()).matches()
                || Patterns.PHONE.matcher(txtEmail.text.toString()).matches())
                && (!txtUserName.text.isEmpty())
        if (valid)
        {
            auth.createUserWithEmailAndPassword(txtEmail.text.toString(), txtPassWord.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var usr = User(task.result.user?.uid.toString(), txtEmail.text.toString())
                        firebaseDatabase.reference.child("User").child(usr.uid).setValue(usr)
                        Toast.makeText(this,"Sign Up successfully", Toast.LENGTH_LONG).show()
                        switchToMainScene()
                    }
                    else
                    {
                        Toast.makeText(this,"Sign Up failed: "+ task.exception?.message, Toast.LENGTH_LONG).show()
                    }

                }
        }
        else
        {
            Toast.makeText(this, "Invalid password or email or username", Toast.LENGTH_LONG).show()
        }

    }

    fun signUpWithGoogle(view: View) {
        var signUpWithGoogleIntent = signUpClient.signInIntent
        startActivityForResult(signUpWithGoogleIntent, SIGN_UP_GOOGLE_REQUEST_CODE)

    }
    fun signUpWithFacebook(view: View) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SIGN_UP_GOOGLE_REQUEST_CODE && resultCode == RESULT_OK)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                e.printStackTrace()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show()
        var credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this, OnCompleteListener {
                if (it.isSuccessful)
                {
                    Log.i(TAG, "Auth successfully")
                    var user = auth.currentUser;
                    var uid = user!!.uid
                    var userName = user!!.displayName
                    var mail = user!!.email
                    var profilePicture = user!!.photoUrl.toString()


                    var usr = User(uid, mail, profilePicture)
                    if (userName!!.isEmpty())
                    {
                        usr.userName = mail!!.substringBeforeLast("@")
                    }
                    else
                    {
                        usr.userName = userName!!
                    }

                    firebaseDatabase.getReference().child("User").child(uid).setValue(usr)
                    switchToMainScene()
                }
                else
                {
                    Log.e(TAG, "Auth fail")
                    Toast.makeText(this, it.exception!!.message, Toast.LENGTH_LONG).show()
                }

            })
    }

    fun switchToMainScene()
    {
        var switchIntent = Intent(this, MainAppActivity::class.java)
        startActivity(switchIntent)
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)

    }

    fun switchToSignIn(view: View) {
        var onSignIn = Intent(this, SignInActivity::class.java)
        startActivity(onSignIn)

    }


}