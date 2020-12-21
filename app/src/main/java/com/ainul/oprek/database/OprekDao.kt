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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
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
    fun getUser(email: String): User

    /**
     * Delete all users from database
     */
    @Query("DELETE FROM table_user")
    fun deleteAllUsers()

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
     * @return listOf(Project)
     */
    @Query("SELECT * FROM table_project WHERE id = :userId")
    fun getProjects(userId: Int): LiveData<List<Project>>
}