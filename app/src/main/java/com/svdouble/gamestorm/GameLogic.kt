package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Color.GREEN
import android.util.Log


data class Cell2D(val x: Int, val y: Int)
open class BasePlayer(val playerId: Int)

abstract class GameField {
    abstract val settings: BaseSettings
}

class GameEvent(val type: EventType, val pos: Cell2D = Cell2D(-1, -1)) {
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
    abstract val drawEngine: CellularDrawEngine2D
    abstract fun dispatchEvent(event: GameEvent)
}

open class BaseSettings {

    open inner class SItem
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

class MyGameHandler(context: Context) : BaseGameHandler() {
    override val drawEngine = CellularDrawEngine2D(context, this)
    override var state = GameState.INIT
    override val gameField = MyField()
    override fun dispatchEvent(event: GameEvent) {
        when(event.type) {
            GameEvent.EventType.START -> {
                state = GameState.RESUMED
                drawEngine.forwardCall(CallDrawBg(GREEN))
                drawEngine.forwardCall(CallDrawGrid(5, 4))
            }
            GameEvent.EventType.STEP -> {
                Log.d(TAG, "Draw cross!")
                drawEngine.forwardCall(CallDrawChip(Cell2D(event.pos.x, event.pos.y), 0))
                drawEngine.invalidate()
            }
            else -> Log.d(TAG, "Unrecognised event")
        }
    }
}

