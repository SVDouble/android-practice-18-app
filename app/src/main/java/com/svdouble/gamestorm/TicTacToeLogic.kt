package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Color
import android.util.Log

/* TicTacToe */
const val GAME_TICTACTOE_ID = 1
val TIcons = arrayOf(R.drawable.t_icon_1, R.drawable.t_icon_2, R.drawable.t_icon_3, R.drawable.t_icon_4)

class TField(private val manager: ResourceManager) : PropertyContainer {

    override fun getResourceManager() = manager
    override fun onPropertiesLock() {
        tField = Array(columns) { Array(rows) { TPlayer("null", -1, -1) } }
    }

    val rows by bindResource(this, 3, "rows", "Field", { it in 2..10 })
    val columns by bindResource(this, 3, "columns", "Field", { it in 2..15 })

    lateinit var tField: Array<Array<TPlayer>>

    private var standardPatterns: ArrayList<Array<Cell2D>> = arrayListOf( // column, row
            arrayOf(Cell2D(-1, 0), Cell2D(0, 0), Cell2D(1, 0)), // right and left
            arrayOf(Cell2D(0, -1), Cell2D(0, 0), Cell2D(0, 1)), // bottom and top
            arrayOf(Cell2D(-1, -1), Cell2D(0, 0), Cell2D(1, 1)), // diagonal
            arrayOf(Cell2D(-1, 1), Cell2D(0, 0), Cell2D(1, -1)), // diagonal
            arrayOf(Cell2D(0, 0), Cell2D(1, 0), Cell2D(0, -1), Cell2D(-1, -1))) // stair

    fun checkField(): Array<Cell2D>? {
        for (row in 0 until rows)
            for (column in 0 until columns)
                check@ for (pattern in standardPatterns) {
                    for (cell in pattern) {
                        val point = cell + Cell2D(column, row)
                        if (tField[column][row].id == "null" // cell is not in game
                                || point.x < 0 || point.x >= columns
                                || point.y < 0 || point.y >= rows)
                            continue@check
                        else if (tField[point.x][point.y].id != tField[column][row].id)
                            continue@check
                    }
                    return Array(pattern.size) { i -> pattern[i] + Cell2D(column + 1, row + 1) }
                }
        return null
    }

    fun reset() {
        manager.detachProperty(PropertyData(0, "rows", "Field"))
        manager.detachProperty(PropertyData(3, "columns", "Field"))
    }
}

data class TPlayer(override var id: String, override var iconId: Int, var chipId: Int, var name: String = "") : BasePlayer(id, iconId) {
    operator fun invoke(p: TPlayer) {
        this.id = p.id
        this.iconId = p.iconId
        this.chipId = p.chipId
    }
}

class TGameHandler(private val game: TGame, context: Context) : BaseGameHandler() {
    override var drawEngine = CellularDrawEngine2D(context, this)
    private val gameField = TField(game.manager)
    override var state = State.INIT
    private lateinit var players: Array<TPlayer>
    private var currentPlayer = 0
    private var total = 0
    private var draw = false
    lateinit var listener: GameListener

    override fun dispatchEvent(event: GameEvent) {
        when (event.type) {
            GameEvent.Type.START -> {
                state = State.RESUMED
                players = game.players.toArray(arrayOf())
                drawEngine.forwardCall(CallDrawBg(Color.GREEN))
                drawEngine.forwardCall(CallDrawGrid(gameField.rows, gameField.columns, Color.BLACK))
                updateInfo()
            }
            GameEvent.Type.STEP -> {
                if (state == State.RESUMED && gameField.tField[event.pos.x][event.pos.y].id == "null") {
                    drawEngine.forwardCall(CallDrawChip(event.pos + 1, players[currentPlayer].chipId))
                    gameField.tField[event.pos.x][event.pos.y](players[currentPlayer])
                    total++
                    val checkResult = gameField.checkField()
                    if (checkResult != null) {
                        drawEngine.forwardCall(CallDrawGridCells(checkResult, Color.RED))
                        dispatchEvent(GameEvent(GameEvent.Type.STOP))
                    } else {
                        currentPlayer = nextPlayer()
                        updateInfo()
                    }
                    if (total == gameField.rows * gameField.columns && state != State.STOPPED) {
                        draw = true
                        dispatchEvent(GameEvent(GameEvent.Type.STOP))
                    }
                }
            }
            GameEvent.Type.STOP -> {
                state = State.STOPPED
                updateInfo()
            }
            else -> Log.d(TAG, "Unrecognised event")
        }
    }

    private fun nextPlayer() = (currentPlayer + 1) % players.size

    fun reset() {
        gameField.reset()
    }

    private fun updateInfo() {
        if (state != State.STOPPED)
            listener.updateCurrentUser(players[currentPlayer].name)
        else if (!draw)
            listener.updateCurrentUser(players[currentPlayer].name + " won!")
        else
            listener.updateCurrentUser("draw!")
    }
}


class TGame(private val context: Context)
    : BaseGame(GAME_TICTACTOE_ID, 5.0, R.string.game_t_title, R.string.game_t_description, R.drawable.tictactoe),
        PropertyContainer {
    val manager = ResourceManager() // should be init. before all bindings
    val playerManager = ResourceManager()
    val players by bindResource(playerManager, this, arrayListOf<TPlayer>(), "players", "game_menu")
    private var handler = TGameHandler(this, context)  // should be init. after manager

    override fun startGame() {
        if (players.size > 0) {
            manager.lockProperties()
            handler.dispatchEvent(GameEvent(GameEvent.Type.START))
        }
    }

    override fun generateGameCard() = GameCard(gameId, context.getString(titleRId), rating, thumbResourceRId)

    fun getDrawEngine() = handler.drawEngine
    fun getState() = handler.state
    fun reset() {
        handler.reset()
        manager.unlockProperties()
        playerManager.unlockProperties()
        handler = TGameHandler(this, context)
    }
    fun setListener(listener: GameListener) {
        handler.listener = listener
    }

    /* Property container */
    override fun getResourceManager() = manager
    override fun onPropertiesLock() {
        playerManager.lockProperties(false)
    }
}
