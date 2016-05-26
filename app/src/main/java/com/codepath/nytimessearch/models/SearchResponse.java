package com.codepath.nytimessearch.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {

    List<Doc> docs;

    SearchResponse() {
        docs = new ArrayList<>();
    }

    public static SearchResponse parseJSON(String response) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = gsonBuilder.create();
        SearchResponse resp = gson.fromJson(response, SearchResponse.class);
        return resp;
    }

    public List<Doc> getDocs() {
        return docs;
    }
}