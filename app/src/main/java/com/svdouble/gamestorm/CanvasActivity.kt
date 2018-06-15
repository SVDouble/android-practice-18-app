package com.svdouble.gamestorm

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.util.Log
import kotlinx.android.synthetic.main.activity_base_canvas.*
import org.jetbrains.anko.displayMetrics

const val TAG = "GameStorm"

interface GameListener {
    fun updateCurrentUser(name: String)
}
class CanvasActivity : AppCompatActivity(), GameListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_base_canvas)
        bc_header_title.typeface = Fonts.getInstance(this).quicksand
        bc_header_menu.setOnClickListener {
            val popup = PopupMenu(this, it)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.menu_tgame, popup.menu)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.mt_action_reset -> {
                        Log.d(TAG, "Reset game!")
                        val tGame = Games.getInstance(this).games[0] as TGame
                        tGame.reset()
                        startActivity(Intent(this, GameMenuActivity::class.java).putExtra(INTENT_ID_KEY, GAME_TICTACTOE_ID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    }
                    else -> return@setOnMenuItemClickListener false
                }
                return@setOnMenuItemClickListener true
            }
            popup.show()
        }

        when(intent.getIntExtra(INTENT_ID_KEY, -1)) {
            GAME_TICTACTOE_ID -> {
                /* Attention: linear layout returns strange width / height */
                val maxH = displayMetrics.heightPixels - bc_header.layoutParams.height - bc_info.layoutParams.height
                val maxW = displayMetrics.widthPixels
                val maxSide = if (maxH > maxW) maxW else maxH
                bc_content.layoutParams.width = maxSide
                bc_content.layoutParams.height = maxSide

                val tGame = Games.getInstance(bc_content.context).games[0] as TGame
                tGame.setListener(this)
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

    override fun updateCurrentUser(name: String) {
        bc_info_move.text = resources.getString(R.string.bc_info_move).format(name)
    }
}

