package com.svdouble.gamestorm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_base_canvas.*

const val TAG = "GameStorm"

class CanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(intent.getIntExtra(INTENT_ID_KEY, -1)) {
            GAME_TICTACTOE_ID -> {
                setContentView(R.layout.activity_base_canvas)
                val tGame = Games.getInstance(bc_content.context).games[0] as TGame
                bc_header_title.text = resources.getString(tGame.titleRId)
                        bc_content.addView(tGame.getDrawEngine())
                if (bc_content.layoutParams.width > bc_content.layoutParams.height)
                    bc_content.layoutParams.width = bc_content.layoutParams.height
                else
                    bc_content.layoutParams.height = bc_content.layoutParams.width
                if (tGame.getState() == BaseGameHandler.State.INIT)
                    tGame.startGame()
            }
            else -> setContentView(R.layout.almost_empty_layout)
        }
    }

    override fun onStop() {
        super.onStop()

        when(intent.getIntExtra(INTENT_ID_KEY, -1)) {
            GAME_TICTACTOE_ID -> {
                bc_content.removeAllViews()
            }
        }
    }
}

