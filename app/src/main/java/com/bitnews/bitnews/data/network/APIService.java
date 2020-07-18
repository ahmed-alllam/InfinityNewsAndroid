package com.bitnews.bitnews.data.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bitnews.bitnews.data.db.AppDatabase;
import com.bitnews.bitnews.data.db.dao.AuthTokenDao;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIService {
    private static final String BASE_URL = "http://192.168.1.24:8000";
    private static APIEndpoints endpoints;

    public static APIEndpoints getService(Context context) {
        if (endpoints == null) {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.interceptors().add(new RequestTokenInterceptor(context));

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

class RequestTokenInterceptor implements Interceptor {
    private AuthTokenDao authTokenDao;

    RequestTokenInterceptor(Context context) {
        authTokenDao = AppDatabase.getInstance(context).getAuthTokenDao();
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder request = chain.request().newBuilder();

        if (authTokenDao.isUserAuthenticated().blockingGet()) {
            String token = authTokenDao.getAuthToken().blockingGet().getToken();
            request = request.addHeader("Authorization", "token " + token);
        }

        return chain.proceed(request.build());
    }
}
