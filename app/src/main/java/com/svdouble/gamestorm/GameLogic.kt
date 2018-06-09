package com.svdouble.gamestorm

import android.content.Context
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/* Base classes */
data class Cell2D(val x: Int, val y: Int) {
    operator fun plus(n: Int) = Cell2D(x + n, y + n)
    operator fun plus(c: Cell2D) = Cell2D(x + c.x, y + c.y)
    fun revert() = Cell2D(y, x)
}

open class BasePlayer(open val playerId: Int)

class GameEvent(val type: Type, val pos: Cell2D = Cell2D(-1, -1)) {
    enum class Type {
        START, STOP, PAUSE, PASS, STEP
    }
}

abstract class BaseGameHandler {
    enum class State {
        INIT, RESUMED, PAUSED, STOPPED
    }

    abstract var state: State
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

data class PropertyData<T : Any>(var currentValue: T, val name: String, val section: String)

class PropertyBounds<T : Any>(private val bounds: Array<out (T) -> Boolean> = arrayOf()) {
    fun checkProperty(value: T): Boolean {
        var testResult = true
        bounds.forEach { if (!it(value)) testResult = false }
        return testResult
    }
}

@Suppress("UNCHECKED_CAST")
class ResourceManager {
    val sections: MutableMap<String, MutableMap<String, Pair<*, PropertyBounds<*>>>> = mutableMapOf()

    fun <T : Any> registerProperty(pData: PropertyData<T>, pBounds: PropertyBounds<T>) {
        val section = sections[pData.section]
        if (section != null && section.containsKey(pData.name))
            throw IllegalArgumentException("Property already exists!")
        if (!pBounds.checkProperty(pData.currentValue))
            throw IllegalArgumentException("Illegal default value!")
        if (section != null)
            section[pData.name] = Pair(pData.currentValue, pBounds)
        else
            sections[pData.section] = mutableMapOf<String, Pair<*, PropertyBounds<*>>>(pData.name to Pair(pData.currentValue, pBounds))
    }

    fun <T : Any> getProperty(pData: PropertyData<T>): T =
            sections.get(pData.section)?.get(pData.name)!!.first as T
}

class PropertyLoader<T : Any>(private val manager: ResourceManager,
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

fun <T : Any> bindResource(propContainer: PropertyContainer,
                           defaultValue: T,
                           name: String,
                           section: String,
                           vararg checkBounds: (T) -> Boolean)
        : PropertyLoader<T> =
        when (defaultValue::class) {
            Int::class, Double::class, String::class ->
                PropertyLoader(propContainer.getResourceManager(), PropertyData(defaultValue, name, section), PropertyBounds(checkBounds))
            else -> throw IllegalArgumentException("Type ${defaultValue::class.java} isn't supported!")
        }

class Games private constructor(private val context: Context) {
    val games: Array<BaseGame> = arrayOf(TGame(context))

    companion object : SingletonHolder<Games, Context>(::Games)
}
