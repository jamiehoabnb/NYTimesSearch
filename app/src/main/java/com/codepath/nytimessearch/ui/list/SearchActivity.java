package com.codepath.nytimessearch.ui.list;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Doc;
import com.codepath.nytimessearch.models.Settings;
import com.codepath.nytimessearch.network.NYTAPI;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

public class SearchActivity extends AppCompatActivity implements
        SettingsFragment.SettingsDialogListener,
        SearchSuggestionAdaptor.SearchSuggestionOnClickListener,
        ListGridView.ListGridViewHolder {

    @BindView(R.id.rvResults)
    RecyclerView rvGridView;

    ListGridView listGridView;

    @BindView(R.id.tbSearch)
    Toolbar toolbar;

    @State ArrayList<Doc> docs;

    SearchSuggestionAdaptor searchSuggestionAdaptor;

    SearchView searchView;

    ListProgressBar progressBar;

    //Need to cache query for endless scroll requests.
    @State String query;

    @State Settings settings;

    @State LinkedHashSet<String> queryHistory;

    private static final int NUM_COLS_PORTRAIT = 4;
    private static final int NUM_COLS_LANDSCAPE = 6;

    private static final String[] CURSOR_COLUMNS = new String[]{"_id", "query"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (docs == null) {
            docs = new ArrayList<>();
        }

        if (queryHistory == null) {
            queryHistory = new LinkedHashSet<>();
        }

        int numCols =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                        NUM_COLS_PORTRAIT : NUM_COLS_LANDSCAPE;

        listGridView = new ListGridView(this, rvGridView, docs, toolbar, numCols);

        if (settings == null) {
            settings = new Settings();
        }
    }

    public Context getContext() {
        return this;
    }

    public void onLoadMore(int page) {
        if (query == null) {
            return;
        }

        //Add another page of articles for endless scroll.
        NYTAPI.search(query, page, settings, listGridView);
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
        final SearchActivity searchActivity = this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                //Cache the query.
                searchActivity.query = query;
                if (! queryHistory.contains(query)) {
                    queryHistory.add(query);
                }

                //Get first page of query.
                progressBar.showProgressBar();
                NYTAPI.search(query, 0, settings, listGridView);
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
        progressBar = new ListProgressBar(actionProgressItem);
        listGridView.setProgressBar(progressBar);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSearchSuggestionClick(String query) {
        this.query = query;
        searchView.setQuery(query, true);
        NYTAPI.search(query, 0, settings, listGridView);
        searchView.clearFocus();
    }

    private void loadSearchSuggestions(String searchText) {
        //Load query history.
        MatrixCursor cursor = new MatrixCursor(CURSOR_COLUMNS);
        int i = 0;
        for (String q: queryHistory) {
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
    }
}
