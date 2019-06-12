package com.example.hangman

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.EditText

class LoginActivity : Activity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        this.findViewById<Button>(R.id.confirm).setOnClickListener{

            val login = this.findViewById<EditText>(R.id.login).text.toString()
            val password = this.findViewById<EditText>(R.id.password).text.toString()

            val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                val usr: UserModel? = UsersDB().readUser(login)
                if (usr != null) {
                    if (usr.passwd == password) this.logIn(usr)
                } else {
                    showInvalidData()
                }
            }else{
                UsersDB.showCouldntConnect(this)
            }

        }

        this.findViewById<Button>(R.id.register).setOnClickListener{
            intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logIn(usr: UserModel){
        val shared = this.getSharedPreferences("com.example.hangman.shared", 0)
        val prevTotal = shared.getInt(usr.name, 0)
        val editor = shared.edit()
        //store current user info
        if(prevTotal <= usr.total)
            editor.putInt(usr.name, usr.total)
        editor.putString("user", usr.name)
        editor.apply()
        intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun showInvalidData(){
        val builder = AlertDialog.Builder(this@LoginActivity)
        builder.setTitle("Invalid data")
        builder.setMessage("Invalid login or password. Try again.")
        builder.setPositiveButton("OKS") { _, _ ->}
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
