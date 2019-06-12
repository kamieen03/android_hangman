package com.example.hangman

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        newGame.setOnClickListener(launchNewGame)
        hallOfFame.setOnClickListener(showRecords)
        scoring.setOnClickListener(showScoring)
        logOut.setOnClickListener(logUserOut)
    }

    val launchNewGame =  View.OnClickListener {
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    val showRecords = View.OnClickListener {
        intent = Intent(this, RecordListActivity::class.java)
        startActivity(intent)
    }

    val showScoring = View.OnClickListener {
        intent = Intent(this, ScoringActivity::class.java)
        startActivity(intent)
    }

    val logUserOut = View.OnClickListener {
        val shared = this.getSharedPreferences("com.example.hangman.shared", 0)
        val editor = shared.edit()
        editor.putString("user", "")
        editor.apply()
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}