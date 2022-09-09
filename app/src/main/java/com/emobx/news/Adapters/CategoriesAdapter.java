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
import com.emobx.news.Model.CategoryListDatumModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import io.realm.RealmList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private Activity context;
    private RealmList<CategoryListDatumModel> data = new RealmList<>();
    private ViewHolder viewholder;
    private AdapterItemClickListner listener;
    private NewsPreferences preferences;

    public CategoriesAdapter(Activity context, RealmList<CategoryListDatumModel> data, AdapterItemClickListner listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        preferences = new NewsPreferences(context);
    }

    @NonNull
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.categories_item_view, null);
        viewholder = new CategoriesAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.ViewHolder holder, int i) {
//        if (preferences.getTheme().equalsIgnoreCase("dark")) {
//            holder.rl_main.setBackgroundColor(context.getResources().getColor(R.color.black));
//            holder.tv_text.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
//            holder.tv_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.time_white, 0, 0, 0);
//            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
//            holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_white, 0, 0, 0);
//            holder.tv_like.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
//        } else {
//            holder.rl_main.setBackgroundColor(context.getResources().getColor(R.color.white));
//            holder.tv_text.setTextColor(context.getResources().getColor(R.color.appBlackDark));
//            holder.tv_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.time, 0, 0, 0);
//            holder.tv_time.setTextColor(context.getResources().getColor(R.color.appBlackLight));
//            holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.likes, 0, 0, 0);
//            holder.tv_like.setTextColor(context.getResources().getColor(R.color.appBlackLight));
//        }

        holder.itemView.setOnClickListener(v -> {
            listener.OnItemClick("categoryClick", i);
        });

        if (data.get(i).getName() != null)
            holder.tv_text.setText(data.get(i).getName());

//        if (data.get(i).getCoverImage() != null && !data.get(i).getCoverImage().isEmpty())
//            if (data.get(i).getCoverImage().startsWith("http"))
//                Picasso.with(context).load(data.get(i).getCoverImage()).into(holder.iv_image);
//            else
//                Picasso.with(context).load(Constants.BASE_URl + "/" + data.get(i).getCoverImage()).into(holder.iv_image);


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

    public void updateList(RealmList<CategoryListDatumModel> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image, iv_download;
        TextView tv_text, tv_time, tv_like, tv_audio;
        RelativeLayout rl_main;

        public ViewHolder(View itemView) {
            super(itemView);

//            rl_main = itemView.findViewById(R.id.rl_main);
            tv_text = itemView.findViewById(R.id.tv_text);
            iv_image = itemView.findViewById(R.id.iv_image);
//            tv_like = itemView.findViewById(R.id.tv_like);
//                tv_audio = itemView.findViewById(R.id.tv_audio);
//                iv_download = itemView.findViewById(R.id.iv_download);
        }
    }

}

