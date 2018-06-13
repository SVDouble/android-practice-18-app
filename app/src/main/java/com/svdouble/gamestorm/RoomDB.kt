package com.svdouble.gamestorm

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.Log


/* Room requires multithreading */
class DbWorkerThread(threadName: String) : HandlerThread(threadName) {

    private lateinit var mWorkerHandler: Handler

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        mWorkerHandler = Handler(looper)
    }

    fun postTask(task: Runnable, aim: String = "") {
        if (::mWorkerHandler.isInitialized)
            mWorkerHandler.post(task)
        else
            Log.d(TAG,"Unsuccessful post: $aim.")
    }

}

@Entity(tableName = "userData")
data class UserData(@PrimaryKey var id: String,
                    @ColumnInfo(name = "nickname") var name: String,
                    @ColumnInfo(name = "iconId") var iconId: Int,
                    @ColumnInfo(name = "tGameIconId") var tGameIconId: Int)


@Dao
interface UserDataDAO {
    @Query("SELECT * from userData")
    fun getAll(): LiveData<List<UserData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userData: UserData)

    @Query("DELETE FROM userData")
    fun deleteAll()

    @Query("DELETE FROM userData WHERE id = :userId")
    fun deleteById(userId: String)

    @Update
    fun update(vararg users: UserData)

    @Query("SELECT * FROM userData WHERE nickname LIKE :name")
    fun findByName(name: String) : List<UserData>
}


@Database(entities = arrayOf(UserData::class), version = 1)
abstract class UserDataBase : RoomDatabase() {
    abstract fun userDataDao(): UserDataDAO

    companion object {
        private var INSTANCE: UserDataBase? = null

        fun getInstance(context: Context): UserDataBase? {
            if (INSTANCE == null) {
                synchronized(UserDataBase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserDataBase::class.java, "test.db") // databaseBuilder()
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
