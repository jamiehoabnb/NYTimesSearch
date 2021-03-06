package com.codepath.nytimessearch.models;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Parcel
//Ice Pick does not support @Parcel so using Serializable here.
public class Settings implements Serializable {

    private Date beginDate;
    private Date endDate;

    public enum SortOrder {
        newest, oldest;
    }

    private SortOrder sortOrder;

    public enum NewsDesk {
        Business, Foreign, Movies, Sports, Styles, Travel;
    }

    private List<NewsDesk> newsDesks;

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<NewsDesk> getNewsDesks() {
        return newsDesks;
    }

    public void setNewsDesks(List<NewsDesk> newsDesks) {
        this.newsDesks = newsDesks;
    }
}
