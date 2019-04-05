package com.example.sungm.urbanapp.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LuasPoints {
    @SerializedName("features")
    @Expose
    private List<Feature> features = null;

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

}
