package com.optimus.eds.db.converters;

import com.google.gson.Gson;
import com.optimus.eds.model.LastOrder;

import androidx.room.TypeConverter;

public class LastOrderConverter {

    @TypeConverter
    public static LastOrder fromString(String value) {
        if(value==null)
            return (null);
        return new Gson().fromJson(value, LastOrder.class);
    }

    @TypeConverter
    public static String fromList(LastOrder list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
