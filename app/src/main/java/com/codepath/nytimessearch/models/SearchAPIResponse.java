package com.codepath.nytimessearch.models;

public class SearchAPIResponse {

    SearchResponse response;

    String status;

    public SearchResponse getResponse() {
        return response;
    }

    public String getStatus() {
        return status;
    }
}
