package com.example.hangman


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import java.sql.*
import java.util.*
import android.os.StrictMode
import android.support.v7.app.AlertDialog
import org.json.JSONArray


class UsersDB {
    private var conn: Connection? = null
    private var username = "sql7293888"
    private var password = "YKk6UGZaGd"




    fun readUser(name: String): UserModel? {
        conn = getConnection()
        val stmt = conn!!.prepareStatement("""select * from USERS
                                                WHERE NAME = ?""".trimIndent())
        stmt.setString(1, name)
        val resultset = stmt.executeQuery()
        val exists: Boolean = resultset.first()
        if (!exists){
            resultset.close()
            stmt.close()
            return null
        }

        val passwd = resultset.getString("passwd")
        val total = resultset.getInt("total")
        resultset.close()
        stmt.close()
        return UserModel(name, passwd, total)
    }

    fun insertUser(user: UserModel){
        class insertTask() : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean? {
                _insertUser(user)
                return null
            }
        }
        insertTask().execute()
    }

    private fun _insertUser(user: UserModel) {
        conn = getConnection()
        val stmt = conn!!.prepareStatement("""
            INSERT INTO USERS (NAME, PASSWD, TOTAL)
            VALUES (?, ?, ?)
        """.trimIndent())
        stmt.setString(1, user.name)
        stmt.setString(2, user.passwd)
        stmt.setInt(3, user.total)
        stmt.execute()
        stmt.close()
    }


    fun updateUser(name: String, total: Int){
        class updateTask() : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                _updateUser(name, total)
                return null
            }
        }
        updateTask().execute()
    }


    private fun _updateUser(name: String, total: Int) {
        conn = getConnection()
        val stmt = conn!!.prepareStatement("""
            UPDATE USERS
            SET TOTAL = ?
            WHERE NAME = ?
        """.trimIndent())
        stmt.setInt(1, total)
        stmt.setString(2, name)
        stmt.execute()
        stmt.close()
    }

    inner class fetchAndCacheRecords(context: Context) : AsyncTask<Void, Void, Void>() {
        val ctx = context
        override fun doInBackground(vararg params: Void?): Void? {
            val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                val arr: JSONArray
                try {
                    arr = _fetchTop10()
                }catch (e: Exception) {return null}
                RecordsDB(ctx).saveRecords(arr)
            }
            return null
        }
    }


    @SuppressLint("NewApi")
    private fun _fetchTop10(): JSONArray {
        conn = getConnection()
        val stmt = conn!!.prepareStatement("select * from USERS ORDER BY TOTAL DESC LIMIT 10")
        val resultset = stmt.executeQuery()
        val ret = JSONArray()
        while (resultset.next()) {
            val temp  = Array(2) {""}
            temp[0] = resultset.getString("NAME")
            temp[1] = resultset.getInt("TOTAL").toString()
            ret.put(JSONArray(temp))
        }
        resultset.close()
        stmt.close()
        return ret
    }



    fun getConnection() : Connection?{
        val connectionProps = Properties()
        connectionProps.put("user", username)
        connectionProps.put("password", password)
        Class.forName("com.mysql.jdbc.Driver").newInstance()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        conn = DriverManager.getConnection(
            "jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7293888",
            connectionProps)
        return conn
    }

    companion object {
        @JvmStatic
        fun showCouldntConnect(ctx: Context) {
            val builder = AlertDialog.Builder(ctx)
            builder.setTitle("Connection error")
            builder.setMessage("Could not connect to database :(\nCheck your Internet connection")
            builder.setNeutralButton("BACK"){_,_->}
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }


}