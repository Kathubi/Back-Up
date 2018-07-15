package com.codesndata.maps.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Collins on 30/09/17 @ 02:54 PM.
 * Package Name: ${PACKAGE_NAME}
 * Project Name : MapMarker
 */

public class LocationModel {
    @SerializedName("serial")
    private String deviceSerialNo;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("time")
    private String time;

    public LocationModel(String deviceSerialNo, String time, String latitude, String longitude) {
        this.deviceSerialNo = deviceSerialNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public String getDeviceSerialNo() {
        return deviceSerialNo;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getTime() {
        return time;
    }

}
