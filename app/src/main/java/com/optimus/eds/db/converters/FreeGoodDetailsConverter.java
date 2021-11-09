package com.optimus.eds.db.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optimus.eds.db.entities.pricing.FreeGoodDetails;
import com.optimus.eds.db.entities.pricing.FreeGoodExclusives;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.room.TypeConverter;

public class FreeGoodDetailsConverter {

    @TypeConverter
    public static List<FreeGoodDetails> fromString(String value) {
        if(value==null)
            return (null);
        Type listType = new TypeToken<ArrayList<FreeGoodDetails>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<FreeGoodDetails> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}
