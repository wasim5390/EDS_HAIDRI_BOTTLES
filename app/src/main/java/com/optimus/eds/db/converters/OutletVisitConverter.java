package com.optimus.eds.db.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.OutletVisit;
import com.optimus.eds.model.LastOrder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.room.TypeConverter;

public class OutletVisitConverter {

    @TypeConverter
    public static List<OutletVisit> fromString(String value) {
        if(value==null)
            return (null);
        Type listType = new TypeToken<ArrayList<OutletVisit>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<OutletVisit> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}
