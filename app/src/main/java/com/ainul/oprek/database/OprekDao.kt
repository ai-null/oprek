package com.ainul.oprek.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ainul.oprek.database.entities.User
import com.ainul.oprek.database.entities.Project
import com.ainul.oprek.util.Constants

@Dao
interface OprekDao {
    // ===== USER =====

    /**
     * create new user, [User.email] must be unique
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun registerUser(data: User)

    /**
     * Grep user data based on its email address
     */
    @Query("SELECT * FROM table_user WHERE email = :email LIMIT 1")
    fun validateEmail(email: String): User?

    @Query("SELECT * FROM table_user WHERE email = :email AND pin = :pin")
    fun getUser(email: String, pin: Int): User?

    @Query("UPDATE table_user SET username = :username WHERE id = :id")
    fun updateUsername(id: Long, username: String)

    @Query("UPDATE table_user SET company = :company WHERE id = :id")
    fun updateCompany(id: Long, company: String)

    @Query("UPDATE table_user SET profile_picture = :path WHERE id = :id")
    fun updateProfilePicture(id: Long, path: String)

    @Query("UPDATE table_user SET income = :values WHERE id = :id")
    fun addIncome(id: Long, values: Double)

    // ===== PROJECT =====

    @Insert
    fun addProject(project: Project)

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun updateProject(data: Project)

    @Query("DELETE FROM table_project WHERE id = :projectId")
    fun deleteProject(projectId: Long)

    // Update status by id
    @Query("UPDATE table_project SET status = :status WHERE id = :projectId")
    fun updateProjectStatus(projectId: Long, status: Int)

    /**
     * grep project data from provided id, returns project data class
     */
    @Query("SELECT * FROM table_project WHERE id = :id")
    fun getProject(id: Long): Project?

    /**
     * get list of projects from database from provided id
     */
    @Query("SELECT * FROM table_project WHERE user_id = :userId ORDER BY id DESC")
    fun getProjects(userId: Long): LiveData<List<Project>?>

    @Query("SELECT * FROM table_project WHERE user_id = :userId AND status = :status ORDER BY id DESC")
    fun filterProject(userId: Long, status: Int): LiveData<List<Project>>
}