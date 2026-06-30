package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class LabSettings(
    @PrimaryKey val id: Int = 1,
    val username: String = "admin",
    val password: String = "admin",
    val isDarkMode: Boolean = false
)
