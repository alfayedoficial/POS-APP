package com.silkysys.pos.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.silkysys.pos.data.model.auth.user.get.Business
import com.silkysys.pos.data.model.auth.user.get.Locations
import java.lang.reflect.Type

class Converters {

    // Converter for list of locations
    @TypeConverter
    fun fromLocationsList(list: List<Locations?>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toLocationsList(value: String?): List<Locations?>? {
        val listType: Type = object : TypeToken<List<Locations?>?>() {}.type
        return Gson().fromJson<List<Locations?>>(value, listType)
    }


    // Converter for Business object
    @TypeConverter
    fun fromBusiness(business: Business) = business.name

    @TypeConverter
    fun toBusiness(name: String) = Business(name)
}