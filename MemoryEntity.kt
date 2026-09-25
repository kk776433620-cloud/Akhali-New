package com.akhali.smartassistant.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_table")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val key: String,
    val value: String,
    val category: String, // "preference", "context", "history", "user_habit"
    val timestamp: Long = System.currentTimeMillis()
)
