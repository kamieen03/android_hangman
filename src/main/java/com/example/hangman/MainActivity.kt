package com.example.hangman

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var word: Array<Pair<Char, Boolean>>    //letter, guessed
    private var stage: Int = 1
    private lateinit var letters: Array<TextView>            //letters shown to player
    private var total: Int = 0
    private lateinit var user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        readUser()

        readTotal()
        updateTotal(0)
        clearState()
        createButtons()
    }

    private fun readTotal() {
        val shared = this.getSharedPreferences("com.example.hangman.shared", 0)
        this.total = shared.getInt(this.user, 0)
    }

    private fun updateTotal(a: Int) {
        this.total += a
        totalLabel.text = this.total.toString()
        val shared = this.getSharedPreferences("com.example.hangman.shared", 0)
        val editor = shared.edit()
        editor.putInt(this.user, this.total)
        editor.apply()
    }

    private fun readUser() {
        val shared = this.getSharedPreferences("com.example.hangman.shared", 0)
        this.user = shared.getString("user", "")!!
    }

    fun drawRandomWord(): Array<Pair<Char, Boolean>> {
        val lines = applicationContext.resources.openRawResource(R.raw.words).bufferedReader().readLines()
        val word = lines[Random.nextInt(lines.size)]
        return Array(word.length) { i -> Pair(word[i].toUpperCase(), false) }
    }

    private fun createButtons() {
        for (i in 0 until buttons.childCount) {
            buttons.getChildAt(i).setOnClickListener(buttonListener)
        }
    }

    private val buttonListener = View.OnClickListener { button: View ->
        val bb = button as Button
        if (bb.currentTextColor == Color.parseColor("#D36135")) return@OnClickListener   //already tried

        button.setTextColor(Color.parseColor("#D36135"))
        var guessed = false
        for (i in 0 until this.word.size) {
            val pair = this.word[i]
            if (bb.text[0] == pair.first) {
                guessed = true
                this.word[i] = Pair(pair.first, true)
                this.letters[i].visibility = View.VISIBLE
            }

        }
        if (!guessed) intensifyHanging()
        else if (this.word.map { pair -> pair.second }            //rzut na bool
                .reduce { acc, bool -> bool && acc }) {   //iloczyn całej listy
            displayAlert("win")
            updateTotal(computeReward())
            postToDB()
//            clearState()
        }
    }

    private fun intensifyHanging() {
        this.stage++
        val id: Int = when (this.stage) {
            2 -> R.drawable.h2
            3 -> R.drawable.h3
            4 -> R.drawable.h4
            5 -> R.drawable.h5
            6 -> R.drawable.h6
            7 -> R.drawable.h7
            8 -> R.drawable.h8
            else -> R.drawable.h1
        }
        hangman_pic.setImageResource(id)
        if (this.stage == 8) {
            displayAlert("lose")
        }
    }


    private fun computeReward(): Int {
        return this.word.size * (8 - this.stage)
    }

    private fun displayAlert(result: String) {
        val builder = AlertDialog.Builder(this@MainActivity)
        val text: String
        text = if (result == "win") {
            builder.setTitle("You won")
            builder.setMessage("You receive " + computeReward().toString() + " points")
            "OK :)"
        } else {
            builder.setTitle("You lost")
            "OK :("
        }
        builder.setPositiveButton(text) { _, _ ->
            clearState()
            Toast.makeText(applicationContext, "New game initiated", Toast.LENGTH_SHORT).show()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun clearState() {
        this.word = drawRandomWord()
        this.stage = 1
        hangman_pic.setImageResource(R.drawable.h1)
        for (i in 0 until buttons.childCount) {
            val b = buttons.getChildAt(i) as Button
            b.setTextColor(Color.BLACK)
        }
        createLetters(this.word)
    }

    @SuppressLint("NewApi")
    private fun createLetters(word: Array<Pair<Char, Boolean>>) {
        letters_box.removeAllViews()
        this.letters = Array(word.size) { i ->
            val letter = TextView(this)
            letter.text = word[i].first.toString()
            letter.visibility = View.INVISIBLE
            letter.textSize = 20F
            letter.gravity = Gravity.CENTER
            letter.setTextColor(Color.parseColor("#02020B"))
            val underscore = ImageView(this)
            underscore.setImageResource(R.drawable.underscore)
            underscore.adjustViewBounds = true
            underscore.maxWidth = 50
            val dynamicGL = LinearLayout(this)
            dynamicGL.orientation = LinearLayout.VERTICAL
            dynamicGL.addView(letter)
            dynamicGL.addView(underscore)
            dynamicGL.setPadding(0, 0, 30, 0)
            letters_box.addView(dynamicGL)
            letter
        }
    }

    private fun postToDB() {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            UsersDB().updateUser(this.user, this.total)
            UsersDB().fetchAndCacheRecords(this).execute()
        }
    }
}

