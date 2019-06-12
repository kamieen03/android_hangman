package com.example.hangman

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        UsersDB().fetchAndCacheRecords(this).execute()
        Handler().postDelayed({
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }, 2000)
    }
}
