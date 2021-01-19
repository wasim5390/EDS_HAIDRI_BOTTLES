package com.optimus.eds.db.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optimus.eds.ui.merchandize.MerchandiseImage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By apple on 4/24/19
 */

public class MerchandiseItemConverter {

    @TypeConverter
    public static List<MerchandiseImage> fromString(String value) {
        if(value==null)
            return (null);
        Type listType = new TypeToken<ArrayList<MerchandiseImage>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<MerchandiseImage> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
