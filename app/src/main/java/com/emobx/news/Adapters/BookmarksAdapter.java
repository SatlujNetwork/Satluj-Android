package com.emobx.news.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.emobx.news.Model.BookmarkData;
import com.emobx.news.R;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<BookmarkData.Datum> list;
    private ViewHolder viewholder;
    private AdapterItemClickListner listener;
    private NewsPreferences preferences;

    public BookmarksAdapter(Activity context, ArrayList<BookmarkData.Datum> data, AdapterItemClickListner listener) {
        this.context = context;
        this.list = data;
        this.listener = listener;
        preferences = new NewsPreferences(context);
    }

    @NonNull
    @Override
    public BookmarksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.bookmarks_item_view, null);
        viewholder = new BookmarksAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarksAdapter.ViewHolder holder, int i) {
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
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackLight));
            holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.likes, 0, 0, 0);
            holder.tv_like.setTextColor(context.getResources().getColor(R.color.appBlackLight));
        }

        BookmarkData.Datum item = list.get(i);
        holder.tv_time.setText(Utils.changeDateFormat(item.getGetNews().getCreatedAt(), "yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy / h:mm a"));

        if (item.getGetNews().getCoverImage() != null)
            Picasso.with(context).load(item.getGetNews().getCoverImage()).into(holder.iv_photo);

        if (item.getGetNews().getLikes() != null && !item.getGetNews().getLikes().toString().isEmpty())
            if (item.getGetNews().getLikes() > 0)
                holder.tv_like.setText(item.getGetNews().getLikes() + "");
            else
                holder.tv_like.setText("");
        else
            holder.tv_like.setText("");

        holder.tv_text.setText(item.getGetNews().getTitle());

        holder.itemView.setOnClickListener(v -> {
            listener.OnItemClick("openNewsDetail", i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_photo, iv_download;
        TextView tv_text, tv_time, tv_like, tv_audio;
        RelativeLayout rl_main;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_photo = itemView.findViewById(R.id.iv_photo);
            rl_main = itemView.findViewById(R.id.rl_main);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_like = itemView.findViewById(R.id.tv_like);
//                tv_audio = itemView.findViewById(R.id.tv_audio);
//                iv_download = itemView.findViewById(R.id.iv_download);
        }
    }

}

