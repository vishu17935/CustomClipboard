package com.example.customclipboard.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipDao {

    @Insert
    suspend fun insertClip(clip: ClipEntity): Long

    @Query("DELETE FROM clips WHERE id = :clipId")
    suspend fun deleteById(clipId: Long)

    @Query("DELETE FROM clips")
    suspend fun deleteAll()

    @Query("SELECT * FROM clips ORDER BY created_at DESC")
    fun getAllClips(): Flow<List<ClipEntity>>

    @Query("SELECT * FROM clips WHERE content LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun searchClips(query: String): Flow<List<ClipEntity>>

    @Query("SELECT * FROM clips ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatestClip(): ClipEntity?

    @Query("SELECT COUNT(*) FROM clips")
    fun getClipCount(): Flow<Int>

    // Retention FIFO Trim: Keeps only the newest 'maxCount' rows
    @Query("DELETE FROM clips WHERE id NOT IN (SELECT id FROM clips ORDER BY created_at DESC LIMIT :maxCount)")
    suspend fun trimToMaxCount(maxCount: Int)
}
