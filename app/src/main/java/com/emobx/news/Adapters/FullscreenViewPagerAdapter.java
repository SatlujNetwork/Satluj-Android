package com.emobx.news.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Fragments.HomeFragment;
import com.emobx.news.Model.CategoryListDatumModel;
import com.emobx.news.Model.NewsSliderListModelDatum;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.RealmList;

public class FullscreenViewPagerAdapter extends PagerAdapter {
    LayoutInflater mLayoutInflater;
    private Context context;
    private RealmList<CategoryListDatumModel> data;
    private AdapterItemClickListner listner;
    private View itemView;

    public FullscreenViewPagerAdapter(Context context, RealmList<CategoryListDatumModel> data, AdapterItemClickListner listner) {
        this.context = context;
        this.data = data;
        this.listner = listner;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size() + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        itemView = mLayoutInflater.inflate(R.layout.home_item, container, false);

//        ImageView iv_video = itemView.findViewById(R.id.iv_video);
//        TextView tv_entertainment = itemView.findViewById(R.id.tv_entertainment);
//        TextView tv_text = itemView.findViewById(R.id.tv_text);
//        TextView tv_time = itemView.findViewById(R.id.tv_time);


        itemView.findViewById(R.id.tv_viewall).setOnClickListener(v -> {
            setExploreViewAllScreen();
        });

        if (position == 0) {
            setExploreScreen();
        } else {
            setOtherScreen();
        }

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    public void setExploreScreen() {
        itemView.findViewById(R.id.explore_pull_refresh).setVisibility(View.VISIBLE);
        itemView.findViewById(R.id.other_pull_refresh).setVisibility(View.GONE);
        itemView.findViewById(R.id.viewall_pull_refresh).setVisibility(View.GONE);
    }

    public void setOtherScreen() {
        itemView.findViewById(R.id.explore_pull_refresh).setVisibility(View.GONE);
        itemView.findViewById(R.id.other_pull_refresh).setVisibility(View.VISIBLE);
        itemView.findViewById(R.id.viewall_pull_refresh).setVisibility(View.GONE);
    }

    public void setExploreViewAllScreen() {
        itemView.findViewById(R.id.explore_pull_refresh).setVisibility(View.GONE);
        itemView.findViewById(R.id.other_pull_refresh).setVisibility(View.GONE);
        itemView.findViewById(R.id.viewall_pull_refresh).setVisibility(View.VISIBLE);
    }
}
