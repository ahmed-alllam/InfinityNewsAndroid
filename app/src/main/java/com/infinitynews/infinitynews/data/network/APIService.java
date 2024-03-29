package com.infinitynews.infinitynews.data.network;


import com.infinitynews.infinitynews.data.db.dao.AuthTokenDao;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIService {
    private static final String BASE_URL = "https://vast-caverns-12592.herokuapp.com";
    private static APIEndpoints endpoints;

    public static APIEndpoints getService() {
        if (endpoints == null) {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.interceptors().add(chain -> {
                Request.Builder request = chain.request().newBuilder();

                String token;
                if (!(token = AuthTokenDao.getToken()).isEmpty()) {
                    request = request.addHeader("Authorization", "token " + token);
                }

                return chain.proceed(request.build());
            });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client.build())
                    .build();
            endpoints = retrofit.create(APIEndpoints.class);
        }
        return endpoints;
    }
}
