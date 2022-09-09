package com.emobx.news.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Model.BannerDatum;
import com.emobx.news.Model.BannerModel;
import com.emobx.news.Model.NewsSliderListModelDatum;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.DownloadFile;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.ExecutionException;

import io.realm.RealmList;

public class ExploreViewPagerAdapter extends PagerAdapter {
    private LayoutInflater mLayoutInflater;
    private Context context;
    private RealmList<BannerDatum> data = new RealmList<>();
    private AdapterItemClickListner listner;

    public ExploreViewPagerAdapter(Context context, RealmList<BannerDatum> data, AdapterItemClickListner listner) {
        this.context = context;
        this.data = data;
        this.listner = listner;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        try {
            return data.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        ImageView iv_video = itemView.findViewById(R.id.iv_video);
        TextView tv_entertainment = itemView.findViewById(R.id.tv_entertainment);
        TextView tv_text = itemView.findViewById(R.id.tv_text);
        TextView tv_time = itemView.findViewById(R.id.tv_time);


        itemView.setOnClickListener(v -> {
            listner.OnItemClick("viewPagerClick", position);
        });


        if (data.get(position).getTitle() != null && !data.get(position).getTitle().isEmpty())
            tv_text.setText(data.get(position).getTitle());

//        if (data.get(position).getCategory() != null && !data.get(position).getCategory().isEmpty())
//            tv_entertainment.setText(data.get(position).getCategory());

        if (data.get(position).getCreatedAt() != null && !data.get(position).getCreatedAt().isEmpty())
            tv_time.setText(Utils.changeDateFormat(data.get(position).getCreatedAt(), "yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy / h:mm a"));


        String url = "";
        if (data.get(position).getCoverImage() != null && !data.get(position).getCoverImage().isEmpty())
            if (data.get(position).getCoverImage().startsWith("http"))
                url = data.get(position).getCoverImage();
            else
                url = Constants.BASE_URl + "/" + data.get(position).getCoverImage();


        File outputFile = new File(Constants.image_dir_path
                + "/" + data.get(position).getId() + "_" + Utils.getFileName(url));
        if (outputFile != null)
            if (outputFile.exists())
                Picasso.with(context).load(outputFile).into(iv_video);
            else
                Picasso.with(context).load(url).into(iv_video);
        else
            Picasso.with(context).load(url).into(iv_video);


        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
