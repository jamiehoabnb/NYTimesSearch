package com.codepath.nytimessearch.ui.list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Source: http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends SmartFragmentStatePagerAdapter {

    CharSequence titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created

    public static final int NUM_TABS = 8;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[]) {
        super(fm);

        this.titles = titles;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new AllArticleListingFragment();
            case 1:
                return new BusinessArticleListingFragment();
            case 2:
                return new ForeignArticleListingFragment();
            case 3:
                return new MoviesArticleListingFragment();
            case 4:
                return new SportsArticleListingFragment();
            case 5:
                return new StylesArticleListingFragment();
            case 6:
                return new TravelArticleListingFragment();
            case 7:
                return new SearchArticleListingFragment();
            default:
                return null;
        }
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return titles.length;
    }
}