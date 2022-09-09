package com.emobx.news.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emobx.news.Activities.PlayerActivity;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.BookmarkData;
import com.emobx.news.Model.WebSeriesDetailModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<WebSeriesDetailModel.Data.LastWatch> list;
    private ViewHolder viewholder;
    private AdapterItemClickListner listener;
    private NewsPreferences preferences;

    public EpisodesAdapter(Activity context, ArrayList<WebSeriesDetailModel.Data.LastWatch> list, AdapterItemClickListner listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        preferences = new NewsPreferences(context);
    }

    @NonNull
    @Override
    public EpisodesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.episodes_item_view, null);
        viewholder = new EpisodesAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesAdapter.ViewHolder holder, int i) {
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            holder.rl_main.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.iv_play.setImageDrawable(context.getResources().getDrawable(R.drawable.play_circle_white));
            holder.tv_episode.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
            holder.tv_episode_name.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
        } else {
            holder.rl_main.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.iv_play.setImageDrawable(context.getResources().getDrawable(R.drawable.play_circle));
            holder.tv_episode.setTextColor(context.getResources().getColor(R.color.appBlackDark));
            holder.tv_episode_name.setTextColor(context.getResources().getColor(R.color.appBlackDark));
        }


        if (list.get(i).getName() != null)
            holder.tv_episode.setText(list.get(i).getName() + "");

        if (list.get(i).getDescription() != null)
            holder.tv_episode_name.setText(list.get(i).getDescription() + "");

        if (list.get(i).getGetUserEpisode() != null
                && list.get(i).getGetUserEpisode().getVideoTotalTime() != null
                && list.get(i).getGetUserEpisode().getResumeAt() != null) {
            holder.progress_resume.setMax(list.get(i).getGetUserEpisode().getVideoTotalTime());
            holder.progress_resume.setProgress(list.get(i).getGetUserEpisode().getResumeAt());
        }

        holder.itemView.setOnClickListener(v -> {
            if (Utils.getLoginToken(context).isEmpty())
                Utils.setLoginPopup(context, preferences);
            else
                context.sendBroadcast(new Intent("callInAppBilling")
                        .putExtra("webSeriesEpisodeDetail", list.get(i))
                        .putExtra("episodeId", list.get(i).getId().toString())
                        .putExtra("seriesId", list.get(i).getSeriesId().toString()));
//            context.startActivity(new Intent(context, PlayerActivity.class)
//                    .putExtra("webSeriesEpisodeDetail", list.get(i))
//                    .putExtra("episodeId", list.get(i).getId().toString())
//                    .putExtra("seriesId", list.get(i).getSeriesId().toString()));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<WebSeriesDetailModel.Data.LastWatch> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_play, iv_download;
        TextView tv_episode, tv_episode_name, tv_like, tv_audio;
        ProgressBar progress_resume;
        RelativeLayout rl_main;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_episode = itemView.findViewById(R.id.tv_episode);
            tv_episode_name = itemView.findViewById(R.id.tv_episode_name);
            progress_resume = itemView.findViewById(R.id.progress_resume);
            rl_main = itemView.findViewById(R.id.rl_main);
            iv_play = itemView.findViewById(R.id.iv_play);
//                tv_audio = itemView.findViewById(R.id.tv_audio);
//                iv_download = itemView.findViewById(R.id.iv_download);
        }
    }

}


