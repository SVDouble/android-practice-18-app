package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*


const val DEBUG_LOG_KEY = "GameStormApp"

class MainActivity : AppCompatActivity() {

    private lateinit var viewAdapter: CardAdapter
    private lateinit var viewManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dataset = arrayOf(GameCard("hello", 5.0, R.drawable.ic_launcher_foreground))
        viewManager = GridLayoutManager(this, 2)
        viewAdapter = CardAdapter(this, dataset)
        viewAdapter.notifyDataSetChanged()

        recycler_view.apply {
            layoutManager = viewManager
            addItemDecoration(GridSpacingItemDecoration(2, dpToPx(10), true))
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
        }
    }

    inner class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) :
            RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }

    private fun dpToPx(dp: Int) =
            Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics))

}



data class GameCard(val title: String, val rating: Double, val thumbnail: Int)

class CardAdapter(private val mContext: Context, private val dataset: Array<GameCard>) :
        RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val titleView = v.findViewById<TextView>(R.id.title)!!
        val ratingView = v.findViewById<TextView>(R.id.rating)!!
        val thumbnailView = v.findViewById<ImageView>(R.id.thumbnail)!!
        val overflowView = v.findViewById<ImageView>(R.id.overflow)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CardAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.game_card, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleView.text = dataset[position].title
        holder.ratingView.text = dataset[position].rating.toString()
        Glide.with(mContext).load(dataset[position].thumbnail).into(holder.thumbnailView)
        holder.overflowView.setOnClickListener { showPopupMenu(holder.overflowView) }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(mContext, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.menu_card, popup.menu)
        popup.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_add_favourite -> Log.d(DEBUG_LOG_KEY, "Wow!")
                R.id.action_rate -> Log.d(DEBUG_LOG_KEY, "Oops!")
                else -> return@setOnMenuItemClickListener false
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    override fun getItemCount() = dataset.size
}

