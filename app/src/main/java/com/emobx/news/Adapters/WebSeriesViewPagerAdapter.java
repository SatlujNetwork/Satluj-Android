package com.emobx.news.Adapters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.emobx.news.Fragments.DynamicFragment;
import com.emobx.news.Fragments.WebSeriesDynamicFragment;
import com.emobx.news.Model.WebSeriesDetailModel;

public class WebSeriesViewPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private WebSeriesDetailModel.Data webSeriesDetail;

    public WebSeriesViewPagerAdapter(FragmentManager fm, int NumOfTabs, WebSeriesDetailModel.Data webSeriesDetail) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.webSeriesDetail = webSeriesDetail;
    }

    // get the current item with position number
    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        b.putInt("position", position);
        b.putSerializable("seriesData", webSeriesDetail);
        Fragment frag = WebSeriesDynamicFragment.newInstance();
        frag.setArguments(b);
        return frag;
    }

    // get total number of tabs
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

