package com.emobx.news.Adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.emobx.news.Activities.WebSeriesDetailActivity;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.WebSeriesDetailModel;
import com.emobx.news.R;

import java.util.ArrayList;

public class SeasonsAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private ArrayList<WebSeriesDetailModel.Data.GetSeason> list = new ArrayList<>();
    private Activity activity;
    private AdapterItemClickListner listener;
    private NewsPreferences preferences;

    public SeasonsAdapter(Activity activity, ArrayList<WebSeriesDetailModel.Data.GetSeason> list, AdapterItemClickListner listener) {
        mLayoutInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.list = list;
        this.listener = listener;
        preferences = new NewsPreferences(activity);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public WebSeriesDetailModel.Data.GetSeason getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_text, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (getItem(position).getSeason() != null)
            holder.tv_seasons.setText("Season " + getItem(position).getSeason());

        if (getItem(position).isSelected()) {
            holder.tv_seasons.setTextColor(activity.getResources().getColor(R.color.appBlue));
            Typeface typeface = ResourcesCompat.getFont(activity, R.font.opensans_semi_bold);
            holder.tv_seasons.setTypeface(typeface);
        } else {
            if (preferences.getTheme().equalsIgnoreCase("dark")) {
                holder.tv_seasons.setTextColor(activity.getResources().getColor(R.color.appBlackxLight));
                Typeface typeface = ResourcesCompat.getFont(activity, R.font.opensans_regular);
                holder.tv_seasons.setTypeface(typeface);
            } else {
                holder.tv_seasons.setTextColor(activity.getResources().getColor(R.color.appBlackDark));
                Typeface typeface = ResourcesCompat.getFont(activity, R.font.opensans_regular);
                holder.tv_seasons.setTypeface(typeface);
            }
        }

        holder.tv_seasons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnItemClick("seasonSelected", position);
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setSelected(false);
                }
                getItem(position).setSelected(true);

                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tv_seasons;
        RelativeLayout rl_main;

        ViewHolder(View itemView) {
            tv_seasons = itemView.findViewById(R.id.tv_seasons);
            rl_main = itemView.findViewById(R.id.rl_main);
        }
    }
}



