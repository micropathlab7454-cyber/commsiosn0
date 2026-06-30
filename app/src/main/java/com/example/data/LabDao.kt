package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LabDao {
    // --- Entries ---
    @Query("SELECT * FROM entries ORDER BY date DESC, id DESC")
    fun getAllEntries(): Flow<List<Entry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: Entry)

    @Update
    suspend fun updateEntry(entry: Entry)

    @Delete
    suspend fun deleteEntry(entry: Entry)

    @Query("DELETE FROM entries WHERE id = :id")
    suspend fun deleteEntryById(id: Int)

    // --- Doctors ---
    @Query("SELECT * FROM doctors ORDER BY name ASC")
    fun getAllDoctors(): Flow<List<Doctor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctor(doctor: Doctor)

    @Update
    suspend fun updateDoctor(doctor: Doctor)

    @Delete
    suspend fun deleteDoctor(doctor: Doctor)

    @Query("DELETE FROM doctors WHERE id = :id")
    suspend fun deleteDoctorById(id: Int)

    // --- Settings ---
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<LabSettings?>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettingsDirect(): LabSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: LabSettings)
}
