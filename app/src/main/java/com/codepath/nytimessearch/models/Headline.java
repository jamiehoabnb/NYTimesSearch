package com.codepath.nytimessearch.models;

import org.parceler.Parcel;

import java.io.Serializable;

@Parcel
//Ice Pick does not support @Parcel so using Serializable here.
public class Headline implements Serializable {

    String main;

    String printHeadline;

    public String getMain() {
        return main;
    }

    public String getPrintHeadline() {
        return printHeadline;
    }
}
