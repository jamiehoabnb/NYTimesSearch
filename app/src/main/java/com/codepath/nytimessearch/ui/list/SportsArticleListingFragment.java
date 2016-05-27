package com.codepath.nytimessearch.ui.list;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Settings;
import com.codepath.nytimessearch.network.NYTAPI;

import java.util.LinkedList;
import java.util.List;

public class SportsArticleListingFragment extends BaseArticleListingFragment {

    private Settings settings;

    public SportsArticleListingFragment() {
        super();

        //This fragment lists all articles sorted by newest.
        settings = new Settings();
        settings.setSortOrder(Settings.SortOrder.newest);

        List<Settings.NewsDesk> newsDesks = new LinkedList<>();
        newsDesks.add(Settings.NewsDesk.Sports);
        settings.setNewsDesks(newsDesks);
    }

    @Override
    public int getGridViewId() {
        return R.id.rvSportsResults;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sports_article_listing;
    }

    @Override
    public void init() {
        NYTAPI.search(null, 0, settings, listGridView);
    }

    @Override
    public void onLoadMore(int page) {
        //Add another page of articles for endless scroll.
        ListProgressBar.getInstance().showProgressBar();
        NYTAPI.search(null, page, settings, listGridView);
    }
}
