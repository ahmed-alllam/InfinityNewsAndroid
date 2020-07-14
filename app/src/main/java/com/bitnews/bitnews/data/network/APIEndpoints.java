package com.bitnews.bitnews.data.network;

import com.bitnews.bitnews.data.models.AuthToken;
import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.data.models.ResponseList;
import com.bitnews.bitnews.data.models.User;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIEndpoints {

    @FormUrlEncoded
    @POST("users/signup/")
    Single<User> signUp(@Field("first_name") String firstName,
                        @Field("last_name") String lastName,
                        @Field("username") String userName,
                        @Field("password") String password);

    @FormUrlEncoded
    @POST("users/signup/")
    Single<User> sinUpAsGuest(@Field("guest") boolean isGuest);

    @FormUrlEncoded
    @POST("users/token/")
    Single<AuthToken> logIn(@Field("username") String username,
                            @Field("password") String password);

    @FormUrlEncoded
    @POST("users/token/")
    Single<AuthToken> logIn(@Field("username") String username);

    @GET("users/me/")
    Single<User> getCurrentUser();

    @GET("news/categories/")
    Single<ResponseList<Category>> getAllCategories();
}
