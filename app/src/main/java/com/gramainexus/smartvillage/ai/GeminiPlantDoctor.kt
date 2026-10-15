package com.gramainexus.smartvillage.ai

import android.graphics.Bitmap
import android.util.Base64
import com.gramainexus.smartvillage.BuildConfig
import com.gramainexus.smartvillage.data.gemini.GeminiClient
import com.gramainexus.smartvillage.data.gemini.GeminiContent
import com.gramainexus.smartvillage.data.gemini.GeminiInlineData
import com.gramainexus.smartvillage.data.gemini.GeminiPart
import com.gramainexus.smartvillage.data.gemini.GeminiRequest
import java.io.ByteArrayOutputStream

class GeminiPlantDoctor {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    suspend fun analyzePlant(bitmap: Bitmap): String {

        if (apiKey.isBlank()) {
            throw Exception("Gemini API key is missing")
        }

        val imageBase64 = bitmapToBase64(bitmap)

        val prompt = """
            You are an AI Plant Doctor for a village agriculture app.

            Analyze the uploaded plant or leaf image carefully.

            Return the result in this exact format:

            Plant Name:
            Plant Health:
            Disease/Problem:
            Confidence:

            Symptoms:
            -
            -
            -

            Treatment:
            -
            -
            -

            Prevention:
            -
            -
            -

            Expert Advice:

            Rules:
            1. Identify the plant if possible.
            2. Identify visible disease or problem if present.
            3. If the plant appears healthy, clearly say Healthy.
            4. Do not invent a disease when there is not enough evidence.
            5. If the image is unclear, clearly say the image is unclear.
            6. Give practical agricultural advice.
            7. Keep the response easy for a farmer to understand.
        """.trimIndent()

        return sendImageToGemini(
            bitmap = bitmap,
            prompt = prompt
        )
    }

    suspend fun analyzeMedicine(bitmap: Bitmap): String {

        if (apiKey.isBlank()) {
            throw Exception("Gemini API key is missing")
        }

        val prompt = """
            You are a medicine information assistant in a village healthcare app.

            Carefully analyze the uploaded medicine/tablet strip, box, bottle,
            label, or medicine package image.

            Your purpose is to provide general medicine information.
            Do NOT prescribe the medicine and do NOT recommend a dosage.

            Return the result in this exact format:

            Medicine Name:
            Strength:
            Common Uses:
            -
            -
            -

            Important Precautions:
            -
            -
            -

            Confidence:

            Safety Advice:

            Rules:
            1. Read the medicine name from the package if it is clearly visible.
            2. Read the strength only if it is clearly visible.
            3. Explain what the medicine is commonly used for.
            4. Mention important general precautions when reasonably known.
            5. Never invent a medicine name from an unclear image.
            6. If the medicine cannot be identified confidently, clearly say:
               "Medicine could not be identified confidently."
            7. Do not provide a dosage or tell the user how many tablets to take.
            8. Do not tell the user to start, stop, or change a medicine.
            9. Do not identify a tablet only from its colour or shape.
            10. If the package text is unclear, ask the user to upload a clearer
                image showing the medicine name and strength.
            11. Keep the explanation simple and easy to understand.
            12. Remind the user to confirm medicine information with a pharmacist
                or doctor when uncertain.
        """.trimIndent()

        return sendImageToGemini(
            bitmap = bitmap,
            prompt = prompt
        )
    }

    private suspend fun sendImageToGemini(
        bitmap: Bitmap,
        prompt: String
    ): String {

        val imageBase64 = bitmapToBase64(bitmap)

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(
                            text = prompt
                        ),
                        GeminiPart(
                            inline_data = GeminiInlineData(
                                mime_type = "image/jpeg",
                                data = imageBase64
                            )
                        )
                    )
                )
            )
        )

        val response = GeminiClient.api.generateContent(
            apiKey = apiKey,
            request = request
        )

        return response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: throw Exception(
                "Gemini returned no analysis"
            )
    }

    private fun bitmapToBase64(
        bitmap: Bitmap
    ): String {

        val outputStream =
            ByteArrayOutputStream()

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            85,
            outputStream
        )

        return Base64.encodeToString(
            outputStream.toByteArray(),
            Base64.NO_WRAP
        )
    }
}