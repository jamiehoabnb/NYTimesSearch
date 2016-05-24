package com.codepath.nytimessearch.models;

import org.parceler.Parcel;

import java.io.Serializable;

@Parcel
public class Headline {

    String main;

    String printHeadline;

    public String getMain() {
        return main;
    }

    public String getPrintHeadline() {
        return printHeadline;
    }
}
