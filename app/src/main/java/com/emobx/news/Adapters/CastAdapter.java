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
import com.emobx.news.Model.WebSeriesDetailModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<WebSeriesDetailModel.Data.CastMember> list;
    private CastAdapter.ViewHolder viewholder;
    private AdapterItemClickListner listener;
    private NewsPreferences preferences;

    public CastAdapter(Activity context, ArrayList<WebSeriesDetailModel.Data.CastMember> list, AdapterItemClickListner listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        preferences = new NewsPreferences(context);
    }

    @NonNull
    @Override
    public CastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.cast_item_view, null);
        viewholder = new CastAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull CastAdapter.ViewHolder holder, int i) {
        if (list.get(i).getPic() != null)
            Utils.loadImageWithUrl(context, list.get(i).getPic() + "", holder.iv_image);

        if (list.get(i).getName() != null)
            holder.tv_name.setText(list.get(i).getName() + "");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<WebSeriesDetailModel.Data.CastMember> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image, iv_download;
        TextView tv_name, tv_time, tv_like, tv_audio;
        RelativeLayout rl_main;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_image = itemView.findViewById(R.id.iv_image);
            tv_name = itemView.findViewById(R.id.tv_name);
//            tv_text = itemView.findViewById(R.id.tv_text);
//            tv_time = itemView.findViewById(R.id.tv_time);
//            tv_like = itemView.findViewById(R.id.tv_like);
//                tv_audio = itemView.findViewById(R.id.tv_audio);
//                iv_download = itemView.findViewById(R.id.iv_download);
        }
    }

}


