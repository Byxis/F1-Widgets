package fr.byxis.f1w.data.local

import android.content.Context
import com.google.gson.Gson
import java.io.File

object RaceStorage {
    private const val FILE_NAME = "f1_cache.json"
    private val gson = Gson()

    fun save(context: Context, data: WidgetData) {
        try {
            val file = File(context.filesDir, FILE_NAME)
            val json = gson.toJson(data)
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun load(context: Context): WidgetData? {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            if (!file.exists()) return null
            val json = file.readText()
            gson.fromJson(json, WidgetData::class.java)
        } catch (_: Exception) {
            null
        }
    }
}