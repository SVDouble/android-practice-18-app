package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Color.BLACK
import android.graphics.Color.GREEN
import android.util.Log

data class Cell2D(val x: Int, val y: Int) {
    operator fun plus(n: Int) = Cell2D(x + n, y + n)
}
open class BasePlayer(open val playerId: Int)

abstract class GameField {
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

open class BaseGame(val gameId: Int, var title: String = "", var thumbResource: Int = -1, var rating: Float = 0f) {

    private lateinit var rules: BaseGameHandler
    private lateinit var players: Array<BasePlayer>

    fun loadFromDatabase() {
        TODO()
    }
}

/* TicTacToe */

class TField : GameField() {

    var rows: Int = 6
    var columns: Int = 5
    val t_field: Array<Array<TPlayer>> = Array(columns) { Array(rows) { TPlayer(-1, -1) } }
    fun checkField(): Boolean {
        return false
    }
}

data class TPlayer(override var playerId: Int, var chipId: Int) : BasePlayer(playerId) {
    operator fun invoke(p: TPlayer) {
        this.playerId = p.playerId
        this.chipId = p.chipId
    }
}

class TGameHandler(context: Context) : BaseGameHandler() {
    override val drawEngine = CellularDrawEngine2D(context, this)
    override val gameField = TField()
    override var state = GameState.INIT
    val players = arrayOf(TPlayer(0, 0), TPlayer(1, 1))
    var current_player = players[0]

    override fun dispatchEvent(event: GameEvent) {
        when(event.type) {
            GameEvent.EventType.START -> {
                state = GameState.RESUMED
                drawEngine.forwardCall(CallDrawBg(GREEN))
                drawEngine.forwardCall(CallDrawGrid(gameField.rows, gameField.columns, BLACK))
            }
            GameEvent.EventType.STEP -> {
                if (gameField.t_field[event.pos.x][event.pos.y] == TPlayer(-1, -1)) {
                    drawEngine.forwardCall(CallDrawChip(event.pos + 1, current_player.chipId))
                    gameField.t_field[event.pos.x][event.pos.y](current_player)
                    if (gameField.checkField())
                        dispatchEvent(GameEvent(GameEvent.EventType.STOP))
                    else
                        current_player = nextPlayer()
                }
            }
            else -> Log.d(TAG, "Unrecognised event")
        }
    }

    private fun nextPlayer()
            = if (current_player == players[0]) players[1] else players[0]
}

