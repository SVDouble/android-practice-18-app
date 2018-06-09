package com.svdouble.gamestorm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

const val TAG = "GameStorm"

class CanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(intent.getIntExtra(INTENT_ID_KEY, -1)) {
            GAME_TICTACTOE_ID -> {
                val tGame = Games.getInstance(this).games[0] as TGame
                if (tGame.getState() == BaseGameHandler.State.INIT)
                    tGame.startGame()
                setContentView(tGame.getDrawEngine())
            }
            else -> setContentView(R.layout.activity_base_canvas)
        }
    }

    override fun onStop() {
        super.onStop()

        when(intent.getIntExtra(INTENT_ID_KEY, -1)) {
            GAME_TICTACTOE_ID -> {
                // detach drawEngine
                setContentView(R.layout.activity_base_canvas)
            }
        }
    }
}

