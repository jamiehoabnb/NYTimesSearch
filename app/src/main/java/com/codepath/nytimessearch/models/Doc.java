package com.codepath.nytimessearch.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.List;

@Parcel
//Ice Pick does not support @Parcel so using Serializable here.
public class Doc implements Serializable {

    @SerializedName("web_url")
    String webUrl;

    String snippet;
    Headline headline;
    List<Multimedia> multimedia;

    public String getWebUrl() {
        return webUrl;
    }

    public String getSnippet() {
        return snippet;
    }

    public Headline getHeadline() {
        return headline;
    }

    public List<Multimedia> getMultimedia() {
        return multimedia;
    }
}
