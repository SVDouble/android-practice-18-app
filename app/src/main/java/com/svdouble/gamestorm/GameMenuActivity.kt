package com.svdouble.gamestorm

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_game_menu.*
import kotlinx.android.synthetic.main.authorization_rv_item.view.*

private const val TEMP_SECTION = "~game_menu_temp"

class ExpandableMenuHeaderItem(val player: TPlayer) : Item<ViewHolder>(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: ViewHolder, position: Int) {
        //viewHolder.itemView.pa_item_player_icon.setImageResource(player.iconId)
        viewHolder.itemView.pa_item_player_name.text = player.id

        viewHolder.itemView.pa_item_player_name.setOnClickListener {
            expandableGroup.onToggleExpanded()
            viewHolder.itemView.pa_item_expand_icon.setImageResource(getIcon())
        }
    }

    override fun getLayout() = R.layout.authorization_rv_item

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    private fun getIcon() =
            if (expandableGroup.isExpanded)
                android.R.drawable.arrow_up_float
            else
                android.R.drawable.arrow_down_float
}

class GameMenuActivity : AppCompatActivity(), LoginFragment.OnLoginFragmentInteractionListener {

    private lateinit var playerManager: ResourceManager
    private lateinit var viewAdapter: CardAdapter
    private lateinit var viewManager: GridLayoutManager
    private lateinit var groupAdapter: GroupAdapter<ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_menu)

        groupAdapter = GroupAdapter()
        user_list.apply {
            layoutManager = GridLayoutManager(this@GameMenuActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }


        when(intent.getIntExtra(INTENT_ID_KEY, -1)) {
            GAME_TICTACTOE_ID -> {
                /* Basic buttons */
                val tGame = Games.getInstance(this).games[0] as TGame
                gm_title.text = getString(tGame.titleRId)
                gm_buttons_play.setOnClickListener { startActivity(Intent(this, CanvasActivity::class.java).putExtra(INTENT_ID_KEY, GAME_TICTACTOE_ID)) }
                gm_buttons_settings.setOnClickListener { startActivity(Intent(this, GameSettingsActivity::class.java).putExtra(INTENT_ID_KEY, GAME_TICTACTOE_ID))}
                gm_header_button_add.setOnClickListener {
                    supportFragmentManager.beginTransaction().add(R.id.activity_game_menu, LoginFragment.newInstance("", "")).commit()
                }

                /* Player settings */
                playerManager = (Games.getInstance(this).games[0] as TGame).playerManager
                val players = playerManager.getProperty(PropertyData(arrayListOf<TPlayer>(), "players", "game_menu"))

                for (player in players) {
                    val pData = PropertyData(player.chipId, "~${player.id}", TEMP_SECTION)
                    playerManager.attachProperty(pData)
                    ExpandableGroup(ExpandableMenuHeaderItem(player), false).apply {
                        add(Section(PropertyWrapper(playerManager, pData).apply { changeTitle("IconId: ") }))
                        groupAdapter.add(this)
                    }
                }
            }
            GAME_SNAKE_ID -> {
                val sGame = Games.getInstance(this).games[1] as SGame
                gm_title.text = getString(sGame.titleRId)
                gm_buttons_play.setOnClickListener { startActivity(Intent(this, SnakeActivity::class.java).putExtra(INTENT_ID_KEY, GAME_SNAKE_ID)) }
                gm_buttons_settings.setOnClickListener { startActivity(Intent(this, GameSettingsActivity::class.java).putExtra(INTENT_ID_KEY, GAME_TICTACTOE_ID))}
            }
        }
        gm_buttons_back.setOnClickListener { startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) }
    }

    override fun onNewPlayer(newPlayer: BasePlayer, name: String) {
        val player = newPlayer as TPlayer
        val pData = PropertyData(player.chipId, "~${player.id}", TEMP_SECTION)
        playerManager.getProperty(PropertyData(arrayListOf<TPlayer>(), "players", "game_menu")).add(player)
        playerManager.attachProperty(pData)
        ExpandableGroup(ExpandableMenuHeaderItem(player), false).apply {
            add(Section(PropertyWrapper(playerManager, pData).apply { changeTitle("IconId: ") }))
            groupAdapter.add(this)
        }
    }
}
