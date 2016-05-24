package com.codepath.nytimessearch.ui.search;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.adapters.ArticleArrayAdapter;
import com.codepath.nytimessearch.models.Doc;
import com.codepath.nytimessearch.models.SearchResponse;
import com.codepath.nytimessearch.models.Settings;
import com.codepath.nytimessearch.network.NYTAPI;
import com.codepath.nytimessearch.ui.article.ArticleActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class SearchActivity extends AppCompatActivity implements SettingsFragment.SettingsDialogListener, NYTAPI.SearchResponseListener {

    @BindView(R.id.gvResults)
    GridView gridView;

    //XXX: Use icicles here.
    ArrayList<Doc> docs;
    ArticleArrayAdapter adapter;

    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        docs = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, docs);
        gridView.setAdapter(adapter);
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
            public boolean onQueryTextSubmit(String query) {
                NYTAPI.search(query, settings, searchActivity);
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

    @OnItemClick(R.id.gvResults)
    public void onArticleClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
        Doc doc = docs.get(position);
        i.putExtra("doc", doc);
        startActivity(i);
    }

    @Override
    public void onFinishDialog(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void onSearchResponse(SearchResponse searchResponse) {
        adapter.clear();
        adapter.addAll(searchResponse.getDocs());
    }
}
