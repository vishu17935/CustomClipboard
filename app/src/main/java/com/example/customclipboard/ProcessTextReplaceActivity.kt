package com.example.customclipboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.customclipboard.data.ClipDatabase
import com.example.customclipboard.data.ClipRepository
import kotlinx.coroutines.launch

class ProcessTextReplaceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == Intent.ACTION_PROCESS_TEXT) {
            val isReadOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)

            if (isReadOnly) {
                Toast.makeText(this, "Target text box is read-only", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            val dao = ClipDatabase.getDatabase(applicationContext).clipDao()
            val repository = ClipRepository(dao, applicationContext)

            lifecycleScope.launch {
                val latestText = repository.getLatestClipText()

                if (!latestText.isNullOrEmpty()) {
                    repository.copyToSystemClipboard(latestText)

                    val resultIntent = Intent().apply {
                        putExtra(Intent.EXTRA_PROCESS_TEXT, latestText)
                    }
                    setResult(RESULT_OK, resultIntent)
                    Toast.makeText(applicationContext, "Replaced from Custom Clipboard", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Custom Clipboard is empty", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        } else {
            finish()
        }
    }
}
