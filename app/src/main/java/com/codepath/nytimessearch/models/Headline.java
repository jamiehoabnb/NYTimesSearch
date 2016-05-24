package com.codepath.nytimessearch.models;

import org.parceler.Parcel;
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
