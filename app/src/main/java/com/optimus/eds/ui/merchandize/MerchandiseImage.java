package com.optimus.eds.ui.merchandize;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created By apple on 4/23/19
 */



public class MerchandiseImage implements Serializable {

    int id;
    String path;
    String image;
    int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBase64Image() {
        return image;
    }

    public void setBase64Image(String image) {
        this.image = image;
    }
}
