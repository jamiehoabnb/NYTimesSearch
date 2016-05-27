package com.codepath.nytimessearch.models;

import org.parceler.Parcel;

import java.io.Serializable;

@Parcel
//Ice Pick does not support @Parcel so using Serializable here.
public class Multimedia implements Serializable {
    String url;
    String type;
    String subtype;

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }
}
