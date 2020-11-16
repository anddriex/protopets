package com.eapp.protopets

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object Utils {
    fun assetFilePath(context: Context, assetName: String): String? {
        val file = File(context.filesDir, assetName)

        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }

        try {
            val inputStream = context.assets.open(assetName)
            inputStream.use { input ->
                val outputStream = FileOutputStream(file)
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return file.absolutePath
        } catch (e: IOException) {
            Log.e("Utils", "Error process asset $assetName to file path")
        }

        return null
    }

    fun topK(scores: FloatArray?, topK: Int): IntArray {
        val values = FloatArray(topK)
        Arrays.fill(values, -Float.MAX_VALUE)
        val ixs = IntArray(topK)
        Arrays.fill(ixs, -1)

        for (i in scores!!.indices) {
            for(j in 0 until topK) {
                if (scores[i] > values[j]) {
                    for (k in (topK - 1) downTo j + 1) {
                        values[k] = values[k - 1]
                        ixs[k] = ixs[k - 1]
                    }
                    values[j] = scores[i]
                    ixs[j] = i
                    break
                }
            }
        }
        return ixs
    }
}