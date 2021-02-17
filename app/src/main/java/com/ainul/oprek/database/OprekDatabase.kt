package com.ainul.oprek.database

import android.content.Context
import androidx.room.*
import com.ainul.oprek.database.entities.User
import com.ainul.oprek.database.entities.Project

@Database(entities = [User::class, Project::class], exportSchema = false, version = 10)
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

