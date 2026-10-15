package com.gramainexus.smartvillage.ai

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PlantDiseaseClassifier(
    private val context: Context
) {

    private var interpreter: Interpreter? = null

    private val inputSize = 224
    private val numChannels = 3

    init {
        loadModel()
    }

    private fun loadModel() {

        val modelBytes =
            context.assets.open("dr_disease_model.tflite").use { inputStream ->
                inputStream.readBytes()
            }

        val modelBuffer =
            ByteBuffer.allocateDirect(modelBytes.size)
                .order(ByteOrder.nativeOrder())

        modelBuffer.put(modelBytes)
        modelBuffer.rewind()

        interpreter = Interpreter(modelBuffer)

        val inputTensor = interpreter!!.getInputTensor(0)
        val outputTensor = interpreter!!.getOutputTensor(0)

        android.util.Log.d(
            "PlantDoctorAI",
            "INPUT shape = ${inputTensor.shape().contentToString()}"
        )

        android.util.Log.d(
            "PlantDoctorAI",
            "INPUT type = ${inputTensor.dataType()}"
        )

        android.util.Log.d(
            "PlantDoctorAI",
            "OUTPUT shape = ${outputTensor.shape().contentToString()}"
        )

        android.util.Log.d(
            "PlantDoctorAI",
            "OUTPUT type = ${outputTensor.dataType()}"
        )
    }

    fun classify(bitmap: Bitmap): FloatArray {

        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            inputSize,
            inputSize,
            true
        )

        val inputBuffer =
            ByteBuffer.allocateDirect(
                4 * inputSize * inputSize * numChannels
            ).order(ByteOrder.nativeOrder())

        val pixels =
            IntArray(inputSize * inputSize)

        resizedBitmap.getPixels(
            pixels,
            0,
            inputSize,
            0,
            0,
            inputSize,
            inputSize
        )

        android.util.Log.d(
            "PlantDoctorAI",
            "First pixel RGB = ${
                ((pixels[0] shr 16) and 0xFF)
            }, ${
                ((pixels[0] shr 8) and 0xFF)
            }, ${
                (pixels[0] and 0xFF)
            }"
        )
        for (pixel in pixels) {



            val red = ((pixel shr 16) and 0xFF) / 255.0f
            val green = ((pixel shr 8) and 0xFF) / 255.0f
            val blue = (pixel and 0xFF) / 255.0f

            inputBuffer.putFloat(red)
            inputBuffer.putFloat(green)
            inputBuffer.putFloat(blue)
        }

        val output = Array(1) { FloatArray(15) }
        interpreter?.run(inputBuffer, output)

        output[0].forEachIndexed { index, score ->
            android.util.Log.d(
                "PlantDoctorAI",
                "Class $index = $score"
            )
        }

        return output[0]
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}