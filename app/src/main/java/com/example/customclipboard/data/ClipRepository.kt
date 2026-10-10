package com.example.customclipboard.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import kotlinx.coroutines.flow.Flow

class ClipRepository(
    private val clipDao: ClipDao,
    private val context: Context
) {
    val allClips: Flow<List<ClipEntity>> = clipDao.getAllClips()
    val clipCount: Flow<Int> = clipDao.getClipCount()

    fun searchClips(query: String): Flow<List<ClipEntity>> {
        return if (query.isBlank()) clipDao.getAllClips() else clipDao.searchClips(query)
    }

    suspend fun saveClip(text: String, maxRetentionCount: Int = 1000) {
        if (text.isBlank()) return
        val entity = ClipEntity(content = text)
        clipDao.insertClip(entity)
        // Enforce FIFO Retention Policy
        clipDao.trimToMaxCount(maxRetentionCount)
    }

    suspend fun deleteClip(clipId: Long) {
        clipDao.deleteById(clipId)
    }

    suspend fun getLatestClipText(): String? {
        return clipDao.getLatestClip()?.content
    }

    // Bridge saved item back to Android OS Primary Clip
    fun copyToSystemClipboard(text: String, label: String = "Custom Clipboard") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
}
