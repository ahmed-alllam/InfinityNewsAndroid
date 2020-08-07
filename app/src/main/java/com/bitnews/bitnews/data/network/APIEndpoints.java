package com.bitnews.bitnews.data.network;

import com.bitnews.bitnews.data.models.AuthToken;
import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.data.models.Post;
import com.bitnews.bitnews.data.models.ResponseList;
import com.bitnews.bitnews.data.models.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIEndpoints {

    @FormUrlEncoded
    @POST("users/signup/")
    Single<User> signUp(@Field("first_name") String firstName,
                        @Field("last_name") String lastName,
                        @Field("username") String userName,
                        @Field("password") String password);

    @FormUrlEncoded
    @POST("users/signup/")
    Single<User> singUpAsGuest(@Field("is_guest") boolean isGuest);

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
    Single<ResponseList<Category>> getAllCategories(@Query("cursor") String cursor);

    @GET("users/me/favourite-categories")
    Single<List<Category>> getFavouriteCategories();

    @PUT("users/me/favourite-categories")
    Completable updateFavouriteCategories(@Body RequestBody requestBody);

    @GET("news/categories/{slug}/posts")
    Single<ResponseList<Post>> getPostsByCategory(@Path("slug") String categorySlug,
                                                  @Query("cursor") String cursor);

    @GET("news/posts/{slug}")
    Single<Post> getPost(@Path("slug") String postSlug);
}
