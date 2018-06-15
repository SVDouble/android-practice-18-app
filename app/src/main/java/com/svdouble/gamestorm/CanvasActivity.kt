package com.svdouble.gamestorm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_base_canvas.*
import org.jetbrains.anko.displayMetrics

const val TAG = "GameStorm"

class CanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(intent.getIntExtra(INTENT_ID_KEY, -1)) {
            GAME_TICTACTOE_ID -> {
                setContentView(R.layout.activity_base_canvas)

                /* Attention: linear layout returns strange width / height */
                val maxH = displayMetrics.heightPixels - bc_header.layoutParams.height - bc_footer.layoutParams.height
                val maxW = displayMetrics.widthPixels
                val maxSide = if (maxH > maxW) maxW else maxH
                bc_content.layoutParams.width = maxSide
                bc_content.layoutParams.height = maxSide

                val tGame = Games.getInstance(bc_content.context).games[0] as TGame
                bc_header_title.text = resources.getString(tGame.titleRId)
                        bc_content.addView(tGame.getDrawEngine())
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

