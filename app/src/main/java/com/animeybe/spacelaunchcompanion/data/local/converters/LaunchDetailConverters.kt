package com.animeybe.spacelaunchcompanion.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LaunchDetailConverters {

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        return if (value == null) null else Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return if (list == null) null else Gson().toJson(list)
    }

    @TypeConverter
    fun fromProgramList(value: String?): List<com.animeybe.spacelaunchcompanion.domain.model.Program>? {
        return if (value == null) null else Gson().fromJson(value, object : TypeToken<List<com.animeybe.spacelaunchcompanion.domain.model.Program>>() {}.type)
    }

    @TypeConverter
    fun fromProgramList(list: List<com.animeybe.spacelaunchcompanion.domain.model.Program>?): String? {
        return if (list == null) null else Gson().toJson(list)
    }
}