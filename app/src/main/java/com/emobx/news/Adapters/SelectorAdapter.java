package com.emobx.news.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.R;

public class SelectorAdapter extends RecyclerView.Adapter<SelectorAdapter.ViewHolder> {
    public int currentDot = 0;
    public int data;
    private Activity context;
    private ViewHolder viewholder;
    private AdapterItemClickListner listener;

    public SelectorAdapter(Activity context, int data, int currentDot, AdapterItemClickListner listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.currentDot = currentDot;
    }

    @NonNull
    @Override
    public SelectorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.selector_item_view, null);
        viewholder = new SelectorAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectorAdapter.ViewHolder holder, int i) {
        if (currentDot == i) {
            holder.iv_dot.setImageDrawable(context.getResources().getDrawable(R.drawable.selected_dot));
        } else {
            holder.iv_dot.setImageDrawable(context.getResources().getDrawable(R.drawable.default_dot));
        }
    }

    @Override
    public int getItemCount() {
        return data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_dot, iv_download;
        TextView tv_name, tv_image, tv_video, tv_audio;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_dot = itemView.findViewById(R.id.iv_dot);
//                tv_name = itemView.findViewById(R.id.tv_name);
//                tv_image = itemView.findViewById(R.id.tv_image);
//                tv_video = itemView.findViewById(R.id.tv_video);
//                tv_audio = itemView.findViewById(R.id.tv_audio);
//                iv_download = itemView.findViewById(R.id.iv_download);
        }
    }

}

