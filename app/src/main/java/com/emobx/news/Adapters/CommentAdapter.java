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
import com.emobx.news.Model.CommentModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<CommentModel.Datum> data;
    private ViewHolder viewholder;
    private AdapterItemClickListner listener;
    private NewsPreferences preferences;

    public CommentAdapter(Activity context, ArrayList<CommentModel.Datum> data, AdapterItemClickListner listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        preferences = new NewsPreferences(context);
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.comments_item_view, null);
        viewholder = new CommentAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int i) {
        holder.itemView.setOnLongClickListener(v -> {
            listener.OnItemClick("commentClick", i);
            return true;
        });

        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            holder.rl_main.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.rl_comment.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.tv_text.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
        } else {
            holder.rl_main.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.rl_comment.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tv_text.setTextColor(context.getResources().getColor(R.color.appBlackDark));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackLight));
        }

        if (data.get(i).getComment() != null)
            holder.tv_text.setText(data.get(i).getComment());

        if (data.get(i).getCreatedAt() != null && !data.get(i).getCreatedAt().isEmpty())
            holder.tv_time.setText(Utils.changeDateFormat(data.get(i).getCreatedAt(), "yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy / h:mm a"));

//        holder.iv_image.setImageDrawable(context.getResources().getDrawable(R.drawable.spiderman));

        if (data.get(i).getGetUser() != null)
            if (data.get(i).getGetUser().getPic() != null && !data.get(i).getGetUser().getPic().isEmpty()) {
                if (data.get(i).getGetUser().getPic().startsWith("http"))
                    Picasso.with(context).load(data.get(i).getGetUser().getPic()).into(holder.iv_image);
                else
                    Picasso.with(context).load(Constants.BASE_URl + "/" + data.get(i).getGetUser().getPic()).into(holder.iv_image);
            } else {
                holder.iv_image.setImageResource(R.drawable.images);
            }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image, iv_download;
        TextView tv_text, tv_time, tv_video, tv_audio;
        RelativeLayout rl_comment, rl_main;

        public ViewHolder(View itemView) {
            super(itemView);

            rl_comment = itemView.findViewById(R.id.rl_comment);
            tv_text = itemView.findViewById(R.id.tv_text);
            rl_main = itemView.findViewById(R.id.rl_main);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_image = itemView.findViewById(R.id.iv_profile);
//                iv_download = itemView.findViewById(R.id.iv_download);
        }
    }

}

