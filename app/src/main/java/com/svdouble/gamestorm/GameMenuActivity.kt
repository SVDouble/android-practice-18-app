package com.svdouble.gamestorm

import android.arch.lifecycle.LiveData
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
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

    private var userDb: UserDataBase? = null
    private lateinit var mDbWorkerThread: DbWorkerThread
    private lateinit var playerManager: ResourceManager
    private lateinit var groupAdapter: GroupAdapter<ViewHolder>

    private val mUiHandler = Handler()

    private lateinit var globalUsers: LiveData<List<UserData>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_menu)

        /* Database */

        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()
        userDb = UserDataBase.getInstance(this)
        fetchUserDataFromDb() // load all users
        if (::globalUsers.isInitialized) {
            Log.d(TAG, "Wow!")
            for (user in globalUsers.value!!)
                Log.d(TAG, user.toString())
        }


        /* Recycler view */
        groupAdapter = GroupAdapter()
        user_list.apply {
            layoutManager = GridLayoutManager(this@GameMenuActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }

        /* Check game */
        when (intent.getIntExtra(INTENT_ID_KEY, -1)) {
            GAME_TICTACTOE_ID -> {
                /* Basic buttons */
                val tGame = Games.getInstance(this).games[0] as TGame
                gm_title.text = getString(tGame.titleRId)
                gm_buttons_play.setOnClickListener { startActivity(Intent(this, CanvasActivity::class.java).putExtra(INTENT_ID_KEY, GAME_TICTACTOE_ID)) }
                gm_buttons_settings.setOnClickListener { startActivity(Intent(this, GameSettingsActivity::class.java).putExtra(INTENT_ID_KEY, GAME_TICTACTOE_ID)) }
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
                gm_buttons_settings.setOnClickListener { startActivity(Intent(this, GameSettingsActivity::class.java).putExtra(INTENT_ID_KEY, GAME_SNAKE_ID)) }
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
        insertUserDataInDb(UserData(player.id, name, player.iconId, player.chipId))
    }

    private fun fetchUserDataFromDb() {
        val task = Runnable {
            val userDataList = userDb?.userDataDao()?.getAll()
            mUiHandler.post({
                if (userDataList != null)
                    globalUsers = userDataList
            })
        }
        mDbWorkerThread.postTask(task, "fetch users")
    }

    private fun findUserByIdInDb(userId: String): List<UserData> {
        var userDataListResult: List<UserData> = arrayListOf()
        val task = Runnable {
            val userDataList = userDb?.userDataDao()?.findByName(userId)
            mUiHandler.post({
                if (userDataList != null && !userDataList.isEmpty())
                    userDataListResult = userDataList
            })
        }
        mDbWorkerThread.postTask(task, "find user by id")
        return userDataListResult
    }

    private fun insertUserDataInDb(userData: UserData) {
        val task = Runnable { userDb?.userDataDao()?.insert(userData) }
        mDbWorkerThread.postTask(task, "insert user data")
    }

    private fun updateUserDataInDb(vararg userData: UserData) {
        val task = Runnable { userDb?.userDataDao()?.update(*userData) }
        mDbWorkerThread.postTask(task, "update user data")
    }

    override fun onDestroy() {
        UserDataBase.destroyInstance()
        mDbWorkerThread.quit()
        super.onDestroy()
    }
}
