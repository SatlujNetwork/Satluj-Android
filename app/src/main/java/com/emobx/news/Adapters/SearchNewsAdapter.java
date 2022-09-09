package com.emobx.news.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import com.emobx.news.Model.NewsListModel;
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.Model.SearchNewsData;
import com.emobx.news.R;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.RealmResults;

public class SearchNewsAdapter extends RecyclerView.Adapter<SearchNewsAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<NewsListModelDatum> data;
    private ViewHolder viewholder;
    private AdapterItemClickListner listener;
    private NewsPreferences preferences;

    public SearchNewsAdapter(Activity context, ArrayList<NewsListModelDatum> data) {
        this.context = context;
        this.data = data;
        preferences = new NewsPreferences(context);
    }

    @NonNull
    @Override
    public SearchNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.item_search_news, null);
        viewholder = new SearchNewsAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchNewsAdapter.ViewHolder holder, int i) {
        NewsListModelDatum item = data.get(i);
        holder.tv_title.setText(item.getTitle());
        if (item.getCoverImage() != null && !item.getCoverImage().isEmpty())
            Picasso.with(context).load(item.getCoverImage()).into(holder.iv_photo);
        holder.tv_like.setText(item.getLikes() + "");
        holder.tv_date.setText(Utils.changeDateFormat(data.get(i).getCreatedAt(), "yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy / h:mm a"));

        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
        } else {
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.appBlackDark));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, EntertainmentDetailActivity.class)
                        .putExtra("newsId", item.getId() + ""));

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_photo, iv_download;
        TextView tv_title, tv_date, tv_like, tv_audio;
        RelativeLayout rl_main;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_date = itemView.findViewById(R.id.tv_date);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_like = itemView.findViewById(R.id.tv_like);
            iv_photo = itemView.findViewById(R.id.iv_photo);
        }
    }

}

