package com.akhali.smartassistant.engine

import android.content.Context
import com.akhali.smartassistant.database.AppDatabase
import com.akhali.smartassistant.database.MemoryEntity
import com.akhali.smartassistant.network.GeminiCloudClient
import com.akhali.smartassistant.services.SystemControlAccessibilityService
import org.json.JSONObject

class DecisionOrchestrator(
    private val context: Context,
    private val geminiClient: GeminiCloudClient,
    private val accessibilityService: SystemControlAccessibilityService?
) {

    private val memoryDao = AppDatabase.getDatabase(context).memoryDao()

    suspend fun processUserCommand(userInput: String, screenText: String): String {
        // 1. Fetch relevant memory context
        val userPreferences = memoryDao.getMemoriesByCategory("preference")
        val contextHistory = memoryDao.getMemoriesByCategory("history")

        val memoryContext = userPreferences.joinToString("; ") { "${it.key}: ${it.value}" } +
                " | Recent History: " + contextHistory.take(5).joinToString("; ") { it.value }

        // 2. Query Gemini AI with Full Context (Screen + Memory)
        val aiResponseJson = geminiClient.analyzeCommandWithMemory(userInput, screenText, memoryContext)

        // 3. Save command to long-term memory
        memoryDao.saveMemory(MemoryEntity(key = "last_command", value = userInput, category = "history"))

        // 4. Parse & Execute System Action via Accessibility Service
        return executeSystemAction(aiResponseJson)
    }

    private suspend fun executeSystemAction(jsonResponse: String): String {
        return try {
            val json = JSONObject(jsonResponse)
            val action = json.optString("action", "NONE")
            val targetText = json.optString("target_text", "")
            val responseSpeech = json.optString("speech_output", "تم تنفيذ الأمر.")

            when (action) {
                "BACK" -> accessibilityService?.performGlobalBack()
                "HOME" -> accessibilityService?.performGlobalHome()
                "CLICK" -> if (targetText.isNotEmpty()) accessibilityService?.clickOnViewByText(targetText)
                "SAVE_MEMORY" -> {
                    val key = json.optString("memory_key")
                    val value = json.optString("memory_value")
                    if (key.isNotEmpty()) {
                        memoryDao.saveMemory(MemoryEntity(key = key, value = value, category = "preference"))
                    }
                }
            }

            responseSpeech
        } catch (e: Exception) {
            jsonResponse
        }
    }
}
