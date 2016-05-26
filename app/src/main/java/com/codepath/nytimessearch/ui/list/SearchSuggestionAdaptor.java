package com.codepath.nytimessearch.ui.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

import com.codepath.nytimessearch.R;

public class SearchSuggestionAdaptor extends SimpleCursorAdapter {

    public interface SearchSuggestionOnClickListener {
        void onSearchSuggestionClick(String query);
    }

    private SearchSuggestionOnClickListener listener;

    public SearchSuggestionAdaptor(Context context, int layout, Cursor c, String[] from, int[] to,
                                   int flags, SearchSuggestionOnClickListener listener) {
        super(context, layout, c, from, to, flags);
        this.listener = listener;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //User Butterknife.
        final TextView textView=(TextView)view.findViewById(R.id.tvQuerySuggestion);
        textView.setText(cursor.getString(1));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSearchSuggestionClick(textView.getText().toString());
            }
        });
    }
}
