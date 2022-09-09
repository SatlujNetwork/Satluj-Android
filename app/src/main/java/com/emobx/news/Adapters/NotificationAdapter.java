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
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.Model.NotificationListModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Activity context;
    private NotificationListModel data;
    private ViewHolder viewholder;
    private AdapterItemClickListner listener;
    private NewsPreferences preferences;

    public NotificationAdapter(Activity context, NotificationListModel data, AdapterItemClickListner listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        preferences = new NewsPreferences(context);
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.notification_item_view, null);
        viewholder = new NotificationAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int i) {
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            holder.rl_main.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.tv_text.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
            holder.tv_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.time_white, 0, 0, 0);
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
        } else {
            holder.rl_main.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tv_text.setTextColor(context.getResources().getColor(R.color.appBlackDark));
            holder.tv_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.time, 0, 0, 0);
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackDark));
        }
        if (data.getData() != null) {
            NewsListModelDatum dataLoc = data.getData().get(i);

            if (dataLoc.getTitle() != null && !dataLoc.getTitle().isEmpty())
                holder.tv_text.setText(dataLoc.getTitle() + "");

            if (dataLoc.getCreatedAt() != null && !dataLoc.getCreatedAt().isEmpty())
                holder.tv_time.setText(Utils.changeDateFormat(dataLoc.getCreatedAt() + "", "yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy / h:mm a"));

            if (dataLoc.getCoverImage() != null && !dataLoc.getCoverImage().toString().isEmpty())
                if (dataLoc.getCoverImage().toString().startsWith("http"))
                    Picasso.with(context).load(dataLoc.getCoverImage().toString()).into(holder.iv_image);
                else
                    Picasso.with(context).load(Constants.BASE_URl + "/" + dataLoc.getCoverImage().toString()).into(holder.iv_image);

        }

        holder.itemView.setOnClickListener(v -> {
            listener.OnItemClick("notificationClick", i);
        });
    }

    @Override
    public int getItemCount() {
        return data.getData().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image, iv_download;
        TextView tv_text, tv_time, tv_video, tv_audio;
        RelativeLayout rl_main;

        public ViewHolder(View itemView) {
            super(itemView);

            rl_main = itemView.findViewById(R.id.rl_main);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_image = itemView.findViewById(R.id.iv_image);
//                tv_audio = itemView.findViewById(R.id.tv_audio);
//                iv_download = itemView.findViewById(R.id.iv_download);
        }
    }

}

