package com.ainul.oprek.repository

import com.ainul.oprek.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseRepository(database: OprekDatabase) {
    private val dao = database.oprekDao

    // ===== USER =====
    // ================

    /**
     * this will register user to database
     * [User.email] must be unique, can't be same as other user
     *
     * @param data [User]
     * @throws Exception
     */
    suspend fun registerUserToDatabase(data: User) {
        withContext(Dispatchers.IO) {
            if (checkAvailability(data.email)) {
                dao.registerUser(data)
            } else {
                throw Exception("Email already used")
            }
        }
    }

    /**
     * this method used to check email of new user, if it same as other
     * returns `false`, otherwise `true`
     *
     * @param email String - [User.email]
     * @return [Boolean]
     */
    private fun checkAvailability(email: String): Boolean {
        return dao.getUser(email = email) == null
    }

    /**
     * used to validate user before go into `MainActivity`
     * TODO: check pin in the parameter. pin supposed to be Int, this ain't using Int but String
     *
     * @param email String [User.email]
     * @param pin Int [User.pin]
     * @return [Boolean]
     */
    suspend fun validateUser(email: String, pin: String): Boolean {
        return withContext(Dispatchers.IO) {
            dao.validateUser(email, pin) != null
        }
    }

    suspend fun getUserByEmail(email: String, pin: String): User? {
        return withContext(Dispatchers.IO) {
            dao.validateUser(email, pin)
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

    // ===== PROJECT =====
    // ===================

    suspend fun getProject(id: Long): Project? {
        return withContext(Dispatchers.IO) {
            dao.getProject(id)
        }
    }

    suspend fun deleteProject(projectId: Long) {
        withContext(Dispatchers.IO) {
            dao.deleteProject(projectId)
        }
    }

    /**
     * create new project
     *
     * @param data [Project]
     * @throws NullPointerException throws `NullPointerException` when the condition's not met
     */
    suspend fun addProjectToDatabase(data: Project) {
        withContext(Dispatchers.IO) {
            if (data.deviceName.isNotBlank() and data.customerName.isNotBlank()) {
                dao.addProject(data)
            } else {
                throw NullPointerException("Error setting null value to non-null property")
            }
        }
    }

    /**
     * update data of the existing project
     *
     * @param data [Project]
     */
    suspend fun updateProjectToDatabase(data: Project) {
        withContext(Dispatchers.IO) {
            dao.updateProject(data)
        }
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

    suspend fun deleteAllProject() {
        withContext(Dispatchers.IO) {
            dao.deleteAllProjects()
        }
    }
}