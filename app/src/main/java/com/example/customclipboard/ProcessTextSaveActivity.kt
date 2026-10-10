package com.example.customclipboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.customclipboard.data.ClipDatabase
import com.example.customclipboard.data.ClipRepository
import kotlinx.coroutines.launch

class ProcessTextSaveActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == Intent.ACTION_PROCESS_TEXT) {
            val selectedText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()

            if (!selectedText.isNullOrBlank()) {
                val dao = ClipDatabase.getDatabase(applicationContext).clipDao()
                val repository = ClipRepository(dao, applicationContext)

                lifecycleScope.launch {
                    repository.saveClip(selectedText)
                    Toast.makeText(applicationContext, "Saved to Custom Clipboard", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                finish()
            }
        } else {
            finish()
        }
    }
}
