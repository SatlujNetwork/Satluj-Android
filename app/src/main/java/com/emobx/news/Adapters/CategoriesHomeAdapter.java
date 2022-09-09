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
import androidx.collection.ArraySet;
import androidx.recyclerview.widget.RecyclerView;

import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.CategoryListDatumModel;
import com.emobx.news.R;

import java.util.ArrayList;

import io.realm.RealmList;

public class CategoriesHomeAdapter extends RecyclerView.Adapter<CategoriesHomeAdapter.ViewHolder> {
    public static int selectedPos = 0;
    private ArrayList<String> namesArr = new ArrayList<>();
    private Activity context;
    private RealmList<CategoryListDatumModel> data;
    private ViewHolder viewholder;
    private AdapterItemClickListner listener;
    private NewsPreferences preferences;

    public CategoriesHomeAdapter(Activity context, RealmList<CategoryListDatumModel> data, AdapterItemClickListner listener, int selectedPos) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.selectedPos = selectedPos;
        preferences = new NewsPreferences(context);
        namesArr.add("Explore");
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName() != null)
                namesArr.add(data.get(i).getName());
        }
    }

    public void updateList(RealmList<CategoryListDatumModel> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoriesHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.categories_home_item_view, null);
        viewholder = new CategoriesHomeAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesHomeAdapter.ViewHolder holder, int i) {
        holder.tv_explore.setText(namesArr.get(i));

        if (i == selectedPos) {
            holder.tv_explore.setTextColor(context.getResources().getColor(R.color.appBlue));
            holder.vi_explore.setBackgroundColor(context.getResources().getColor(R.color.appBlue));
            holder.vi_explore.setVisibility(View.VISIBLE);
        } else {
            if (preferences.getTheme().equalsIgnoreCase("dark")) {
                holder.tv_explore.setTextColor(context.getResources().getColor(R.color.appBlackxLight));
                holder.vi_explore.setBackgroundColor(context.getResources().getColor(R.color.appBlackxLight));
                holder.vi_explore.setVisibility(View.GONE);
            } else {
                holder.tv_explore.setTextColor(context.getResources().getColor(R.color.appBlackDark));
                holder.vi_explore.setBackgroundColor(context.getResources().getColor(R.color.appBlackDark));
                holder.vi_explore.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPos = i;
            notifyDataSetChanged();
            listener.OnItemClick("categoryClick", i - 1);
        });
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
    }

    @Override
    public int getItemCount() {
        return namesArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_explore;
        View vi_explore;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_explore = itemView.findViewById(R.id.tv_explore);
            vi_explore = itemView.findViewById(R.id.vi_explore);
//            tv_time = itemView.findViewById(R.id.tv_time);
//            tv_like = itemView.findViewById(R.id.tv_like);
//                tv_audio = itemView.findViewById(R.id.tv_audio);
//                iv_download = itemView.findViewById(R.id.iv_download);
        }
    }

}

