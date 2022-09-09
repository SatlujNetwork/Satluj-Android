package com.emobx.news.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emobx.news.Activities.EntertainmentDetailActivity;
import com.emobx.news.Activities.WebSeriesDetailActivity;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.Model.VideoWebSeriesModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.RealmList;

public class WebSeriesAdapter extends RecyclerView.Adapter<WebSeriesAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<VideoWebSeriesModel.Data.Webseries> list;
    private ViewHolder viewholder;
    private AdapterItemClickListner listener;
    private NewsPreferences preferences;

    public WebSeriesAdapter(Activity context, ArrayList<VideoWebSeriesModel.Data.Webseries> data, AdapterItemClickListner listener) {
        this.context = context;
        this.list = data;
        this.listener = listener;
        preferences = new NewsPreferences(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.web_series_item_view, null);
        viewholder = new ViewHolder(view);
        return viewholder;
    }

    public void updateList(ArrayList<VideoWebSeriesModel.Data.Webseries> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        VideoWebSeriesModel.Data.Webseries item = list.get(i);
        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, WebSeriesDetailActivity.class)
                    .putExtra("seriesId", item.getId() + ""));
        });

        Utils.loadImageWithUrl(context, item.getThumbNail(), holder.iv_video);

        if (item.getName() != null && item.getSeason() != null)
            holder.tv_text.setText(item.getName() + " - Season " + item.getSeason());


        if (item.getReleaseYear() != null)
            holder.tv_time.setText(item.getReleaseYear() + "");

        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            holder.ll_main.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.tv_text.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
            holder.tv_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.time_white, 0, 0, 0);
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
//            holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_white, 0, 0, 0);
//            holder.tv_like.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
        } else {
            holder.ll_main.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tv_text.setTextColor(context.getResources().getColor(R.color.appBlackDark));
            holder.tv_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.time, 0, 0, 0);
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackDark));
//            holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.likes, 0, 0, 0);
//            holder.tv_like.setTextColor(context.getResources().getColor(R.color.appBlackLight));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_video, iv_download;
        TextView tv_text, tv_time;
        LinearLayout ll_main;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_video = itemView.findViewById(R.id.iv_video);
            ll_main = itemView.findViewById(R.id.ll_main);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_time = itemView.findViewById(R.id.tv_time);
//            tv_like = itemView.findViewById(R.id.tv_like);
//                tv_video = itemView.findViewById(R.id.tv_video);
//                tv_audio = itemView.findViewById(R.id.tv_audio);
//                iv_download = itemView.findViewById(R.id.iv_download);
        }
    }

}


