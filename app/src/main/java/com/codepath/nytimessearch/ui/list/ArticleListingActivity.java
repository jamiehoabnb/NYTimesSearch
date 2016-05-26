package com.codepath.nytimessearch.ui.list;

import android.app.FragmentManager;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Settings;
import com.codepath.nytimessearch.ui.list.slidingtab.SlidingTabLayout;

import java.util.LinkedHashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

public class ArticleListingActivity extends AppCompatActivity implements
        SettingsFragment.SettingsDialogListener,
        SearchSuggestionAdaptor.SearchSuggestionOnClickListener {

    @BindView(R.id.tbSearch)
    Toolbar toolbar;

    @BindView(R.id.tbLayout)
    ViewGroup tbLayout;

    SearchSuggestionAdaptor searchSuggestionAdaptor;

    SearchView searchView;

    @State
    LinkedHashSet<String> queryHistory;

    @State
    String query;

    @State
    Settings settings;

    private static final String[] CURSOR_COLUMNS = new String[]{"_id", "query"};

    @BindView(R.id.pager)
    ViewPager pager;

    ViewPagerAdapter adapter;

    @BindView(R.id.tabs)
    SlidingTabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_article_listing);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (queryHistory == null) {
            queryHistory = new LinkedHashSet<>();
        }

        if (settings == null) {
            settings = new Settings();
        }

        //Set up fragments now that we have a toolbar and progress bar for them to use.
        SearchArticleListingFragment.setSettings(settings);
        SearchArticleListingFragment.setQuery(query);

        CharSequence titles[]={
                getString(R.string.top_stories),
                getResources().getString(R.string.business),
                getResources().getString(R.string.foreign),
                getResources().getString(R.string.movies),
                getResources().getString(R.string.sports),
                getResources().getString(R.string.styles),
                getResources().getString(R.string.travel),
                getResources().getString(R.string.search)
        };

        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),titles);
        pager.setAdapter(adapter);

        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        tabs.setViewPager(pager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        //Set up the search manager.
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Set up the query text listener.
        final ArticleListingActivity searchActivity = this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                if (!queryHistory.contains(query)) {
                    queryHistory.add(query);
                }

                //Switch to the search tab.
                tabs.getViewPager().setCurrentItem(ViewPagerAdapter.NUM_TABS-1);

                //Get first page of query.
                ListProgressBar.getInstance().showProgressBar();
                searchActivity.query = query;
                SearchArticleListingFragment.search(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                loadSearchSuggestions(s);
                return true;
            }
        });

        searchSuggestionAdaptor = new SearchSuggestionAdaptor(this, R.layout.item_search_suggestion,
                null, CURSOR_COLUMNS, null, -1000, this);
        searchView.setSuggestionsAdapter(searchSuggestionAdaptor);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem actionProgressItem = menu.findItem(R.id.miActionProgress);
        ListProgressBar.createInstance(actionProgressItem);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSearchSuggestionClick(String query) {
        searchView.setQuery(query, true);
        this.query = query;
        SearchArticleListingFragment.search(query);
        searchView.clearFocus();
    }

    private void loadSearchSuggestions(String searchText) {
        //Load query history.
        MatrixCursor cursor = new MatrixCursor(CURSOR_COLUMNS);
        int i = 0;
        for (String q : queryHistory) {
            String[] temp = new String[2];
            temp[0] = Integer.toString(i);
            temp[1] = q;
            cursor.addRow(temp);
            i++;
        }

        searchSuggestionAdaptor.changeCursor(cursor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.preferences:
                FragmentManager fm = getFragmentManager();
                SettingsFragment settingsFragment = SettingsFragment.newInstance(settings);
                settingsFragment.show(fm, "settings");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFinishDialog(Settings settings) {
        this.settings = settings;
        SearchArticleListingFragment.setSettings(settings);
    }
}
