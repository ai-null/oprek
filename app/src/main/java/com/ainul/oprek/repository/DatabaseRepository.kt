package com.ainul.oprek.repository

import androidx.lifecycle.LiveData
import com.ainul.oprek.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseRepository(database: OprekDatabase) {
    private val dao = database.oprekDao

    /**
     * grep all rows from `table_project`.
     * returns list of Project{@see Entities}
     *
     * @param userId Int - User.id {@see Entities}
     * @return LiveData
     */
    suspend fun getAllProjects(userId: Int): LiveData<List<Project>> {
        return withContext(Dispatchers.IO) {
            dao.getProjects(userId)
        }
    }

    /**
     * this will register user to database
     * User.email {@see Entities} must be unique, can't be same as other user
     *
     * @param data User - {@see Entities}
     * @throws Exception
     */
    suspend fun registerUserToDatabase(data: User) {
        withContext(Dispatchers.IO) {
            if (checkAvailability(data.email)) {
                dao.registerUser(data)
            } else {
                throw Exception("user already exist")
            }
        }
    }

    /**
     * this method used to check email of new user, if it same as other
     * returns `false`, otherwise `true`
     *
     * @param email String - {@see Entities} User.email
     * @return Boolean
     */
    private fun checkAvailability(email: String): Boolean {
        return dao.getUser(email = email) == null
    }

    /**
     * !! WARNING !!
     * This method only meant for development purposes, erase or comment this method
     * and all of its instance when build on production
     */
    suspend fun deleteAllUser() {
        withContext(Dispatchers.IO) {
            dao.deleteAllUsers()
        }
    }

    /**
     * grep all rows from `table_user` {@see Entities}, returns as listOf<User>
     *
     * @return List<User>
     */
    suspend fun getAllUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            dao.getUsers()
        }
    }

    /**
     * create new project, {@see Dao.addProject}
     *
     * @param data Project - {@see Entities}
     */
    suspend fun addProjectToDatabase(data: Project) {
        withContext(Dispatchers.IO) {
            dao.addProject(data)
        }
    }

    /**
     * update data of the existing project, {@see Dao.updateProject}
     *
     * @param data Project - {@see Entities}
     */
    suspend fun updateProjectToDatabase(data: Project) {
        withContext(Dispatchers.IO) {
            dao.updateProject(data)
        }
    }
}