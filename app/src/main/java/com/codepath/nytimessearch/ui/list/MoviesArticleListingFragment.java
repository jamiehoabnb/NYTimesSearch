package com.codepath.nytimessearch.ui.list;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Settings;
import com.codepath.nytimessearch.network.NYTAPI;
import com.codepath.nytimessearch.ui.list.BaseArticleListingFragment;
import com.codepath.nytimessearch.ui.list.ListGridView;

import java.util.LinkedList;
import java.util.List;

public class MoviesArticleListingFragment extends BaseArticleListingFragment {

    private Settings settings;

    public MoviesArticleListingFragment() {
        super();

        //This fragment lists all articles sorted by newest.
        settings = new Settings();
        settings.setSortOrder(Settings.SortOrder.newest);

        List<Settings.NewsDesk> newsDesks = new LinkedList<>();
        newsDesks.add(Settings.NewsDesk.Movies);
        settings.setNewsDesks(newsDesks);
    }

    @Override
    public int getGridViewId() {
        return R.id.rvMoviesResults;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_movies_article_listing;
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
}
