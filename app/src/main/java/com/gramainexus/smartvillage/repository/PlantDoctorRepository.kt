package com.gramainexus.smartvillage.repository

import android.content.Context
import android.graphics.Bitmap
import com.gramainexus.smartvillage.ai.GeminiPlantDoctor
import com.gramainexus.smartvillage.data.PlantDiagnosis

class PlantDoctorRepository(
    private val context: Context
) {

    private val geminiPlantDoctor =
        GeminiPlantDoctor()

    suspend fun analyzePlant(bitmap: Bitmap): PlantDiagnosis {

        val result = geminiPlantDoctor.analyzePlant(bitmap)

        return PlantDiagnosis(
            plantName = extractValue(
                result,
                "Plant Name:"
            ),
            status = extractValue(
                result,
                "Plant Health:"
            ),
            diseaseName = extractValue(
                result,
                "Disease/Problem:"
            ),
            confidence = parseConfidence(result),
            symptoms = extractList(
                result,
                "Symptoms:",
                "Treatment:"
            ),
            treatment = extractList(
                result,
                "Treatment:",
                "Prevention:"
            ),
            prevention = extractList(
                result,
                "Prevention:",
                "Expert Advice:"
            ),
            advice = extractValue(
                result,
                "Expert Advice:"
            )
        )
    }

    private fun extractValue(
        text: String,
        label: String
    ): String {

        val start = text.indexOf(
            label,
            ignoreCase = true
        )

        if (start == -1) {
            return "Not available"
        }

        val valueStart =
            start + label.length

        val end =
            text.indexOf(
                "\n",
                valueStart
            )

        return if (end == -1) {
            text.substring(valueStart).trim()
        } else {
            text.substring(
                valueStart,
                end
            ).trim()
        }
    }

    private fun extractList(
        text: String,
        startLabel: String,
        endLabel: String
    ): List<String> {

        val start =
            text.indexOf(
                startLabel,
                ignoreCase = true
            )

        if (start == -1) {
            return emptyList()
        }

        val contentStart =
            start + startLabel.length

        val end =
            text.indexOf(
                endLabel,
                contentStart,
                ignoreCase = true
            )

        val section =
            if (end == -1) {
                text.substring(contentStart)
            } else {
                text.substring(
                    contentStart,
                    end
                )
            }

        return section
            .lines()
            .map { it.trim() }
            .filter {
                it.startsWith("-")
            }
            .map {
                it.removePrefix("-").trim()
            }
            .filter {
                it.isNotEmpty()
            }
    }

    private fun parseConfidence(
        text: String
    ): Float {

        val confidence =
            extractValue(
                text,
                "Confidence:"
            )

        val percentage =
            Regex("""(\d+(?:\.\d+)?)\s*%""")
                .find(confidence)
                ?.groupValues
                ?.get(1)
                ?.toFloatOrNull()

        if (percentage != null) {
            return (percentage / 100f)
                .coerceIn(0f, 1f)
        }

        return when {
            confidence.contains(
                "high",
                ignoreCase = true
            ) -> 0.90f

            confidence.contains(
                "medium",
                ignoreCase = true
            ) -> 0.70f

            confidence.contains(
                "low",
                ignoreCase = true
            ) -> 0.40f

            else -> 0f
        }
    }

    fun close() {
        // Nothing to close for Gemini.
    }
}