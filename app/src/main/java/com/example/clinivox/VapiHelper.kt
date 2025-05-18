package com.example.clinivox

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import ai.vapi.android.Vapi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VapiHelper(private val context: Context, private val lifecycle: Lifecycle) {
    private val vapi: Vapi

    init {
        val configuration = Vapi.Configuration(
            publicKey = "b09f18ae-f0be-4f32-b570-1e842ce1e1d9",
            host = "api.vapi.ai" // Opcional
        )
        vapi = Vapi(context, lifecycle, configuration)
    }

    fun startAssistant(
        assistantId: String,
        clienteNome: String,
        onSuccess: OnAssistantSuccess,
        onFailure: OnAssistantFailure
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                vapi.start(
                    assistantId = assistantId,
                    assistantOverrides = mapOf(
                        "variableValues" to mapOf("nome_cliente" to clienteNome)
                    )
                )
                onSuccess.onSuccess()
            } catch (e: Throwable) {
                onFailure.onFailure(e)
            }
        }
    }

    fun stopAssistant() {
        vapi.stop()
    }


}