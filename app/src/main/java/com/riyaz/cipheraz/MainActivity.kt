package com.riyaz.cipheraz

import android.app.ActionBar
import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.riyaz.cipheraz.databinding.ActivityMainBinding
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        auth = Firebase.auth

        if(auth.currentUser == null){
            gotToSignInActivity()
        }

        setSupportActionBar(binding.toolbar)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun gotToSignInActivity() {
        startActivity(Intent(this@MainActivity, SignInActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.check_integrity -> {
                gotToCheckIntegrityActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun gotToCheckIntegrityActivity() {
        startActivity(Intent(this@MainActivity, IntegrityCheckActivity::class.java))
    }
}
