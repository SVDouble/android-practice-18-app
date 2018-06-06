package com.svdouble.gamestorm

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_game_settings.*
import kotlinx.android.synthetic.main.recycler_header.view.*
import kotlinx.android.synthetic.main.recycler_item.view.*
import java.util.*

const val SPAN_COUNT = 1 // Number of rows

class GameSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        val boringFancyItems = generateFancyItems(6)
        val excitingFancyItems = generateFancyItems(12)

        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            spanCount = SPAN_COUNT
        }

        recyclerView.apply {
            layoutManager = GridLayoutManager(this@GameSettingsActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }

        ExpandableGroup(ExpandableHeaderItem("Boring Group"), false).apply {
            add(Section(boringFancyItems))
            groupAdapter.add(this)
        }

        ExpandableGroup(ExpandableHeaderItem("Exciting Group"), false).apply {
            add(Section(excitingFancyItems))
            groupAdapter.add(this)
        }
    }

    private fun generateFancyItems(count: Int): MutableList<CVItem>{
        val rnd = Random()
        return MutableList(count){
            CVItem(CV("john", "doe"), Color.argb(255, rnd.nextInt(256),
                    rnd.nextInt(256), rnd.nextInt(256)))
            }
        }

}

data class CV(val name: String, val surname: String)

class CVItem(private val cv: CV, private val color: Int) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.recycler_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.name.text = cv.name
        //viewHolder.itemView.surname.text = cv.surname
        viewHolder.itemView.item_card.setCardBackgroundColor(color)
    }

    override fun getSpanSize(spanCount: Int, position: Int) = spanCount / SPAN_COUNT

}

class ExpandableHeaderItem(val title: String) : Item<ViewHolder>(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.company_title.text = title
        viewHolder.itemView.header_icon.setImageResource(getIcon())

        viewHolder.itemView.header_card.setOnClickListener {
            expandableGroup.onToggleExpanded()
            viewHolder.itemView.header_icon.setImageResource(getIcon())
        }
    }

    override fun getLayout() = R.layout.recycler_header

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    private fun getIcon() =
        if (expandableGroup.isExpanded)
            android.R.drawable.ic_delete
        else
            android.R.drawable.ic_input_add
    }



