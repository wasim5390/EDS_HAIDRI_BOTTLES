package com.optimus.eds.db.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optimus.eds.db.entities.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductConverter {
    @TypeConverter
    public static List<Product> fromString(String value) {
        if (value == null) {
            return (null);
        }
        Type listType = new TypeToken<ArrayList<Product>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<Product> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
