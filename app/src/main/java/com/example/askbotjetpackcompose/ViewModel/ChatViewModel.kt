package com.example.askbotjetpackcompose.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.askbotjetpackcompose.BuildConfig
import com.example.askbotjetpackcompose.Models.MessageModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    //"gemini-1.5-flash"
    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }
    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.API_KEY

    )

    fun sendMessage(question: String) {
        //Log.e("question ask: ", "" + question)

        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) {
                            text(it.message)
                        }
                    }.toList()
                )
                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("Typing.....", "model"))

                val response = chat.sendMessage(question)
                messageList.removeLast()
                messageList.add(MessageModel(response.text.toString(), "model"))
                //Log.e("Response: ", "" + response.text.toString())
            } catch (e: Exception) {
                messageList.removeLast()
                messageList.add(MessageModel("Error: " + e.message.toString(), "model"))
            }

        }
    }
}