package com.codepath.nytimessearch.ui.list;

import android.view.MenuItem;

public class ListProgressBar {

    private static ListProgressBar instance;

    private MenuItem actionProgressItem;

    private ListProgressBar(MenuItem actionProgressItem) {
        this.actionProgressItem = actionProgressItem;
    }

    public static void createInstance(MenuItem actionProgressItem) {
        instance = new ListProgressBar(actionProgressItem);
    }

    public static ListProgressBar getInstance() {
        return instance;
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
