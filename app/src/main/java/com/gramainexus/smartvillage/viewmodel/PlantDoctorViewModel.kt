
package com.gramainexus.smartvillage.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gramainexus.smartvillage.data.PlantDiagnosis
import com.gramainexus.smartvillage.repository.PlantDoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlantDoctorViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        PlantDoctorRepository(
            application.applicationContext
        )

    private val _diagnosis =
        MutableStateFlow<PlantDiagnosis?>(null)

    val diagnosis: StateFlow<PlantDiagnosis?> =
        _diagnosis.asStateFlow()

    private val _isAnalyzing =
        MutableStateFlow(false)

    val isAnalyzing: StateFlow<Boolean> =
        _isAnalyzing.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    fun analyzePlant(bitmap: Bitmap) {

        viewModelScope.launch {

            _isAnalyzing.value = true
            _errorMessage.value = null

            try {

                val result =
                    repository.analyzePlant(bitmap)

                _diagnosis.value = result

            } catch (e: retrofit2.HttpException) {

                val errorBody =
                    e.response()?.errorBody()?.string()

                e.printStackTrace()

                _errorMessage.value =
                    "HTTP ${e.code()}\n${errorBody ?: "No error details returned"}"

            } catch (e: Exception) {

                e.printStackTrace()

                _errorMessage.value =
                    "${e.javaClass.simpleName}: ${e.message ?: "Unable to analyze plant"}"

            } finally {

                _isAnalyzing.value = false
            }
        }
    }

    fun clearDiagnosis() {

        _diagnosis.value = null
        _errorMessage.value = null
    }

    override fun onCleared() {

        repository.close()

        super.onCleared()
    }
}
