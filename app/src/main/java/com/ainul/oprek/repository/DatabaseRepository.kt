package com.ainul.oprek.repository

import com.ainul.oprek.database.*
import com.ainul.oprek.database.entities.User
import com.ainul.oprek.database.entities.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseRepository(database: OprekDatabase) {
    private val dao = database.oprekDao

    // ===== USER =====
    // ================

    suspend fun registerUserToDatabase(data: User) {
        withContext(Dispatchers.IO) {
            if (checkAvailability(data.email)) {
                dao.registerUser(data)
            } else {
                throw Exception("Email already used")
            }
        }
    }

    suspend fun updateUsername(id: Long, username: String) {
        withContext(Dispatchers.IO) {
            dao.updateUsername(id, username)
        }
    }

    suspend fun updateCompany(id: Long, company: String) {
        withContext(Dispatchers.IO) {
            dao.updateCompany(id, company)
        }
    }

    suspend fun updateProfilePicture(id: Long, path: String) {
        withContext(Dispatchers.IO) {
            dao.updateProfilePicture(id, path)
        }
    }

    suspend fun updateIncome(id: Long, values: Double) {
        withContext(Dispatchers.IO) {
            dao.updateIncome(id, values)
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
        return dao.validateEmail(email = email) == null
    }

    suspend fun getUser(email: String, pin: Int): User? {
        return withContext(Dispatchers.IO) {
            dao.getUser(email, pin)
        }
    }

    // ===== PROJECT =====
    // ===================

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

    suspend fun getProject(id: Long): Project? {
        return withContext(Dispatchers.IO) {
            dao.getProject(id)
        }
    }

    suspend fun updateProject(data: Project) {
        withContext(Dispatchers.IO) {
            dao.updateProject(data)
        }
    }

    suspend fun deleteProject(projectId: Long) {
        withContext(Dispatchers.IO) {
            dao.deleteProject(projectId)
        }
    }

    suspend fun updateStatus(projectId: Long, status: Int) {
        withContext(Dispatchers.IO) {
            dao.updateProjectStatus(projectId, status)
        }
    }

    /**
     * !! WARNING !!
     * This method only meant for development purposes, erase or comment this method
     * and all of its instance when build on production
     */
//    suspend fun deleteAllUser() {
//        withContext(Dispatchers.IO) {
//            dao.deleteAllUsers()
//        }
//    }
//
//    suspend fun deleteAllProject() {
//        withContext(Dispatchers.IO) {
//            dao.deleteAllProjects()
//        }
//    }
}