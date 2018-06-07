package com.svdouble.gamestorm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

const val TAG = "GameStorm"

class CanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(intent.getIntExtra(INTENT_ID_KEY, -1)) {
            GAME_TICTACTOE_ID -> {
                TGame.getInstance(this).startGame()
                setContentView(TGame.getInstance(this).getDrawEngine())
            }
            else -> setContentView(R.layout.activity_base_canvas)
        }
    }
}

