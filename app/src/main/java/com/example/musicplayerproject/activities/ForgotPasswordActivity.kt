package com.example.musicplayerproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.musicplayerproject.R
import com.example.musicplayerproject.databinding.ActivityForgotPasswordBinding
import com.example.musicplayerproject.models.data.User
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    private lateinit var txtEmail: EditText

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        txtEmail = binding.textRestoreEmail
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
    }

    fun onRestorePassword(view: View)
    {
        var valid = (Patterns.EMAIL_ADDRESS.matcher(txtEmail.text.toString()).matches()
                || Patterns.PHONE.matcher(txtEmail.text.toString()).matches())
        if (valid)
        {
            auth.sendPasswordResetEmail(txtEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sent email restore successf", Toast.LENGTH_LONG)
                            .show()
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this, "Any error occur", Toast.LENGTH_LONG)
                            .show()
                    }
                }
        }
        else
        {
            Toast.makeText(this, "Invalid password or email or username", Toast.LENGTH_LONG).show()
        }
    }
}