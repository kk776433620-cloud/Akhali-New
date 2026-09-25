package com.akhali.smartassistant.network

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiCloudClient(private val apiKey: String = "AQ.Ab8RN6JsWzblic2AQtyxE0rBDnFLp4kOvHmo0DoxxHVy2i85eQ") {

    private val model = GenerativeModel(
        modelName = "gemini-flash-latest",
        apiKey = apiKey
    )

    suspend fun analyzeCommand(userInput: String, screenContext: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            Context: $screenContext
            User Input: $userInput
            Task: Analyze the user input based on the current screen context and return a structured JSON command for the system.
        """.trimIndent()

        val response = model.generateContent(content { text(prompt) })
        return@withContext response.text ?: "ERROR_NO_RESPONSE"
    }

    suspend fun analyzeCommandWithMemory(userInput: String, screenContext: String, memoryContext: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            System Memory Context: $memoryContext
            Current Screen Context: $screenContext
            User Input: $userInput
            Task: Process command taking user memory into account. Output JSON format:
            {
              "action": "CLICK" | "BACK" | "HOME" | "SAVE_MEMORY" | "NONE",
              "target_text": "string if action is CLICK",
              "memory_key": "string if SAVE_MEMORY",
              "memory_value": "string if SAVE_MEMORY",
              "speech_output": "Arabic response for TTS"
            }
        """.trimIndent()

        val response = model.generateContent(content { text(prompt) })
        return@withContext response.text ?: "ERROR_NO_RESPONSE"
    }
}
