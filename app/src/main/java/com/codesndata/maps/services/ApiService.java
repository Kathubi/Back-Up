package com.codesndata.maps.services;

import com.codesndata.maps.models.ListLocationModel;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Collins on 30/09/17 @ 02:57 PM.
 * Package Name: ${PACKAGE_NAME}
 * Project Name : MapMarker
 */

public interface ApiService {
    @GET("JsonDisplayMarker.php")
    Call<ListLocationModel> getAllLocation();
}