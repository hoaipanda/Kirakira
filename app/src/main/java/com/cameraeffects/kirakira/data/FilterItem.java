package com.cameraeffects.kirakira.data;

import android.graphics.Bitmap;

import com.cameraeffects.kirakira.filter.helper.analog3_MagicFilterType;

import java.io.Serializable;

public class FilterItem implements Serializable{
    private Bitmap bitmap;
    private analog3_MagicFilterType type;
    private String name;

    public FilterItem() {
    }

    public FilterItem(Bitmap bitmap, analog3_MagicFilterType type, String name) {
        this.bitmap = bitmap;
        this.type = type;
        this.name = name;
    }

    public FilterItem(analog3_MagicFilterType type) {
        this.type = type;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public analog3_MagicFilterType getType() {
        return type;
    }

    public void setType(analog3_MagicFilterType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
