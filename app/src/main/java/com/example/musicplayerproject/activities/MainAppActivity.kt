package com.example.musicplayerproject.activities


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.musicplayerproject.R
import com.example.musicplayerproject.databinding.ActivityMainAppBinding
import com.example.musicplayerproject.fragments.HomeFragment
import com.example.musicplayerproject.fragments.LibraryFragment
import com.example.musicplayerproject.fragments.SearchFragment
import com.example.musicplayerproject.fragments.SettingFragment
import com.facebook.appevents.suggestedevents.ViewOnClickListener


class MainAppActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mainAppBinding: ActivityMainAppBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Permissions for downloading online medias
        checkPermission()

        mainAppBinding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(mainAppBinding.root)
        supportActionBar?.hide()

        val homeFragment = HomeFragment()

        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        transaction.replace(mainAppBinding.transactionLayout.id, homeFragment)
        transaction.commit()

        handleClickListener()

    }

    private fun handleClickListener() {
        mainAppBinding.buttonHome.setBackgroundColor(resources.getColor(R.color.green_brand))
        mainAppBinding.buttonHome.setOnClickListener(this)
        mainAppBinding.buttonSearch.setOnClickListener(this)
        mainAppBinding.buttonLibrary.setOnClickListener(this)
        mainAppBinding.buttonSetting.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id)
        {
            R.id.button_home -> {
                mainAppBinding.buttonHome.setBackgroundColor(resources.getColor(R.color.green_brand))
                mainAppBinding.buttonSearch.setBackgroundColor(resources.getColor(R.color.dark_grey))
                mainAppBinding.buttonLibrary.setBackgroundColor(resources.getColor(R.color.dark_grey))
                mainAppBinding.buttonSetting.setBackgroundColor(resources.getColor(R.color.dark_grey))
                val homeFragment = HomeFragment()
                replaceFragment(homeFragment)
            }
            R.id.button_search ->{
                mainAppBinding.buttonHome.setBackgroundColor(resources.getColor(R.color.dark_grey))
                mainAppBinding.buttonSearch.setBackgroundColor(resources.getColor(R.color.green_brand))
                mainAppBinding.buttonLibrary.setBackgroundColor(resources.getColor(R.color.dark_grey))
                mainAppBinding.buttonSetting.setBackgroundColor(resources.getColor(R.color.dark_grey))
                val searchFragment = SearchFragment()
                replaceFragment(searchFragment)
            }
            R.id.button_library -> {
                mainAppBinding.buttonHome.setBackgroundColor(resources.getColor(R.color.dark_grey))
                mainAppBinding.buttonSearch.setBackgroundColor(resources.getColor(R.color.dark_grey))
                mainAppBinding.buttonLibrary.setBackgroundColor(resources.getColor(R.color.green_brand))
                mainAppBinding.buttonSetting.setBackgroundColor(resources.getColor(R.color.dark_grey))
                val libraryFragment = LibraryFragment()
                replaceFragment(libraryFragment)
            }
            R.id.button_setting -> {
                mainAppBinding.buttonHome.setBackgroundColor(resources.getColor(R.color.dark_grey))
                mainAppBinding.buttonSearch.setBackgroundColor(resources.getColor(R.color.dark_grey))
                mainAppBinding.buttonLibrary.setBackgroundColor(resources.getColor(R.color.dark_grey))
                mainAppBinding.buttonSetting.setBackgroundColor(resources.getColor(R.color.green_brand))
                val settingFragment = SettingFragment()
                replaceFragment(settingFragment)
            }
        }

    }

    private fun replaceFragment(fragment: Fragment)
    {
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        transaction.replace(mainAppBinding.transactionLayout.id, fragment)
        transaction.commit()
    }

    private fun checkPermission() {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 101)
        } else {
            Log.v("Music", "Permission already granted")
        }
    }
}