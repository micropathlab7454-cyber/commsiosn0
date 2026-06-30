package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class LabRepository(private val labDao: LabDao) {

    val allEntries: Flow<List<Entry>> = labDao.getAllEntries()
    val allDoctors: Flow<List<Doctor>> = labDao.getAllDoctors()
    
    // Settings stream, emits a default if null
    val settings: Flow<LabSettings> = labDao.getSettings().map { it ?: LabSettings() }

    // --- Entries suspend methods ---
    suspend fun insertEntry(entry: Entry) {
        labDao.insertEntry(entry)
    }

    suspend fun updateEntry(entry: Entry) {
        labDao.updateEntry(entry)
    }

    suspend fun deleteEntry(entry: Entry) {
        labDao.deleteEntry(entry)
    }

    suspend fun deleteEntryById(id: Int) {
        labDao.deleteEntryById(id)
    }

    // --- Doctors suspend methods ---
    suspend fun insertDoctor(doctor: Doctor) {
        labDao.insertDoctor(doctor)
    }

    suspend fun updateDoctor(doctor: Doctor) {
        labDao.updateDoctor(doctor)
    }

    suspend fun deleteDoctor(doctor: Doctor) {
        labDao.deleteDoctor(doctor)
    }

    suspend fun deleteDoctorById(id: Int) {
        labDao.deleteDoctorById(id)
    }

    // --- Settings suspend methods ---
    suspend fun getSettingsDirect(): LabSettings {
        return labDao.getSettingsDirect() ?: LabSettings().also {
            labDao.insertSettings(it)
        }
    }

    suspend fun updateSettings(settings: LabSettings) {
        labDao.insertSettings(settings)
    }

    // --- Backup & Restore ---
    fun backupDatabase(context: Context): Boolean {
        return try {
            val dbFile = context.getDatabasePath("micro_path_lab_database")
            val backupFile = File(context.getExternalFilesDir(null), "micro_path_lab_backup.db")
            if (dbFile.exists()) {
                dbFile.copyTo(backupFile, overwrite = true)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun restoreDatabase(context: Context): Boolean {
        return try {
            val dbFile = context.getDatabasePath("micro_path_lab_database")
            val backupFile = File(context.getExternalFilesDir(null), "micro_path_lab_backup.db")
            if (backupFile.exists()) {
                AppDatabase.getDatabase(context).close()
                val shmFile = File(dbFile.path + "-shm")
                val walFile = File(dbFile.path + "-wal")
                if (shmFile.exists()) shmFile.delete()
                if (walFile.exists()) walFile.delete()
                backupFile.copyTo(dbFile, overwrite = true)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
