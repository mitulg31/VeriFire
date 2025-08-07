package com.kopa_samchu.VeriFire

import android.content.Context
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

data class ClassificationResult(val isSpam: Boolean, val spamType: String)

class SpamClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var vocabulary: Map<String, Int>? = null
    private val modelFileName = "VeriFire_Spam_Model.tflite"
    private val vocabFileName = "tfidf_vocabulary.json"
    private val maxFeatures = 5000

    @Throws(IOException::class)
    fun initialize() {
        interpreter = Interpreter(loadModelFile())
        vocabulary = loadVocabulary()
    }

    fun classify(message: String): ClassificationResult {
        if (interpreter == null || vocabulary == null) {
            throw IllegalStateException("Classifier has not been initialized.")
        }

        val inputArray = preprocessText(message)
        val modelInput = arrayOf(inputArray)
        val outputArray = Array(1) { FloatArray(2) }
        interpreter?.run(modelInput, outputArray)

        val probabilities = outputArray[0]
        val isSpam = probabilities[1] > probabilities[0]

        var spamType = "Spam"
        if (isSpam) {
            spamType = categorizeSpam(message)
        }

        return ClassificationResult(isSpam, spamType)
    }

    private fun categorizeSpam(message: String): String {
        val lowerCaseMessage = message.lowercase()
        return when {
            "prize" in lowerCaseMessage || "won" in lowerCaseMessage || "claim" in lowerCaseMessage || "congratulations" in lowerCaseMessage -> "Prize Scam"
            "account" in lowerCaseMessage || "suspended" in lowerCaseMessage || "verify" in lowerCaseMessage || "bank" in lowerCaseMessage -> "Phishing"
            "offer" in lowerCaseMessage || "sale" in lowerCaseMessage || "discount" in lowerCaseMessage -> "Marketing"
            else -> "General Spam"
        }
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }

    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelFileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    @Throws(IOException::class)
    private fun loadVocabulary(): Map<String, Int> {
        val jsonString = context.assets.open(vocabFileName).bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val vocabMap = mutableMapOf<String, Int>()
        jsonObject.keys().forEach { key ->
            vocabMap[key] = jsonObject.getInt(key)
        }
        return vocabMap
    }

    private fun preprocessText(text: String): FloatArray {
        val cleanText = text.lowercase().replace(Regex("[^a-z\\s]"), "")
        val tokens = cleanText.split(Regex("\\s+")).filter { it.isNotEmpty() }
        val wordCounts = tokens.groupingBy { it }.eachCount()
        val vector = FloatArray(maxFeatures)
        for ((word, _) in wordCounts) {
            val index = vocabulary?.get(word)
            if (index != null && index < maxFeatures) {
                vector[index] = 1.0f
            }
        }
        return vector
    }
}