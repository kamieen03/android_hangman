package com.example.hangman

import android.provider.BaseColumns
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray


class RecordsDB(context: Context) : SQLiteOpenHelper(context,
    DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //clear and rebuild
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    @Throws(SQLiteConstraintException::class)
    fun saveRecords(arr: JSONArray): Boolean {
        if(this.readRecords() != null){
            updateRecords(arr)
            return true
        }
        val db = writableDatabase
        val values = ContentValues()
        values.put(RecordsEntry.RECORDS, arr.toString())
        values.put(RecordsEntry.ID, "1")

        db.insert(RecordsEntry.TABLE_NAME, null, values)
        return true
    }


    fun readRecords(): JSONArray? {
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("select * from " + RecordsEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return null
        }

        if (cursor!!.moveToFirst()) {
            return JSONArray(cursor.getString(
                cursor.getColumnIndex(RecordsEntry.RECORDS)
            ))
        }
        cursor.close()
        return null
    }

    private fun updateRecords(arr: JSONArray) {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(RecordsEntry.RECORDS, arr.toString())
        db.update(RecordsEntry.TABLE_NAME, contentValues, "id = ?", arrayOf("1"))
    }


    companion object {
        const val DATABASE_NAME = "Records.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RecordsEntry.TABLE_NAME + " (" +
                    RecordsEntry.ID + " TEXT PRIMARY KEY," + RecordsEntry.RECORDS + " TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + RecordsEntry.TABLE_NAME
    }


    class RecordsEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "recordsTable"
            const val ID = "id"
            const val RECORDS = "records"
        }
    }


}