package com.ainul.oprek.database

import android.content.Context
import androidx.room.*

@Dao
interface OprekDao {
    // == USER RELATED CONTROLLER ==
    // TODO: Add Delete account and update user profile
    @Insert(entity = User::class, onConflict = OnConflictStrategy.ABORT)
    fun registerUser()

    // == PROJECT RELATED CONTROLLER ==
    // TODO: add delete project
    @Insert(entity = Project::class, onConflict = OnConflictStrategy.REPLACE)
    fun addProject()

    @Update(entity = Project::class, onConflict = OnConflictStrategy.ABORT)
    fun updateProject()

    /**
     * get list of projects from database
     *
     * @param userId Int - user id from User table
     *
     * @return listOf(Project)
     */
    @Query("SELECT * FROM table_project WHERE id = user_id")
    fun getProjects(userId: Int): List<Project>
}

@Database(entities = [User::class, Project::class], exportSchema = true, version = 1)
abstract class OprekDatabase : RoomDatabase() {
    abstract val oprekDao: OprekDao

    companion object {
        private lateinit var instance: OprekDatabase

        fun getDatabase(context: Context): OprekDatabase {
            synchronized(OprekDatabase::class.java) {
                if (!::instance.isInitialized) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        OprekDatabase::class.java,
                        "oprek_database"
                    ).fallbackToDestructiveMigration().build()
                }

                return instance
            }
        }
    }
}

