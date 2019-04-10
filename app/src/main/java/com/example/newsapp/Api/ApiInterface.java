package com.example.newsapp.Api;

import com.example.newsapp.models.NewsModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("top-headlines")
    Call<NewsModel> getnews(
       @Query("country") String country,
       @Query("apiKey") String apiKey
    ) ;

    @GET("everything")
    Call<NewsModel> GetSearchNews(
            @Query("q") String Keyword,
            @Query("language") String language,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey
    );
}
