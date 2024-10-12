package com.example.finnishmp_app.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PMDao {
    // Retrieves all parliament members from the database as a Flow
    @Query("SELECT * FROM parliamentmember")
    fun getAll(): Flow<List<ParliamentMember>>

    // Retrieves a specific parliament member by their Heteka ID
    @Query("SELECT * FROM parliamentmember WHERE hetekaId = :hetekaId")
    fun getById(hetekaId: Int?): Flow<ParliamentMember>

    // Retrieves a random parliament member from the database
    @Query("SELECT * FROM parliamentmember ORDER BY RANDOM() LIMIT 1")
    fun getRandom(): Flow<ParliamentMember>

    //Inserts data into the database and replaces existing records if there's a conflict.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(members: List<ParliamentMember>)

    //Updates an existing record, replacing it if a conflict arises
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(member: ParliamentMember)

    //Deletes a specified record from the database
    @Delete
    suspend fun delete(member: ParliamentMember)
}