package com.svdouble.gamestorm

import android.app.Application
import android.arch.persistence.room.*
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import org.jetbrains.anko.doAsync


/* Room requires multithreading */
//class DbWorkerThread(threadName: String) : HandlerThread(threadName) {
//
//    private lateinit var mWorkerHandler: Handler
//
//    override fun onLooperPrepared() {
//        super.onLooperPrepared()
//        mWorkerHandler = Handler(looper)
//    }
//
//    fun postTask(task: Runnable, aim: String = "") {
//        if (::mWorkerHandler.isInitialized)
//            mWorkerHandler.post(task)
//        else
//            Log.d(TAG,"Unsuccessful post: $aim.")
//    }
//
//}

@Entity(tableName = "userData")
data class UserData(@PrimaryKey var id: String,
                    @ColumnInfo(name = "nickname") var name: String,
                    @ColumnInfo(name = "iconId") var iconId: Int,
                    @ColumnInfo(name = "tGameIconId") var tGameIconId: Int)


@Dao
interface UserDataDAO {
    @get:Query("SELECT * FROM userData")
    val all: List<UserData>

    @Insert
    fun insert(userData: UserData)

    @Insert
    fun insertAll(userData: List<UserData>)

    @Query("DELETE FROM userData")
    fun deleteAll()

    @Delete
    fun delete(user: UserData)

    @Update
    fun update(users: List<UserData>)

    @Query("SELECT * FROM userData WHERE nickname LIKE :name")
    fun findByName(name: String) : List<UserData>

    @Query("SELECT * FROM userData WHERE id = :userId")
    fun findById(userId: String): UserData
}

@Database(entities = [(UserData::class)], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDataDao(): UserDataDAO

    companion object {

        private var sInstance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                sInstance = Room
                        .databaseBuilder(context.applicationContext, AppDatabase::class.java, "example")
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return sInstance!!
        }
    }

}

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        doAsync {
            val database = AppDatabase.getInstance(context = this@App)
            if (!database.userDataDao().all.isEmpty())
                database.userDataDao().deleteAll()
        }
    }

}
