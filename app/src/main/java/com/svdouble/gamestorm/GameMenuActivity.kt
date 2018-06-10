package com.svdouble.gamestorm

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_game_menu.*

class GameMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_menu)

        when(intent.getIntExtra(INTENT_ID_KEY, -1)) {
            GAME_TICTACTOE_ID -> {
                val tGame = Games.getInstance(this).games[0] as TGame
                textViewTitle.text = getString(tGame.titleRId)
                textViewDescription.text = getString(tGame.descriptionRId)
                buttonPlay.setOnClickListener { startActivity(Intent(this, CanvasActivity::class.java).putExtra(INTENT_ID_KEY, GAME_TICTACTOE_ID)) }
                buttonSettings.setOnClickListener { startActivity(Intent(this, GameSettingsActivity::class.java).putExtra(INTENT_ID_KEY, GAME_TICTACTOE_ID))}
            }
        }
        buttonBack.setOnClickListener { startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) }
    }
}
