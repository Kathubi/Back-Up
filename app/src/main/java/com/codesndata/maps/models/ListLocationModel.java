package com.codesndata.maps.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Collins on 9/30/2017 @ 02:53 PM.
 * Package Name: ${PACKAGE_NAME}
 * Project Name : MapMarker
 */

public class ListLocationModel {
    @SerializedName("data")
    private List<LocationModel> mData;

    public ListLocationModel(List<LocationModel> mData) {
        this.mData = mData;
    }

    public List<LocationModel> getmData() {
        return mData;
    }

}
