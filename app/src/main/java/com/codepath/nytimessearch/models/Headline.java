package com.codepath.nytimessearch.models;

import java.io.Serializable;

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
