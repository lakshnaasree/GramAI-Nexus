package com.gramainexus.smartvillage.data

data class FarmerProfile(
    // Existing farmer profile
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val village: String = "",
    val district: String = "",
    val state: String = "",
    val landArea: String = "",
    val crops: String = "",
    val profilePhotoUrl: String = "",

    // Employment profile
    val about: String = "",
    val education: String = "",
    val skills: String = "",
    val experience: String = "",
    val certifications: String = "",
    val languages: String = "",
    val openToWork: Boolean = false,
    val resumePath: String = ""
)