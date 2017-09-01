package com.essential.audio.utils

import com.google.gson.Gson

/**
 * Created by dongc on 8/31/2017.
 */
class JsonHelper private constructor() {
    private val mGson: Gson = Gson()

    companion object {
        val instance: JsonHelper = JsonHelper()
    }

    fun toJson(source: Any): String = mGson.toJson(source)

    fun <T> fromJson(json: String, classOfT: Class<T>): T = mGson.fromJson(json, classOfT)
}