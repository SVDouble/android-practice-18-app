package com.svdouble.gamestorm

data class Point2D(val x: Double, val y: Double)

open class BasePlayer(val playerId: Int)

abstract class GameField {
    abstract val settings: BaseSettings
}

class GameEvent(val type: EventType, player: BasePlayer, pos: Point2D) {
    enum class EventType {
        START, STOP, PAUSE, PASS, STEP
    }
}

abstract class BaseGameDispatcher {
    enum class GameState {
        INIT, RESUMED, PAUSED, STOPPED
    }
    abstract var state: GameState
    abstract val gameField: GameField
    abstract fun dispatchEvent(event: GameEvent)
}

open class BaseSettings {

    open inner class SItem {}
    data class Section(val header: String, val settings: Array<SItem>)

    var allFields: Array<Section> = arrayOf()
}

open class BaseGame(val gameId: Int, var title: String = "", var thumbResource: Int = -1, var rating: Float = 0f) {

    private lateinit var rules: BaseGameDispatcher
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

class MyGameDispatcher() : BaseGameDispatcher() {
    override var state = GameState.INIT
    override val gameField = MyField()
    override fun dispatchEvent(event: GameEvent) {
        when(event.type) {
            GameEvent.EventType.START -> {
                state = GameState.RESUMED
                //gameField.square_field =
            }
            else -> TODO()
        }
    }
}
