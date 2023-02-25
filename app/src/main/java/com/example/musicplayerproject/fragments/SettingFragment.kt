package com.example.musicplayerproject.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicplayerproject.R
import com.example.musicplayerproject.activities.SignUpActivity
import com.example.musicplayerproject.databinding.FragmentSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class SettingFragment: Fragment() {

    private lateinit var  settingBinding: FragmentSettingBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingBinding = FragmentSettingBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database

        settingBinding.signOutButton.setOnClickListener{
            signOut()
        }

        displayUserData()

        return settingBinding.root
    }

    private fun displayUserData() {
        var ref = database.reference.child("User").child(auth.currentUser!!.uid).child("profilePictureURL")
        ref.get().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result.value != null) {
                val pic = task.result.value.toString()
                Log.v("Login", pic)
                Picasso.get().load(R.drawable.random_1)
                    .placeholder(com.facebook.R.drawable.com_facebook_profile_picture_blank_square)
                    .into(settingBinding.profilePicture)
            }
        }

        database.reference.child("User").child(auth.currentUser!!.uid).child("email").get().addOnSuccessListener {
            settingBinding.txtEmail.text = it.value.toString()
        }

    }

    private fun signOut()
    {
        auth.signOut()
        var switchToSignUp = Intent(context, SignUpActivity::class.java)
        startActivity(switchToSignUp)
    }
}