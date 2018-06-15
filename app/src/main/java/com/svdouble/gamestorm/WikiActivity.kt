package com.svdouble.gamestorm

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_wiki.*

class WikiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wiki)

        aw_content.typeface = Fonts.getInstance(this).neuropol
        when (intent.getIntExtra(INTENT_ID_KEY, -1)) {
            GAME_TICTACTOE_ID -> {
                aw_footer.setOnClickListener {
                    startActivity(Intent(this, GameMenuActivity::class.java).putExtra(INTENT_ID_KEY, GAME_TICTACTOE_ID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }
                aw_content.text = resources.getString(R.string.aw_content_tgame)
            }
            GAME_SNAKE_ID -> {
                aw_footer.setOnClickListener {
                    startActivity(Intent(this, GameMenuActivity::class.java).putExtra(INTENT_ID_KEY, GAME_SNAKE_ID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }
                aw_content.text = resources.getString(R.string.aw_content_sgame)
            }
        }
    }
}
