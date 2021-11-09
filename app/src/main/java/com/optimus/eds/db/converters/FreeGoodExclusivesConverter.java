package com.optimus.eds.db.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.db.entities.pricing.FreeGoodExclusives;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.room.TypeConverter;

public class FreeGoodExclusivesConverter {

    @TypeConverter
    public static List<FreeGoodExclusives> fromString(String value) {
        if(value==null)
            return (null);
        Type listType = new TypeToken<ArrayList<FreeGoodExclusives>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<FreeGoodExclusives> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}
