package com.codepath.nytimessearch.ui.list;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.nytimessearch.models.Doc;

import java.util.ArrayList;

import icepick.Icepick;

public abstract class BaseArticleListingFragment extends Fragment implements ListGridView.ListGridViewHolder {

    RecyclerView rvGridView;

    ArrayList<Doc> docs;

    ViewGroup tbLayout;

    ListGridView listGridView;

    private static final int NUM_COLS_PORTRAIT = 4;
    private static final int NUM_COLS_LANDSCAPE = 6;

    public BaseArticleListingFragment() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutId(), container, false);

        if (docs == null) {
            docs = new ArrayList<>();
        }

        rvGridView = (RecyclerView) view.findViewById(getGridViewId());

        int numCols =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                        NUM_COLS_PORTRAIT : NUM_COLS_LANDSCAPE;

        listGridView = new ListGridView(this, rvGridView, docs, numCols);

        if (docs.isEmpty()) {
            init();
        }
        return view;
    }

    public abstract int getLayoutId();

    /**
     * Initialize the grid view with data.
     */
    public abstract void init();

    public abstract int getGridViewId();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
