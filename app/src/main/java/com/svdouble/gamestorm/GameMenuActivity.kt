package com.svdouble.gamestorm

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.ImageButton
import com.squareup.picasso.Picasso
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_game_menu.*
import kotlinx.android.synthetic.main.authorization_rv_item.view.*

private const val TEMP_SECTION = "~game_menu_temp"

interface MenuHeaderItemInteraction {
    var groupAdapter: GroupAdapter<ViewHolder>
    fun destroyItem(player: TPlayer)
    fun updatePlayer(player: TPlayer)
}

class ExpandableMenuHeaderItem(val manager: android.support.v4.app.FragmentManager, val caller: MenuHeaderItemInteraction, val player: TPlayer, val name: String)
    : Item<ViewHolder>(), ExpandableItem {

    lateinit var imageBtn: ImageButton

    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: ViewHolder, position: Int) {
        imageBtn = viewHolder.itemView.pa_item_player_icon
        //viewHolder.itemView.pa_item_player_icon.setImageResource(player.iconId)
        viewHolder.itemView.pa_item_player_name.text = name
        viewHolder.itemView.pa_item_expand_icon.visibility = View.GONE
//        viewHolder.itemView.pa_item_player_name.setOnClickListener {
//            expandableGroup.onToggleExpanded()
//            viewHolder.itemView.pa_item_expand_icon.setImageResource(getIcon())
//        }
        Picasso.get().load(player.chipId).fit().centerInside()
                .into(viewHolder.itemView.pa_item_player_icon)
        viewHolder.itemView.pa_item_player_icon.setOnClickListener {
            manager.beginTransaction().add(R.id.activity_game_menu, SelectIconFragment.newInstance(this, player)).commit()
        }
        viewHolder.itemView.pa_item_button_delete.setOnClickListener {
            caller.destroyItem(player)
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

class GameMenuActivity
    : AppCompatActivity(),
        LoginFragment.OnLoginFragmentInteractionListener,
        MenuHeaderItemInteraction,
        SelectIconFragment.OnSelectIconFragmentInteractionListener {

    private val groupList = mutableListOf<String>()
    private lateinit var playerManager: ResourceManager
    private lateinit var viewAdapter: CardAdapter
    private lateinit var viewManager: GridLayoutManager
    override lateinit var groupAdapter: GroupAdapter<ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_menu)

        Picasso.get().load(R.drawable.play_icon).fit().centerInside().into(gm_buttons_play)
        Picasso.get().load(R.drawable.settings_icon).fit().centerInside().into(gm_buttons_settings)
        Picasso.get().load(R.drawable.info_icon).fit().centerInside().into(gm_buttons_wiki)
        Picasso.get().load(R.drawable.leaderboard_icon).fit().centerInside().into(gm_buttons_scoreboard)

        groupAdapter = GroupAdapter()
        user_list.apply {
            layoutManager = GridLayoutManager(this@GameMenuActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }


        gm_title.typeface = Fonts.getInstance(this).quicksand
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

                if (groupList.isEmpty())
                    for (player in players) {
//                    val pData = PropertyData(player.chipId, "~${player.id}", TEMP_SECTION)
//                    playerManager.attachProperty(pData)
                        ExpandableGroup(ExpandableMenuHeaderItem(supportFragmentManager, this, player, "oops!"), false).apply {
                            //                            add(Section(PropertyWrapper(playerManager, pData).apply { changeTitle("IconId: ") }))
                            groupAdapter.add(this)
                        }
                        groupList.add(player.id)
                    }

            }
            GAME_SNAKE_ID -> {
                val sGame = Games.getInstance(this).games[1] as SGame
                gm_title.text = getString(sGame.titleRId)
                gm_buttons_play.setOnClickListener { startActivity(Intent(this, SnakeActivity::class.java).putExtra(INTENT_ID_KEY, GAME_SNAKE_ID)) }
                gm_buttons_settings.setOnClickListener { startActivity(Intent(this, GameSettingsActivity::class.java).putExtra(INTENT_ID_KEY, GAME_SNAKE_ID))}
            }
        }
        gm_buttons_back.setOnClickListener { startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) }
    }

    override fun onNewPlayer(newPlayer: BasePlayer, name: String) {
        val player = newPlayer as TPlayer
//        val pData = PropertyData(player.chipId, "~${player.id}", TEMP_SECTION)
        playerManager.getProperty(PropertyData(arrayListOf<TPlayer>(), "players", "game_menu")).add(player)
//        playerManager.attachProperty(pData)

        ExpandableGroup(ExpandableMenuHeaderItem(supportFragmentManager, this, player, name), false).apply {
            //add(Section(PropertyWrapper(playerManager, pData).apply { changeTitle("IconId: ") }))
            groupAdapter.add(this)
        }
        groupList.add(player.id)
    }

    override fun destroyItem(player: TPlayer) {
        playerManager.getProperty(PropertyData(arrayListOf<TPlayer>(), "players", "game_menu")).remove(player)
        groupAdapter.removeGroup(groupList.indexOf(player.id))
        groupList.remove(player.id)
    }

    override fun updatePlayer(player: TPlayer) {
        playerManager.getProperty(PropertyData(arrayListOf<TPlayer>(), "players", "game_menu")).find { it.id == player.id }!!.apply { chipId = player.chipId }
    }

    override fun checkIconFree(iconId: Int) =
            playerManager.getProperty(PropertyData(arrayListOf<TPlayer>(), "players", "game_menu"))
                    .find { it.chipId == iconId } == null
}
