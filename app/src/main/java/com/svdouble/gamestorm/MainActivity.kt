package com.svdouble.gamestorm

import android.content.Context
import android.content.Intent
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*

const val DEBUG_LOG_KEY = "GameStormApp"
const val INTENT_ID_KEY = "GAME_ID"

class MainActivity : AppCompatActivity() {

    private lateinit var viewAdapter: CardAdapter
    private lateinit var viewManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        /* Init toolbar */
        setSupportActionBar(toolbar)
        collapsing_toolbar.title = " "
        appbar.setExpanded(true)

        /* hiding & showing the title when toolbar expanded & collapsed */
        appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (appBarLayout.totalScrollRange + verticalOffset == 0)
                collapsing_toolbar.title = getString(R.string.app_name)
            else if (toolbar.title.toString() == getString(R.string.app_name)) {
                collapsing_toolbar.title = " "
            }
        }

        /* Initialize dataset, Manager and Adapter */
        val dataset = arrayOf(
                TGame.getInstance(this).generateGameCard(),
                GameCard(0,"world", 5.0, R.drawable.ic_launcher_foreground),
                GameCard(0,"wow!", 1.0, R.drawable.ic_launcher_background),
                GameCard(0,"really!", 1.0, R.drawable.ic_launcher_background),
                GameCard(0,"mmm!", 1.0, R.drawable.ic_launcher_background),
                GameCard(0,"amazing!", 1.0, R.drawable.ic_launcher_background))
        viewManager = GridLayoutManager(this, 2)
        viewAdapter = CardAdapter(this, dataset)
        viewAdapter.notifyDataSetChanged()

        /* Set recycler's options */
        recycler_view.apply {
            layoutManager = viewManager
            addItemDecoration(GridSpacingItemDecoration(2, dpToPx(10), true))
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
        }

        /* Init header background */
        //Glide.with(this).load(R.drawable.ic_launcher_background).into(backdrop)
        Picasso.get().load(R.drawable.sun).into(backdrop)

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


data class GameCard(val gameId: Int, val title: String, val rating: Double, val thumbnail: Int)

/* Custom adapter for the RecyclerView */
class CardAdapter(private val mContext: Context, private val dataset: Array<GameCard>) :
        RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        //val cardView = v.findViewById<CardView>(R.id.card_view)!!
        val titleView = v.findViewById<TextView>(R.id.textViewTitle)!!
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
        holder.ratingView.text = mContext.getString(R.string.rating_pattern).format(dataset[position].rating)
        //Glide.with(mContext).load(dataset[position].thumbnail).into(holder.thumbnailView)
        Picasso.get().load(dataset[position].thumbnail).into(holder.thumbnailView)
        holder.overflowView.setOnClickListener { showPopupMenu(holder.overflowView) }
        holder.thumbnailView.setOnClickListener { mContext.startActivity(Intent(mContext, GameMenuActivity::class.java).putExtra(INTENT_ID_KEY, dataset[position].gameId)) }
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

