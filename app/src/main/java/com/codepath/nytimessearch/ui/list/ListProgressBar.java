package com.codepath.nytimessearch.ui.list;

import android.view.MenuItem;

public class ListProgressBar {

    MenuItem actionProgressItem;

    public ListProgressBar(MenuItem actionProgressItem) {
        this.actionProgressItem = actionProgressItem;
    }


    public void showProgressBar() {
        // Show progress item
        actionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        actionProgressItem.setVisible(false);
    }
}
