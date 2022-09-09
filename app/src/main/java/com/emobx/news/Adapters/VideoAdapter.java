package com.emobx.news.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emobx.news.Activities.EntertainmentDetailActivity;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.Model.VideoWebSeriesModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.RealmList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<VideoWebSeriesModel.Data.Video> list;
    private ViewHolder viewholder;
    private AdapterItemClickListner listener;
    private NewsPreferences preferences;

    public VideoAdapter(Activity context, ArrayList<VideoWebSeriesModel.Data.Video> data, AdapterItemClickListner listener) {
        this.context = context;
        this.list = data;
        this.listener = listener;
        preferences = new NewsPreferences(context);
    }

    public void updateList(ArrayList<VideoWebSeriesModel.Data.Video> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.video_item_view, null);
        viewholder = new VideoAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int i) {
        VideoWebSeriesModel.Data.Video item = list.get(i);
        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, EntertainmentDetailActivity.class)
                    .putExtra("newsId", item.getId() + "")
                    .putExtra("catId", item.getCatId() + ""));
        });

        Utils.loadImageWithUrl(context, item.getCoverImage(), holder.iv_video);

        if (item.getTitle() != null)
            holder.tv_text.setText(Html.fromHtml(item.getTitle()));
        if (item.getLikes() != null && item.getLikes() > 0)
            holder.tv_like.setText(item.getLikes() + "");
        if (item.getCreatedAt() != null)
            holder.tv_time.setText(Utils.changeDateFormat(item.getCreatedAt(), "yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy / h:mm a") + "");

        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            holder.rl_main.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.tv_text.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
            holder.tv_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.time_white, 0, 0, 0);
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
            holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_white, 0, 0, 0);
            holder.tv_like.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
        } else {
            holder.rl_main.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tv_text.setTextColor(context.getResources().getColor(R.color.appBlackDark));
            holder.tv_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.time, 0, 0, 0);
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackDark));
            holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.likes, 0, 0, 0);
            holder.tv_like.setTextColor(context.getResources().getColor(R.color.appBlackLight));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_video, iv_download;
        TextView tv_text, tv_time, tv_like, tv_audio;
        RelativeLayout rl_main;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_video = itemView.findViewById(R.id.iv_video);
            rl_main = itemView.findViewById(R.id.rl_main);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_like = itemView.findViewById(R.id.tv_like);
//                tv_video = itemView.findViewById(R.id.tv_video);
//                tv_audio = itemView.findViewById(R.id.tv_audio);
//                iv_download = itemView.findViewById(R.id.iv_download);
        }
    }

}

