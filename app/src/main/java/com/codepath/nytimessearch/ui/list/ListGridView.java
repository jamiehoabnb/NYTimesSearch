package com.codepath.nytimessearch.ui.list;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Doc;
import com.codepath.nytimessearch.models.SearchResponse;
import com.codepath.nytimessearch.network.NYTAPI;
import com.codepath.nytimessearch.ui.view.ArticleActivity;
import com.codepath.nytimessearch.util.EndlessRecyclerViewScrollListener;
import com.codepath.nytimessearch.util.InternetCheckUtil;
import com.codepath.nytimessearch.util.ItemClickSupport;

import org.parceler.Parcels;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Manages a RecyclerView with StaggeredGridLayoutManager for a class that implements the
 * ListGridViewHolder interface.
 */
public class ListGridView implements NYTAPI.SearchResponseListener {

    public interface ListGridViewHolder {
        Context getContext();
        void onLoadMore(int page);
    }

    private ListGridViewHolder holder;

    private RecyclerView rvGridView;

    private DocAdapter adapter;

    private ArrayList<Doc> docs;

    private StaggeredGridLayoutManager gridLayoutManager;

    private EndlessRecyclerViewScrollListener currentScrollListener;

    public ListGridView(final ListGridViewHolder holder, final RecyclerView rvGridView,
                        final ArrayList<Doc> docs, final int numCols) {
        this.holder = holder;
        this.rvGridView = rvGridView;
        this.docs = docs;

        adapter = new DocAdapter(docs);
        rvGridView.setAdapter(adapter);

        gridLayoutManager =
                new StaggeredGridLayoutManager(numCols, StaggeredGridLayoutManager.VERTICAL);
        rvGridView.setLayoutManager(gridLayoutManager);
        rvGridView.setItemAnimator(new SlideInUpAnimator());

        ItemClickSupport.addTo(rvGridView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent i = new Intent(holder.getContext(), ArticleActivity.class);
                        Doc doc = docs.get(position);
                        i.putExtra("doc", Parcels.wrap(doc));
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        holder.getContext().startActivity(i);
                    }
                }
        );

        rvGridView.addOnScrollListener(getScrollNewListener());
    }

    private EndlessRecyclerViewScrollListener getScrollNewListener() {
        currentScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(final int page, final int totalItemsCount) {
                holder.onLoadMore(page);
            }
        };
        return currentScrollListener;
    }

    @Override
    public void onSearchResponse(SearchResponse searchResponse, boolean addPage) {
        ListProgressBar.getInstance().hideProgressBar();

        if (addPage) {
            int curSize = adapter.getItemCount();
            docs.addAll(searchResponse.getDocs());
            adapter.notifyItemRangeInserted(curSize, docs.size() - 1);
        } else {
            //When you clear and add new data, the scroll listener needs to be re-created as well.
            //Otherwise it stops working.
            rvGridView.removeOnScrollListener(currentScrollListener);
            docs.clear();
            rvGridView.addOnScrollListener(getScrollNewListener());
            docs.addAll(searchResponse.getDocs());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSearchError() {
        ListProgressBar.getInstance().hideProgressBar();

        int errorMsgId = InternetCheckUtil.isOnline() ?
                R.string.search_api_error : R.string.internet_connection_error;

        Snackbar.make(rvGridView, errorMsgId, Snackbar.LENGTH_LONG)
                .setAction(holder.getContext().getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.onLoadMore(0);
                    }
                })
                .show();
    }
}
