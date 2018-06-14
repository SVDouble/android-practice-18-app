package com.svdouble.gamestorm

import android.content.Context
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/* Base classes */
data class Cell2D(val x: Int, val y: Int) {
    operator fun plus(n: Int) = Cell2D(x + n, y + n)
    operator fun plus(c: Cell2D) = Cell2D(x + c.x, y + c.y)
    fun revert() = Cell2D(y, x)
}

open class BasePlayer(open var id: String, open var iconId: Int) {
    companion object {
        private const val source = "abcdefghijklmnopqrstuvwxyz0123456789"
        fun generatePlayerId() : String =
                (1..6).map { source[Random().nextInt(source.length)] }
                        .joinToString("")
    }
}

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
    // Attention: one PropertyContainer may have multiple resource managers
    fun getResourceManager(): ResourceManager
    fun onPropertiesLock()
}

data class ResourcePair<A : Any, B : Any>(var first: A, var second: B)
typealias RPair = ResourcePair<Any, PropertyBounds<Any>>

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
    val sections: MutableMap<String, MutableMap<String, RPair>> = mutableMapOf()
    private var containers = mutableListOf<PropertyContainer>()
    private var propertiesLocked = false

    fun <T : Any> attachProperty(pData: PropertyData<T>, pBounds: PropertyBounds<T> = PropertyBounds()) {
        val section = sections[pData.section]
        if (section != null && section.containsKey(pData.name))
            throw IllegalArgumentException("Property $pData already exists!")
        if (!pBounds.checkProperty(pData.currentValue))
            throw IllegalArgumentException("Illegal default value!")
        if (section != null)
            section[pData.name] = ResourcePair(pData.currentValue, pBounds) as RPair
        else
            sections[pData.section] = mutableMapOf(pData.name to ResourcePair(pData.currentValue, pBounds) as RPair)
    }

    fun <T: Any> detachProperty(pData: PropertyData<T>) {
        sections[pData.section]!!.remove(pData.name)
    }

    fun detachTempProperties() {
        for ((key, value) in sections)
            if (!key.isEmpty() && key[0] == '~')
                sections.remove(key)
    }

    fun <T : Any> getProperty(pData: PropertyData<T>): T =
            sections[pData.section]?.get(pData.name)!!.first as T

    fun <T : Any> setProperty(pData: PropertyData<T>): Boolean {
        if (!propertiesLocked) {
            val pBounds = sections[pData.section]?.get(pData.name)!!.second
            if (!pBounds.checkProperty(pData.currentValue))
                throw IllegalArgumentException("Illegal default value!")
            sections[pData.section]!![pData.name]!!.first = pData.currentValue
            return true
        }
        return false
    }

    fun registerContainer(container: PropertyContainer) {
        containers.add(container)
    }

    fun lockProperties(chained: Boolean = true) {
        propertiesLocked = true
        if (chained)
            containers.forEach { it.onPropertiesLock() }
    }
}

class PropertyLoader<T : Any>(private val manager: ResourceManager,
                              private val pData: PropertyData<T>,
                              private val pBounds: PropertyBounds<T> = PropertyBounds()) {

    inner class PropertyDelegate : ReadOnlyProperty<PropertyContainer, T> {
        init {
            manager.attachProperty(pData, pBounds)
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

/* Bind resource for containers with multiple managers */
fun <T : Any> bindResource(resManager: ResourceManager,
                           propContainer: PropertyContainer,
                           defaultValue: T,
                           name: String,
                           section: String,
                           vararg checkBounds: (T) -> Boolean)
        : PropertyLoader<T> {
    when (defaultValue::class) {
        Boolean::class, Int::class, Double::class, String::class, ArrayList::class -> {
            resManager.registerContainer(propContainer)
            return PropertyLoader(resManager, PropertyData(defaultValue, name, section), PropertyBounds(checkBounds))
        }
        else -> throw IllegalArgumentException("Type ${defaultValue::class.java} isn't supported!")
    }
}

/* Bind resource for containers with one manager */
fun <T : Any> bindResource(propContainer: PropertyContainer,
                           defaultValue: T,
                           name: String,
                           section: String,
                           vararg checkBounds: (T) -> Boolean)
        : PropertyLoader<T> =
        bindResource(propContainer.getResourceManager(),
                propContainer,
                defaultValue,
                name,
                section,
                *checkBounds)

/* Container for all games */
class Games private constructor(context: Context) {
    val games: Array<BaseGame> = arrayOf(TGame(context), SGame(context))

    companion object : SingletonHolder<Games, Context>(::Games)
}
