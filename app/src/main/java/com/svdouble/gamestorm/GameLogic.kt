package com.svdouble.gamestorm

import android.content.Context
import android.util.Log
import android.view.View


data class Cell2D(val x: Int, val y: Int)
open class BasePlayer(val playerId: Int)

abstract class GameField {
    abstract val settings: BaseSettings
}

class GameEvent(val type: EventType, val player: BasePlayer, val pos: Cell2D) {
    enum class EventType {
        START, STOP, PAUSE, PASS, STEP
    }
}

abstract class BaseGameHandler {
    enum class GameState {
        INIT, RESUMED, PAUSED, STOPPED
    }
    abstract var state: GameState
    abstract val gameField: GameField
    abstract val drawEngine: DrawEngine2D
    abstract fun dispatchEvent(event: GameEvent)
}

open class BaseSettings {

    open inner class SItem {}
    data class Section(val header: String, val settings: Array<SItem>)

    var allFields: Array<Section> = arrayOf()
}

open class BaseGame(val gameId: Int, var title: String = "", var thumbResource: Int = -1, var rating: Float = 0f) {

    private lateinit var rules: BaseGameHandler
    private lateinit var players: Array<BasePlayer>

    fun loadFromDatabase() {
        TODO()
    }
}

/* TicTacToe */

class MyField : GameField() {
    override val settings = BaseSettings()
    var square_field: Array<Array<MyPlayer>> = arrayOf()
    var rows: Int = 3
    var columns: Int = 3
}

class MyPlayer(playerId: Int, val chipId: Int) : BasePlayer(playerId)

class MyGameHandler(val context: Context) : BaseGameHandler() {
    override val drawEngine = DrawEngine2D(context, this)
    override var state = GameState.INIT
    override val gameField = MyField()
    override fun dispatchEvent(event: GameEvent) {
        when(event.type) {
            GameEvent.EventType.START -> {
                state = GameState.RESUMED
                Log.d(TAG, "Draw grid!")
                drawEngine.storage.drawCalls.add(CallDrawGrid(5, 4, drawEngine))
            }
            GameEvent.EventType.STEP -> {
                Log.d(TAG, "Draw cross!")
                drawEngine.storage.drawCalls.add(CallDrawChip(Cell2D(event.pos.x, event.pos.y), 0))
                drawEngine.invalidate()
            }
            else -> Log.d(TAG, "Unrecognised event")
        }
    }
}

