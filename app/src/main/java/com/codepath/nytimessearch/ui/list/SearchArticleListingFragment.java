package com.codepath.nytimessearch.ui.list;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Settings;
import com.codepath.nytimessearch.network.NYTAPI;

import icepick.State;

public class SearchArticleListingFragment extends BaseArticleListingFragment {

    @State String query;

    @State Settings settings;

    public SearchArticleListingFragment() {
        super();
    }

    @Override
    public int getGridViewId() {
        return R.id.rvSearchResults;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search_article_listing;
    }

    @Override
    public void init() {
        if (query != null) {
            NYTAPI.search(query, 0, settings, listGridView);
        }
    }

    @Override
    public void onLoadMore(int page) {
        //Add another page of articles for endless scroll.
        NYTAPI.search(query, page, settings, listGridView);
    }

    public void search(Settings settings, String query) {
        this.query = query;
        this.settings = settings;
        NYTAPI.search(query, 0, settings, listGridView);
    }

}
