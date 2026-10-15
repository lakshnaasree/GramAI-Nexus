
package com.gramainexus.smartvillage.data

data class PlantDiagnosis(
    val plantName: String = "",
    val status: String = "",
    val diseaseName: String = "",
    val confidence: Float = 0f,
    val symptoms: List<String> = emptyList(),
    val treatment: List<String> = emptyList(),
    val prevention: List<String> = emptyList(),
    val advice: String = ""
)

