package com.ainul.oprek.database

import androidx.lifecycle.LiveData
import androidx.room.*

// TODO: Add Delete account and update user profile

@Dao
interface OprekDao {
    /**
     * create new user, [User.email] must be unique
     *
     * @param data User - {@see Entities}
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun registerUser(data: User)

    @Insert
    fun addProject(project: Project)

    /**
     * Select all users from database, returns list of user
     *
     * @return [List] list of user
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

    @Query("SELECT * FROM table_project WHERE id = :id")
    fun getProject(id: Long): Project?

    @Query("SELECT * FROM table_user WHERE email = :email AND pin = :pin")
    fun validateUser(email: String, pin: String): User?

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun updateProject(data: Project)

    /**
     * get list of projects from database
     *
     * @param userId Int - user id from user table
     * @return [LiveData] nullable listOf(Project) since there will be case when user doesn't have any project yet
     */
    @Query("SELECT * FROM table_project WHERE user_id = :userId ORDER BY id DESC")
    fun getProjects(userId: Long): LiveData<List<Project>?>

    @Query("DELETE FROM table_project WHERE id = :projectId")
    fun deleteProject(projectId: Long)

    /**
     * !! WARNING !!
     * this methods only meant for development purposes
     */
    @Query("DELETE FROM table_user")
    fun deleteAllUsers()

    @Query("DELETE FROM table_project")
    fun deleteAllProjects()
}