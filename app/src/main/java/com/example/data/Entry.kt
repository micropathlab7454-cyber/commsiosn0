package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // format: YYYY-MM-DD
    val patientName: String,
    val age: Int,
    val test: String,
    val amount: Double,
    val doctorAmount: Double,
    val otherAmount: Double,
    val doctorId: Int? = null // Associated doctor for commission calculations
)
