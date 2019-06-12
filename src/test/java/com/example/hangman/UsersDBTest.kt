package com.example.hangman
import com.example.hangman.UserModel
import com.example.hangman.UsersDB
import org.junit.Test
import org.junit.Assert.*
class UsersDBTest {
    val db = UsersDB()

    @Test
    fun connectionWorks() {
        assertNotNull(db.getConnection())
    }

    @Test
    fun insertingWorks(){
        val u = UserModel("name", "root", 0)
        db.insertUser(u)
        assertNotNull(db.readUser("name"))
    }
}
