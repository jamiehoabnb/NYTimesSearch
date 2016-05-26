package com.codepath.nytimessearch.ui.list;

import android.support.design.widget.TabLayout;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Settings;
import com.codepath.nytimessearch.network.NYTAPI;
import com.codepath.nytimessearch.ui.list.BaseArticleListingFragment;

public class SearchArticleListingFragment extends BaseArticleListingFragment {

    private static String query;

    private static Settings settings;

    private static ListGridView listGridView;

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

    public static void setSettings(Settings settings) {
        SearchArticleListingFragment.settings = settings;
    }

    public static void setQuery(String query) {
        SearchArticleListingFragment.query = query;
    }

    @Override
    public void onLoadMore(int page) {
        //Add another page of articles for endless scroll.
        NYTAPI.search(query, page, settings, listGridView);
    }

    public static void search(String query) {
        SearchArticleListingFragment.query = query;
        NYTAPI.search(query, 0, settings, listGridView);
    }

    @Override
    public void setListGridView(ListGridView listGridView) {
        SearchArticleListingFragment.listGridView = listGridView;
    }

}
