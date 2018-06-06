package com.svdouble.gamestorm

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

const val DEBUG_LOG_KEY = "GameStormApp"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        Log.d(DEBUG_LOG_KEY, "Starting CanvasActivity!")
        startActivity(Intent(this, CanvasActivity::class.java))
    }

}
