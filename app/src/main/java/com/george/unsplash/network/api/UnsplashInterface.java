package com.george.unsplash.network.api;

import com.george.unsplash.network.models.collection.CollectionPhotos;
import com.george.unsplash.network.models.search.Search;
import com.george.unsplash.network.models.topic.Topic;
import com.george.unsplash.network.models.user.Token;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.user.Me;
import com.george.unsplash.network.models.user.common.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UnsplashInterface {

    @POST("oauth/token")
    Call<Token> getToken(@Query("client_id") String client_id,
                         @Query("client_secret") String client_secret,
                         @Query("redirect_uri") String redirect_uri,
                         @Query("code") String code,
                         @Query("grant_type") String grant_type);
    @GET("me")
    Call<Me> getMeData();

    @GET("users/{username}")
    Call<User> getUserData(@Path("username") String userName);

    @GET("photos/random")
    Call<Photo> getRandomPhoto(@Query("orientation") String orientation);

    @GET("topics")
    Call<List<Topic>> getTopics();

    @GET("topics/{id_or_slug}")
    Call<Topic> getTopic(@Path("id_or_slug") String slug);

    @GET("/topics/{id_or_slug}/photos")
    Call<List<Photo>> getTopicPhotos(@Path("id_or_slug") String slug,
                                     @Query("page") int page);

    @GET("photos/{id}")
    Call<Photo> getPhoto(@Path("id") String id);

    @POST("photos/{id}/like")
    Call<Photo> likePhoto(@Path("id") String id);

    @DELETE("photos/{id}/like")
    Call<Photo> unlikePhoto(@Path("id") String id);

    @GET("search/photos")
    Call<Search> findPhotos(@Query("query") String query,
                            @Query("page") int page,
                            @Query("color") String color,
                            @Query("orientation") String orientation);

    @GET("users/{username}/photos")
    Call<List<Photo>> getUserPhotos(@Path("username") String username,
                                    @Query("page") int page,
                                    @Query("per_page") int per_page);

    @GET("users/{username}/likes")
    Call<List<Photo>> getUserLikePhotos(@Path("username") String username,
                                        @Query("page") int page,
                                        @Query("per_page") int per_page);

    @GET("/users/{username}/collections")
    Call<List<CollectionPhotos>> getUserCollection(@Path("username") String username,
                                                   @Query("page") int page,
                                                   @Query("per_page") int per_page);

}
