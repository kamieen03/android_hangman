package com.example.hangman

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_record_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.json.JSONArray
import kotlin.coroutines.CoroutineContext

class RecordListActivity : AppCompatActivity(), CoroutineScope {
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_list)

        back.setOnClickListener{
            intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
        val arr = fetchRecords()
        displayRecordLsit(arr)

    }


    @SuppressLint("NewApi")
    fun fetchRecords(): JSONArray {
        val arr: JSONArray? =  RecordsDB(this).readRecords()
        if(arr != null)
            return arr
        return JSONArray("[]")
    }

    private fun displayRecordLsit(arr: JSONArray){
        var arr2: Array<String> = arrayOf()
        for (i in 0..(arr.length()-1)){
            val item = arr.getJSONArray(i)
            arr2 = arr2.plus((i+1).toString() + ". " + item.getString(0) +
                    ": " + item.getString(1))
        }

        val adapter = ArrayAdapter(this, R.layout.list_item, arr2)
        wyniki.adapter = adapter
    }




}
