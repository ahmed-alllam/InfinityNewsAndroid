package com.bitnews.bitnews.data.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIService {
    private static final String BASE_URL = "http://192.168.1.24:8000";
    private static APIEndpoints endpoints;

    public static APIEndpoints getService() {
        if (endpoints == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            endpoints = retrofit.create(APIEndpoints.class);
        }
        return endpoints;
    }
}
