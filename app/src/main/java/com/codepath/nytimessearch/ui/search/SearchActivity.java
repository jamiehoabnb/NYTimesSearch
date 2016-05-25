package com.codepath.nytimessearch.ui.search;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class SearchActivity extends AppCompatActivity implements SettingsFragment.SettingsDialogListener, NYTAPI.SearchResponseListener {

    @BindView(R.id.rvResults)
    RecyclerView rvResults;

    //XXX: Use icicles here.
    ArrayList<Doc> docs;
    DocAdapter adapter;
    String query;

    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tbSearch);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        docs = new ArrayList<>();
        adapter = new DocAdapter(docs);
        rvResults.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
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

        final SearchActivity searchActivity = this;
        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(final int page, final int totalItemsCount) {
                if (query == null) {
                    return;
                }

                //Add another page of articles because user is scrolling.
                NYTAPI.search(query, page, settings, searchActivity);
            }
        });

        settings = new Settings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        final SearchActivity searchActivity = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                searchActivity.query = query;
                //A new query starts on page 0.
                NYTAPI.search(query, 0, settings, searchActivity);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
        if (addPage) {
            int curSize = adapter.getItemCount();
            docs.addAll(searchResponse.getDocs());
            adapter.notifyItemRangeInserted(curSize, docs.size() - 1);
        } else {
            docs.clear();
            docs.addAll(searchResponse.getDocs());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSearchError() {
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
