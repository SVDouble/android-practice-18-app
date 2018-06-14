package com.svdouble.gamestorm

import android.content.Context

const val GAME_SNAKE_ID = 2

class SGame(private val context: Context)
    : BaseGame(GAME_SNAKE_ID, 6.0, R.string.game_s_title, R.string.game_s_description, R.drawable.snake3) {

    override fun startGame() {
    }

    override fun generateGameCard() =
            GameCard(gameId, context.getString(titleRId), rating, thumbResourceRId)
}