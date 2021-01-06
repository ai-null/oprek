package com.ainul.oprek.database

import androidx.lifecycle.LiveData
import androidx.room.*

// TODO: Add Delete account and update user profile

@Dao
interface OprekDao {
    // ===== USER =====
    // ================
    /**
     * create new user, [User.email] must be unique
     *
     * @param data [User]
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun registerUser(data: User)

    /**
     * Select ALL users from database, returns list of user
     *
     * @return [List] list of [User]
     */
    @Query("SELECT * FROM table_user")
    fun getUsers(): List<User>

    /**
     * Grep user data based on its email address
     *
     * @param email String
     * @return [User]? nullable user rows, there will be case when there's no user yet
     */
    @Query("SELECT * FROM table_user WHERE email = :email LIMIT 1")
    fun getUser(email: String): User?

    @Query("SELECT * FROM table_user WHERE email = :email AND pin = :pin")
    fun validateUser(email: String, pin: String): User?

    // ===== PROJECT =====
    // ===================

    @Insert
    fun addProject(project: Project)

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun updateProject(data: Project)

    @Query("DELETE FROM table_project WHERE id = :projectId")
    fun deleteProject(projectId: Long)

    /**
     * grep project data from provided id, returns project data class
     *
     * @param id [Project.id]
     * @return [Project]? nullable project data class
     */
    @Query("SELECT * FROM table_project WHERE id = :id")
    fun getProject(id: Long): Project?

    /**
     * get list of projects from database from provided id
     *
     * @param userId Int - user id from user table
     * @return [LiveData]
     *  nullable listOf([Project]) since there will be case when user doesn't have any project yet
     */
    @Query("SELECT * FROM table_project WHERE user_id = :userId ORDER BY id DESC")
    fun getProjects(userId: Long): LiveData<List<Project>?>

    /**
     * !! WARNING !!
     * this methods only meant for development purposes
     */
    @Query("DELETE FROM table_user")
    fun deleteAllUsers()

    @Query("DELETE FROM table_project")
    fun deleteAllProjects()
}