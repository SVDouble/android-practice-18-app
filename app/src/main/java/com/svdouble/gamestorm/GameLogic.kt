package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Color.*
import android.util.Log
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

const val GAME_TICTACTOE_ID = 1


/* Base classes */
data class Cell2D(val x: Int, val y: Int) {
    operator fun plus(n: Int) = Cell2D(x + n, y + n)
    operator fun plus(c: Cell2D) = Cell2D(x + c.x, y + c.y)
    fun revert() = Cell2D(y, x)
}

open class BasePlayer(open val playerId: Int)

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
    abstract val drawEngine: CellularDrawEngine2D
    abstract fun dispatchEvent(event: GameEvent)
}

abstract class BaseGame(val gameId: Int, var rating: Double = 0.0, var titleRId: Int = -1, var descriptionRId: Int = -1, var thumbResourceRId: Int = -1) {
    abstract fun startGame()
    abstract fun generateGameCard(): GameCard
}


/* Settings */
interface PropertyContainer {
    fun getResourceManager(): ResourceManager
}

data class PropertyData<T>(var currentValue: T, val name: String, val section: String)

class PropertyBounds<T> {
    fun <T> checkProperty(value: T): Boolean {
        return true
    }
}

@Suppress("UNCHECKED_CAST")
class ResourceManager {
    val sections: MutableMap<String, MutableMap<String, Pair<*, PropertyBounds<*>>>> = mutableMapOf()

    fun <T> registerProperty(pData: PropertyData<T>, pBounds: PropertyBounds<T>) {
        val section = sections[pData.section]
        if (section != null && section.containsKey(pData.name))
            throw IllegalArgumentException("Property already exists!")
        pBounds.checkProperty(pData.currentValue)
        if (section != null)
            section[pData.name] = Pair(pData.currentValue, pBounds)
        else
            sections[pData.section] = mutableMapOf<String, Pair<*, PropertyBounds<*>>>(pData.name to Pair(pData.currentValue, pBounds))
    }

    fun <T> getProperty(pData: PropertyData<T>): T =
            sections.get(pData.section)?.get(pData.name)!!.first as T
}

class PropertyLoader<T>(private val manager: ResourceManager,
                        private val pData: PropertyData<T>,
                        private val pBounds: PropertyBounds<T> = PropertyBounds()) {

    inner class PropertyDelegate : ReadOnlyProperty<PropertyContainer, T> {
        init {
            manager.registerProperty(pData, pBounds)
        }

        override fun getValue(thisRef: PropertyContainer, property: KProperty<*>): T =
                manager.getProperty(pData)
    }

    operator fun provideDelegate(
            thisRef: PropertyContainer,
            property: KProperty<*>
    ): ReadOnlyProperty<PropertyContainer, T> {
        assert(pBounds.checkProperty(pData.currentValue))
        return PropertyDelegate()
    }
}

fun <T> bindResource(propContainer: PropertyContainer, defaultValue: T, name: String, section: String): PropertyLoader<T> {
    return PropertyLoader(propContainer.getResourceManager(), PropertyData(defaultValue, name, section))
}

/* TicTacToe */

class TField(private val manager: ResourceManager) : PropertyContainer {

    override fun getResourceManager() = manager

    val rows by bindResource(this, 3, "rows", "field")
    val columns by bindResource(this, 3, "columns", "field")

    val tField: Array<Array<TPlayer>> = Array(columns) { Array(rows) { TPlayer(-1, -1) } }

    private var standardPatterns: ArrayList<Array<Cell2D>> = arrayListOf( // column, row
            arrayOf(Cell2D(-1, 0), Cell2D(0, 0), Cell2D(1, 0)), // right and left
            arrayOf(Cell2D(0, -1), Cell2D(0, 0), Cell2D(0, 1)), // bottom and top
            arrayOf(Cell2D(-1, -1), Cell2D(0, 0), Cell2D(1, 1)), // diagonal
            arrayOf(Cell2D(-1, 1), Cell2D(0, 0), Cell2D(1, -1))) // diagonal

    fun checkField(): Array<Cell2D>? {
        for (row in 0 until rows)
            for (column in 0 until columns)
                check@ for (pattern in standardPatterns) {
                    for (cell in pattern) {
                        val point = cell + Cell2D(column, row)
                        if (tField[column][row].playerId == -1 // cell is not in game
                                || point.x < 0 || point.x >= columns
                                || point.y < 0 || point.y >= rows)
                            continue@check
                        else if (tField[point.x][point.y].playerId != tField[column][row].playerId)
                            continue@check
                    }
                    return Array(pattern.size) { i -> pattern[i] + Cell2D(column + 1, row + 1) }
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

class TGameHandler(private val game: TGame, context: Context) : BaseGameHandler() {
    override val drawEngine = CellularDrawEngine2D(context, this)
    private val gameField = TField(game.manager)
    override var state = GameState.INIT
    private lateinit var players: Array<TPlayer>
    private var currentPlayer = 0

    override fun dispatchEvent(event: GameEvent) {
        when (event.type) {
            GameEvent.EventType.START -> {
                state = GameState.RESUMED
                players = game.players
                drawEngine.forwardCall(CallDrawBg(GREEN))
                drawEngine.forwardCall(CallDrawGrid(gameField.rows, gameField.columns, BLACK))
            }
            GameEvent.EventType.STEP -> {
                if (state == GameState.RESUMED && gameField.tField[event.pos.x][event.pos.y].playerId == -1) {
                    drawEngine.forwardCall(CallDrawChip(event.pos + 1, players[currentPlayer].chipId))
                    gameField.tField[event.pos.x][event.pos.y](players[currentPlayer])
                    val checkResult = gameField.checkField()
                    if (checkResult != null) {
                        drawEngine.forwardCall(CallDrawGridCells(checkResult, RED))
                        dispatchEvent(GameEvent(GameEvent.EventType.STOP))
                    } else
                        currentPlayer = nextPlayer()
                }
            }
            GameEvent.EventType.STOP -> {
                state = GameState.STOPPED
            }
            else -> Log.d(TAG, "Unrecognised event")
        }
    }

    private fun nextPlayer() = (currentPlayer + 1) % players.size
}


class TGame private constructor(private val context: Context)
    : BaseGame(GAME_TICTACTOE_ID, 5.0, R.string.game_t_title, R.string.game_t_description, R.drawable.ic_launcher_foreground) {
    val manager = ResourceManager()
    var players = arrayOf(TPlayer(1, 0), TPlayer(2, 1)) // Hardcore mode: same chips
    private val handler by lazy { TGameHandler(this, context) }

    override fun startGame() {
        handler.dispatchEvent(GameEvent(GameEvent.EventType.START))
    }

    override fun generateGameCard() = GameCard(gameId, context.getString(titleRId), rating, thumbResourceRId)

    fun getDrawEngine() = handler.drawEngine
    fun getState() = handler.state

    companion object : SingletonHolder<TGame, Context>(::TGame)
}
