package com.example.musicplayerproject.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicplayerproject.databinding.LoadingBinding

class LoadingScreen : Fragment() {
    private lateinit var loadingScreenBinding: LoadingBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadingScreenBinding = LoadingBinding.inflate(inflater, container, false)
        loadingScreenBinding.loadingLayout.setOnTouchListener { _, _ -> true }
        return loadingScreenBinding.root
    }
}