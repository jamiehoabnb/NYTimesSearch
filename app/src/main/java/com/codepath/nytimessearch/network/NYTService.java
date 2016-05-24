package com.codepath.nytimessearch.network;

import com.codepath.nytimessearch.models.SearchAPIResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NYTService {

    @GET("/svc/search/v2/articlesearch.json")
    public Call<SearchAPIResponse> search();
}
