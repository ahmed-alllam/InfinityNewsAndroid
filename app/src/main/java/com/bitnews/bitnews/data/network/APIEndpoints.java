package com.bitnews.bitnews.data.network;

import com.bitnews.bitnews.data.models.AuthToken;
import com.bitnews.bitnews.data.models.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIEndpoints {

    @FormUrlEncoded
    @POST
    public Call<User> signUp(@Field("first_name") String firstName,
                             @Field("last_name") String lastName,
                             @Field("username") String userName,
                             @Field("password") String password);

    @FormUrlEncoded
    @POST
    public Call<AuthToken> logIn(@Field("username") String username,
                                 @Field("password") String password);
}
