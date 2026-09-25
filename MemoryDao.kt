package com.akhali.smartassistant.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MemoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMemory(memory: MemoryEntity)

    @Query("SELECT * FROM memory_table WHERE key = :key LIMIT 1")
    suspend fun getMemoryByKey(key: String): MemoryEntity?

    @Query("SELECT * FROM memory_table WHERE category = :category ORDER BY timestamp DESC LIMIT 20")
    suspend fun getMemoriesByCategory(category: String): List<MemoryEntity>

    @Query("SELECT * FROM memory_table ORDER BY timestamp DESC LIMIT 50")
    suspend fun getAllMemories(): List<MemoryEntity>
}
