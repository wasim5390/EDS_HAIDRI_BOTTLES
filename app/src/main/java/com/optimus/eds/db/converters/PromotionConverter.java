package com.optimus.eds.db.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optimus.eds.db.entities.Promotion;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.room.TypeConverter;

public class PromotionConverter {

    @TypeConverter
    public static List<Promotion> fromString(String value) {
        if(value==null)
            return (null);
        Type listType = new TypeToken<ArrayList<Promotion>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<Promotion> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
