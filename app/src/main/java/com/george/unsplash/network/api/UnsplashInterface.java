package com.george.unsplash.network.api;

import com.george.unsplash.network.models.Statistic.Statistic;
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
import retrofit2.http.PUT;
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
    Call<User> getUserData(@Path("username") String username);

    @GET("topics")
    Call<List<Topic>> getTopics();

    @GET("topics/{id_or_slug}")
    Call<Topic> getTopic(@Path("id_or_slug") String slug);

    @GET("/topics/{id_or_slug}/photos")
    Call<List<Photo>> getTopicPhotos(@Path("id_or_slug") String slug,
                                     @Query("page") int page,
                                     @Query("per_page") int perPage);

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
                            @Query("orientation") String orientation,
                            @Query("per_page") int perPage);

    @GET("users/{username}/photos")
    Call<List<Photo>> getUserPhotos(@Path("username") String username,
                                    @Query("page") int page,
                                    @Query("per_page") int perPage);

    @GET("users/{username}/likes")
    Call<List<Photo>> getUserLikePhotos(@Path("username") String username,
                                        @Query("page") int page);

    @GET("/users/{username}/collections")
    Call<List<CollectionPhotos>> getUserCollection(@Path("username") String username,
                                                   @Query("page") int page);

    @GET("/collections/{id}/photos")
    Call<List<Photo>> getCollectionPhotos(@Path("id") String id,
                                          @Query("page") int page);

    @POST("collections")
    Call<CollectionPhotos> createNewCollection(@Query("title") String title,
                                               @Query("description") String description,
                                               @Query("private") boolean isPrivate);

    @PUT("collections/{id}")
    Call<CollectionPhotos> updateCollection(@Path("id") String id,
                                            @Query("title") String title,
                                            @Query("description") String description,
                                            @Query("private") boolean isPrivate);

    @DELETE("collections/{id}")
    Call<CollectionPhotos> deleteCollection(@Path("id") String id);

    @GET("users/{username}/statistics")
    Call<Statistic> getUserStatistic(@Path("username") String username);

}
