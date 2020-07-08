package com.bitnews.bitnews.data.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIService {
    private static final String BASE_URL = "127.0.0.1";
    private APIEndpoints endpoints;

    public APIEndpoints getService() {
        if (endpoints == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            endpoints = retrofit.create(APIEndpoints.class);
        }
        return endpoints;
    }
}
