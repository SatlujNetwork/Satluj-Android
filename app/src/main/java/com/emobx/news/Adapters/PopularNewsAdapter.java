package com.emobx.news.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.DownloadFile;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.ExecutionException;

import io.realm.RealmList;

public class PopularNewsAdapter extends RecyclerView.Adapter<PopularNewsAdapter.ViewHolder> {
    private Activity context;
    private RealmList<NewsListModelDatum> data;
    private ViewHolder viewholder;
    private AdapterItemClickListner listener;
    private NewsPreferences preferences;

    public PopularNewsAdapter(Activity context, RealmList<NewsListModelDatum> data, AdapterItemClickListner listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        preferences = new NewsPreferences(context);
    }

    public void updateList(RealmList<NewsListModelDatum> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PopularNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.popular_news_item_view, null);
        viewholder = new PopularNewsAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull PopularNewsAdapter.ViewHolder holder, int i) {
        holder.itemView.setOnClickListener(v -> {
            listener.OnItemClick("popularNews", i);
        });
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            holder.rl_main.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.tv_text.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
            holder.tv_text1.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
            holder.tv_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.time_white, 0, 0, 0);
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
            holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_white, 0, 0, 0);
            holder.tv_like.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
        } else {
            holder.rl_main.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tv_text.setTextColor(context.getResources().getColor(R.color.appBlackDark));
            holder.tv_text1.setTextColor(context.getResources().getColor(R.color.appBlackDark));
            holder.tv_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.time, 0, 0, 0);
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackLight));
            holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.likes, 0, 0, 0);
            holder.tv_like.setTextColor(context.getResources().getColor(R.color.appBlackLight));
        }

        if (data.get(i).getTitle() != null)
            holder.tv_text.setText(data.get(i).getTitle() + "");

        if (data.get(i).getDescription() != null)
            holder.tv_text1.setText(Html.fromHtml(data.get(i).getDescription() + ""));

        if (data.get(i).getCreatedAt() != null && !data.get(i).getCreatedAt().isEmpty())
            holder.tv_time.setText(Utils.changeDateFormat(data.get(i).getCreatedAt(), "yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy / h:mm a"));

        if (data.get(i).getLikes() != null && !data.get(i).getLikes().toString().isEmpty())
            if (data.get(i).getLikes() > 0)
                holder.tv_like.setText(data.get(i).getLikes() + "");
            else
                holder.tv_like.setText("");
        else
            holder.tv_like.setText("");

        String url = "";
        if (data.get(i).getCoverImage() != null && !data.get(i).getCoverImage().isEmpty())
            if (data.get(i).getCoverImage().startsWith("http"))
                url = data.get(i).getCoverImage();
            else
                url = Constants.BASE_URl + "/" + data.get(i).getCoverImage();


        File outputFile = new File(Constants.image_dir_path
                + "/" + data.get(i).getId() + "_" + Utils.getFileName(url));
        if (outputFile != null)
            if (outputFile.exists())
                Picasso.with(context).load(outputFile).into(holder.iv_image);
            else
                Picasso.with(context).load(url).into(holder.iv_image);
        else
            Picasso.with(context).load(url).into(holder.iv_image);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image, iv_download;
        TextView tv_text, tv_text1, tv_time, tv_like;
        RelativeLayout rl_main;

        public ViewHolder(View itemView) {
            super(itemView);

            rl_main = itemView.findViewById(R.id.rl_main);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_text1 = itemView.findViewById(R.id.tv_text1);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_like = itemView.findViewById(R.id.tv_like);
            iv_image = itemView.findViewById(R.id.iv_image);
        }
    }

}

