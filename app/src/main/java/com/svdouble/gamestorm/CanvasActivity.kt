package com.svdouble.gamestorm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

const val TAG = "GameStorm"

class CanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameHandler = MyGameHandler(this)
        gameHandler.dispatchEvent(GameEvent(GameEvent.EventType.START))
        setContentView(gameHandler.drawEngine)
    }
}

