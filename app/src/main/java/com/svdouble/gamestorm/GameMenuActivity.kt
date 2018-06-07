package com.svdouble.gamestorm

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_game_menu.*

class GameMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_menu)

        buttonSettings.setOnClickListener { startActivity(Intent(this, GameSettingsActivity::class.java))}
        buttonBack.setOnClickListener { startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) }
        buttonPlay.setOnClickListener { startActivity(Intent(this, CanvasActivity::class.java))}
    }
}
