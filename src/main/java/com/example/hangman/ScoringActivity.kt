package com.example.hangman

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_scoring.*

class ScoringActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoring)

        val rules = "Upon winning a game, user receives number of points equal to word's length times " +
                "unactivated parts of hangman.\n" +
                "PTS = Length ✕ Unactive"
        scoring_rules.text = rules
        scoring_rules.setTextColor(Color.parseColor("#02020B"))
    }


}
