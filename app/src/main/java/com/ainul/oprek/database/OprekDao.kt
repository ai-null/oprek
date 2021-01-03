package com.ainul.oprek.database

import androidx.lifecycle.LiveData
import androidx.room.*

// TODO: Add Delete account and update user profile
// TODO: add delete project

@Dao
interface OprekDao {
    /**
     * create new user, User.email {@see Entities} must be unique
     *
     * @param data User - {@see Entities}
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun registerUser(data: User)

    /**
     * create new project of Project {@see Entities}
     *
     * @param project Project - {@see Entities}
     */
    @Insert
    fun addProject(project: Project)

    /**
     * Select all users from database, returns list of user
     *
     * @return listOf<User>
     */
    @Query("SELECT * FROM table_user")
    fun getUsers(): List<User>

    /**
     * Grep user data based on its email address
     *
     * @param email String
     * @return User
     */
    @Query("SELECT * FROM table_user WHERE email = :email LIMIT 1")
    fun getUser(email: String): User?

    @Query("SELECT * FROM table_user WHERE email = :email AND pin = :pin")
    fun validateUser(email: String, pin: String): User?

    /**
     * Delete all users from database
     */
    @Query("DELETE FROM table_user")
    fun deleteAllUsers()

    @Query("DELETE FROM table_project")
    fun deleteAllProjects()

    /**
     * update project data, changing status, etc.
     * @param data {@link Project}
     */
    @Update(onConflict = OnConflictStrategy.ABORT)
    fun updateProject(data: Project)

    /**
     * get list of projects from database
     *
     * @param userId Int - user id from user table
     * @return nullable listOf(Project) since there will be case when user doesn't have any project yet
     */
    @Query("SELECT * FROM table_project WHERE user_id = :userId ORDER BY id DESC")
    fun getProjects(userId: Long): LiveData<List<Project>?>
}