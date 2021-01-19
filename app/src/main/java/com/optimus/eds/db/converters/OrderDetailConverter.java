package com.optimus.eds.db.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optimus.eds.db.entities.OrderDetail;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailConverter {

    @TypeConverter
    public static List<OrderDetail> fromString(String value) {
        if(value==null)
            return (null);
        Type listType = new TypeToken<ArrayList<OrderDetail>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<OrderDetail> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
