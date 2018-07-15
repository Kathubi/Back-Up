package com.codesndata.maps.services;

//public static final String URL      = "http://192.168.43.58/elitestech/google_marker/";

import com.codesndata.maps.LoggingInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Collins on 30/09/17 @ 02:55 PM.
 * Package Name: ${PACKAGE_NAME}
 * Project Name : MapMarker
 */

public class ApiClient {
    private static final String URL      = "http://192.168.43.58/elitestech/Locator/login/";
    private static Retrofit RETROFIT     = null;

   public static Retrofit getClient(){
        if(RETROFIT==null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new LoggingInterceptor())
                    .build();
            RETROFIT = new Retrofit.Builder()
                    .baseUrl(URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return RETROFIT;
    }
}