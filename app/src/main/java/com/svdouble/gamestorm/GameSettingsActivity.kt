package com.svdouble.gamestorm

import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_game_settings.*
import kotlinx.android.synthetic.main.settings_header.view.*
import kotlinx.android.synthetic.main.settings_item_bool.view.*
import kotlinx.android.synthetic.main.settings_item_string.view.*

private const val SPAN_COUNT = 1 // Number of columns

@Suppress("UNCHECKED_CAST")
class PropertyWrapper<T : Any>(val manager: ResourceManager, var pData: PropertyData<T>, val pBounds: PropertyBounds<T> = PropertyBounds()) : Item<ViewHolder>() {

    lateinit var title: String

    override fun getLayout() =
            when (pData.currentValue::class) {
                Int::class, Double::class, String::class -> R.layout.settings_item_string
                Boolean::class -> R.layout.settings_item_bool
                else -> throw IllegalArgumentException("Type ${pData.currentValue::class.java} isn't supported!")
            }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        when (pData.currentValue::class) {
            String::class -> {
                changeTitle(viewHolder.itemView.resources.getString(R.string.settings_prop_name_pattern).format(pData.name), true)
                view.prop_name_string.text = title
                view.prop_name_string.setTextColor(GREEN)
                view.prop_field_string.setText(pData.currentValue.toString())
                view.prop_field_string.inputType = InputType.TYPE_CLASS_TEXT
                view.prop_field_string.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        val value = view.prop_field_string.text?.toString()
                        if (!value.isNullOrEmpty() && !value!!.isBlank()) {
                            val newValue = value as? T
                            if (newValue != null && pBounds.checkProperty(newValue)) {
                                manager.setProperty(pData.apply { currentValue = newValue })
                                view.prop_name_string.setTextColor(GREEN)
                            }
                            else
                                view.prop_name_string.setTextColor(RED)
                        }
                        else
                            view.prop_name_string.setTextColor(RED)
                    }
                })
            }
            Int::class -> {
                changeTitle(viewHolder.itemView.resources.getString(R.string.settings_prop_name_pattern).format(pData.name), true)
                view.prop_name_string.text = title
                view.prop_name_string.setTextColor(GREEN)
                view.prop_field_string.setText(pData.currentValue.toString())
                view.prop_field_string.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                view.prop_field_string.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        val value = view.prop_field_string.text?.toString()
                        if (!value.isNullOrEmpty() && !value!!.isBlank()) {
                            val newValue = value.toIntOrNull() as? T
                            if (newValue != null && pBounds.checkProperty(newValue)) {
                                manager.setProperty(pData.apply { currentValue = newValue })
                                view.prop_name_string.setTextColor(GREEN)
                            }
                            else
                                view.prop_name_string.setTextColor(RED)
                        }
                        else
                            view.prop_name_string.setTextColor(RED)
                    }
                })
            }
            Double::class -> {
                changeTitle(viewHolder.itemView.resources.getString(R.string.settings_prop_name_pattern).format(pData.name), true)
                view.prop_name_string.text = title
                view.prop_name_string.setTextColor(GREEN)
                view.prop_field_string.setText(pData.currentValue.toString())
                view.prop_field_string.inputType = InputType.TYPE_CLASS_NUMBER
                view.prop_field_string.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        val value = view.prop_field_string.text?.toString()
                        if (!value.isNullOrEmpty() && !value!!.isBlank()) {
                            val newValue = value.toDoubleOrNull() as? T
                            if (newValue != null && pBounds.checkProperty(newValue)) {
                                manager.setProperty(pData.apply { currentValue = newValue })
                                view.prop_name_string.setTextColor(GREEN)
                            }
                            else
                                view.prop_name_string.setTextColor(RED)
                        }
                        else
                            view.prop_name_string.setTextColor(RED)
                    }
                })
            }
            Boolean::class -> {
                changeTitle(viewHolder.itemView.resources.getString(R.string.settings_prop_name_pattern).format(pData.name), true)
                view.prop_name_bool.text = title
                view.prop_name_bool.setTextColor(GREEN)
                view.prop_field_bool.isChecked = pData.currentValue as Boolean
                view.prop_field_bool.setOnCheckedChangeListener { _, state ->
                    manager.setProperty(pData.apply { currentValue = state as T })
                }
            }
        }
    }

    override fun getSpanSize(spanCount: Int, position: Int) = spanCount / SPAN_COUNT

    fun changeTitle(newTitle: String, bind: Boolean = false) {
        if (!bind)
            title = newTitle
        else if (!::title.isInitialized)
            title = newTitle
    }
}

class ExpandableSettingsHeaderItem(val title: String) : Item<ViewHolder>(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.company_title.text = title
        viewHolder.itemView.header_icon.setImageResource(getIcon())

        viewHolder.itemView.header_card.setOnClickListener {
            expandableGroup.onToggleExpanded()
            viewHolder.itemView.header_icon.setImageResource(getIcon())
        }
    }

    override fun getLayout() = R.layout.settings_header

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    private fun getIcon() =
            if (expandableGroup.isExpanded)
                android.R.drawable.arrow_up_float
            else
                android.R.drawable.arrow_down_float
}

class GameSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            spanCount = SPAN_COUNT
        }

        recyclerView.apply {
            layoutManager = GridLayoutManager(this@GameSettingsActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }

        when (intent.getIntExtra(INTENT_ID_KEY, -1)) {
            GAME_TICTACTOE_ID -> {
                val manager = (Games.getInstance(this).games[0] as TGame).manager
                for ((title, section) in manager.sections)
                    ExpandableGroup(ExpandableSettingsHeaderItem(title), false).apply {
                        add(Section(section.map { (name, res) -> PropertyWrapper(manager, PropertyData(res.first, name, title), res.second) }))
                        groupAdapter.add(this)
                    }
            }
            GAME_SNAKE_ID -> {
                val manager = (Games.getInstance(this).games[1] as SGame).manager
                for ((title, section) in manager.sections)
                    ExpandableGroup(ExpandableSettingsHeaderItem(title), false).apply {
                        add(Section(section.map { (name, res) -> PropertyWrapper(manager, PropertyData(res.first, name, title), res.second) }))
                        groupAdapter.add(this)
                    }
            }
        }
    }
}
