package com.emobx.news.Adapters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.emobx.news.Fragments.DynamicFragment;
import com.emobx.news.Model.CategoryListDatumModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class DynamicFragmentAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private int mNumOfTabs;
    private RealmList<CategoryListDatumModel> data;

    public DynamicFragmentAdapter(FragmentManager fm, int NumOfTabs, RealmList<CategoryListDatumModel> data) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.data = data;
    }

    // get the current item with position number
    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        Fragment frag;
        b.putInt("position", position);
        if (position > 0) {
            frag = DynamicFragment.newInstance(data.get(position - 1).getId());
            b.putInt("id", data.get(position - 1).getId());
        } else {
            frag = DynamicFragment.newInstance(-1);
            b.putInt("id", -1);
        }
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    // get total number of tabs
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

