package com.example.hangman

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_login.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        this.findViewById<Button>(R.id.confirm).setOnClickListener{
            val name = this.findViewById<EditText>(R.id.login).text.toString()
            val passwd = this.findViewById<EditText>(R.id.password).text.toString()
            val new_user = UserModel(name, passwd, 0)
            if(name.contains(" ") || passwd.contains(" ")) {
                showIvalidData("Username nor password can't contain spaces.")
                return@setOnClickListener
            }
            if(name == "" || passwd == "") {
                showIvalidData("Username nor password can't be empty.")
                return@setOnClickListener
            }

            val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                val db = UsersDB()
                if (name == "user" || db.readUser(name) != null) {
                    this.showAlreadyTakenDialog()
                } else {
                    db.insertUser(new_user)
                    intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }else{
                UsersDB.showCouldntConnect(this)
            }
        }
    }


    fun showAlreadyTakenDialog(){
        val builder = AlertDialog.Builder(this@RegisterActivity)
        builder.setTitle("Invalid data")
        builder.setMessage("This username is already taken. Try another one.")
        builder.setPositiveButton("OKS") { _, _ ->}
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun showIvalidData(msg : String){
        val builder = AlertDialog.Builder(this@RegisterActivity)
        builder.setTitle("Invalid data")
        builder.setMessage(msg)
        builder.setPositiveButton("OKS") { _, _ ->}
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}