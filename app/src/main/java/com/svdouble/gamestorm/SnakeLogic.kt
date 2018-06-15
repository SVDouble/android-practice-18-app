package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Color.BLUE
import android.graphics.Color.GREEN
import android.media.MediaPlayer

const val GAME_SNAKE_ID = 2

class SGame(private val context: Context)
    : BaseGame(GAME_SNAKE_ID, 6.0, R.string.game_s_title, R.string.game_s_description, R.drawable.snake),
        PropertyContainer {

    lateinit var mp1: MediaPlayer
    lateinit var mp2: MediaPlayer

    lateinit var drawEngine: SnakeDrawEngine2D

    val manager = ResourceManager()

    private val speed by bindResource(this, 150, "Speed", "Base", { it in 100..1000 })
    private val color by bindResource(this, "green", "Color", "Base",
            { it.toLowerCase() in arrayOf("green", "blue")})
    private val size by bindResource(this, 10, "size", "Base", { it in 1..50 })
    private val colap by bindResource(this, 1, "number of apples", "Base", { it in 1..10 })
    override fun startGame() {
        //manager.lockProperties()
        mp1 = MediaPlayer.create(context, R.raw.pac)
        mp2 = MediaPlayer.create(context, R.raw.hell)
        val clr = when(color) {
            "green" -> GREEN
            "blue" -> BLUE
            else -> GREEN
        }
        drawEngine = SnakeDrawEngine2D(context, clr, mp1, mp2, size.toFloat(), colap)
        mp1.start()
        drawEngine.Start(speed)
    }

    override fun generateGameCard() =
            GameCard(gameId, context.getString(titleRId), rating, thumbResourceRId)

    override fun getResourceManager() = manager
    override fun onPropertiesLock() {
    }

    fun stopGame() {
        mp1.stop()
        mp2.stop()
    }
}