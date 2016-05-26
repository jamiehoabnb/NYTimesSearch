package com.codepath.nytimessearch.ui.list;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Settings;
import com.codepath.nytimessearch.network.NYTAPI;

public class AllArticleListingFragment extends BaseArticleListingFragment {

    private Settings settings;

    private ListGridView listGridView;

    public AllArticleListingFragment() {
        super();

        //This fragment lists all articles sorted by newest.
        settings = new Settings();
        settings.setSortOrder(Settings.SortOrder.newest);
    }

    @Override
    public int getGridViewId() {
        return R.id.rvAllResults;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_all_article_listing;
    }

    @Override
    public void init() {
        NYTAPI.search(null, 0, settings, listGridView);
    }

    @Override
    public void onLoadMore(int page) {
        //Add another page of articles for endless scroll.
        NYTAPI.search(null, page, settings, listGridView);
    }

    @Override
    public void setListGridView(ListGridView listGridView) {
        this.listGridView = listGridView;
    }
}
