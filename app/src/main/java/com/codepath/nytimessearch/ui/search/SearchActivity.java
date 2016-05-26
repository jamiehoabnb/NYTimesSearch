package com.codepath.nytimessearch.ui.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Doc;
import com.codepath.nytimessearch.models.SearchResponse;
import com.codepath.nytimessearch.models.Settings;
import com.codepath.nytimessearch.network.NYTAPI;
import com.codepath.nytimessearch.ui.article.ArticleActivity;
import com.codepath.nytimessearch.util.EndlessRecyclerViewScrollListener;
import com.codepath.nytimessearch.util.InternetCheckUtil;
import com.codepath.nytimessearch.util.ItemClickSupport;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class SearchActivity extends AppCompatActivity implements
        SettingsFragment.SettingsDialogListener, NYTAPI.SearchResponseListener,
        SearchSuggestionAdaptor.SearchSuggestionOnClickListener {

    @BindView(R.id.rvResults)
    RecyclerView rvResults;

    @BindView(R.id.tbSearch)
    Toolbar toolbar;

    @State ArrayList<Doc> docs;

    DocAdapter adapter;

    SearchSuggestionAdaptor searchSuggestionAdaptor;

    SearchView searchView;

    StaggeredGridLayoutManager gridLayoutManager;

    EndlessRecyclerViewScrollListener currentScrollListener;

    MenuItem miActionProgressItem;

    //Need to cache query for endless scroll requests.
    @State String query;

    @State Settings settings;

    @State LinkedHashSet<String> queryHistory;

    private static final int NUM_COLS_PORTRAIT = 4;
    private static final int NUM_COLS_LANDSCAPE = 6;

    private static final String[] CURSOR_COLUMNS = new String[]{"_id", "query"};

    //Source:  https://rylexr.tinbytes.com/2015/04/27/how-to-hideshow-android-toolbar-when-scrolling-google-play-musics-behavior/
    // The elevation of the toolbar when content is scrolled behind
    private static final float TOOLBAR_ELEVATION = 14f;

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

        adapter = new DocAdapter(docs);
        rvResults.setAdapter(adapter);

        int numCols =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                        NUM_COLS_PORTRAIT : NUM_COLS_LANDSCAPE;

        gridLayoutManager =
                new StaggeredGridLayoutManager(numCols, StaggeredGridLayoutManager.VERTICAL);
        rvResults.setLayoutManager(gridLayoutManager);
        rvResults.setItemAnimator(new SlideInUpAnimator());

        ItemClickSupport.addTo(rvResults).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                        Doc doc = docs.get(position);
                        i.putExtra("doc", Parcels.wrap(doc));
                        startActivity(i);
                    }
                }
        );

        rvResults.addOnScrollListener(getScrollNewListener());

        if (settings == null) {
            settings = new Settings();
        }
    }

    private EndlessRecyclerViewScrollListener getScrollNewListener() {
        final SearchActivity searchActivity = this;
        currentScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(final int page, final int totalItemsCount) {
                if (query == null) {
                    return;
                }

                //Add another page of articles for endless scroll.
                NYTAPI.search(query, page, settings, searchActivity);
            }

            // Keeps track of the overall vertical offset in the list
            int verticalOffset;

            // Determines the scroll UP/DOWN direction
            boolean scrollingUp;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (scrollingUp) {
                        if (verticalOffset > toolbar.getHeight()) {
                            toolbarAnimateHide();
                        } else {
                            toolbarAnimateShow(verticalOffset);
                        }
                    } else {
                        if (toolbar.getTranslationY() < toolbar.getHeight() * -0.6 && verticalOffset > toolbar.getHeight()) {
                            toolbarAnimateHide();
                        } else {
                            toolbarAnimateShow(verticalOffset);
                        }
                    }
                }
            }

            @Override
            public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                verticalOffset += dy;
                scrollingUp = dy > 0;
                int toolbarYOffset = (int) (dy - toolbar.getTranslationY());
                toolbar.animate().cancel();
                if (scrollingUp) {
                    if (toolbarYOffset < toolbar.getHeight()) {
                        toolbar.setTranslationY(-toolbarYOffset);
                    } else {
                        toolbar.setTranslationY(-toolbar.getHeight());
                    }
                } else {
                    if (toolbarYOffset < 0) {
                        toolbar.setTranslationY(0);
                    } else {
                        toolbar.setTranslationY(-toolbarYOffset);
                    }
                }
            }
        };
        return currentScrollListener;
    }

    private void toolbarAnimateShow(final int verticalOffset) {
        toolbar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180);
    }

    private void toolbarAnimateHide() {
        toolbar.animate()
                .translationY(-toolbar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180);
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
                showProgressBar();
                NYTAPI.search(query, 0, settings, searchActivity);
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
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    @Override
    public void onSearchSuggestionClick(String query) {
        this.query = query;
        searchView.setQuery(query, true);
        NYTAPI.search(query, 0, settings, this);
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

    @Override
    public void onSearchResponse(SearchResponse searchResponse, boolean addPage) {
        hideProgressBar();
        if (addPage) {
            int curSize = adapter.getItemCount();
            docs.addAll(searchResponse.getDocs());
            adapter.notifyItemRangeInserted(curSize, docs.size() - 1);
        } else {
            //When you clear and add new data, the scroll listener needs to be re-created as well.
            //Otherwise it stops working.
            rvResults.removeOnScrollListener(currentScrollListener);
            docs.clear();
            rvResults.addOnScrollListener(getScrollNewListener());
            docs.addAll(searchResponse.getDocs());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSearchError() {
        hideProgressBar();
        final SearchActivity searchActivity = this;

        int errorMsgId = InternetCheckUtil.isOnline() ?
                R.string.search_api_error : R.string.internet_connection_error;

        Snackbar.make(rvResults, errorMsgId, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NYTAPI.search(query, 0, settings, searchActivity);
                    }
                })
                .show();
    }
}
