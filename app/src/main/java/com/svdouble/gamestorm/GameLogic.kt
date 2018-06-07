package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Color.*
import android.util.Log

data class Cell2D(val x: Int, val y: Int) {
    operator fun plus(n: Int) = Cell2D(x + n, y + n)
    operator fun plus(c: Cell2D) = Cell2D(x + c.x, y + c.y)
    fun revert() = Cell2D(y, x)
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

    var rows: Int = 3
    var columns: Int = 4
    val tField: Array<Array<TPlayer>> = Array(columns) { Array(rows) { TPlayer(-1, -1) } }

    var standartPatterns: ArrayList<Array<Cell2D>> = arrayListOf( // column, row
            arrayOf(Cell2D(-1, 0), Cell2D(0, 0), Cell2D(1, 0)), // right and left
            arrayOf(Cell2D(0, -1), Cell2D(0, 0), Cell2D(0, 1)), // bottom and top
            arrayOf(Cell2D(-1, -1), Cell2D(0, 0), Cell2D(1, 1)), // diagonal
            arrayOf(Cell2D(-1, 1), Cell2D(0, 0), Cell2D(1, -1))) // diagonal

    fun checkField(): Array<Cell2D>? {
        for (row in 0 until rows)
            for (column in 0 until columns)
                check@ for (pattern in standartPatterns) {
                    for (cell in pattern) {
                        val point = cell + Cell2D(column, row)
                        if (tField[column][row].playerId == -1 // cell is not in game
                                || point.x < 0 || point.x >= columns
                                || point.y < 0 || point.y >= rows)
                            continue@check
                        else if (tField[point.x][point.y].playerId != tField[column][row].playerId)
                            continue@check
                    }
                    return Array(pattern.size) {i -> pattern[i] + Cell2D(row + 1, column + 1)}
        }
        return null
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
                if (state == GameState.RESUMED && gameField.tField[event.pos.x][event.pos.y].playerId == -1) {
                    drawEngine.forwardCall(CallDrawChip(event.pos + 1, current_player.chipId))
                    gameField.tField[event.pos.x][event.pos.y](current_player)
                    val checkResult = gameField.checkField()
                    if (checkResult != null) {
                        drawEngine.forwardCall(CallDrawGridCells(checkResult, RED))
                        dispatchEvent(GameEvent(GameEvent.EventType.STOP))
                    }
                    else
                        current_player = nextPlayer()
                }
            }
            GameEvent.EventType.STOP -> {
                state = GameState.STOPPED
            }
            else -> Log.d(TAG, "Unrecognised event")
        }
    }

    private fun nextPlayer()
            = if (current_player == players[0]) players[1] else players[0]
}

