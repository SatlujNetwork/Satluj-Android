package com.emobx.news.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Activities.EntertainmentActivity;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Adapters.EntertainmentAdapter;
import com.emobx.news.Adapters.VideoAdapter;
import com.emobx.news.Adapters.WebSeriesAdapter;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.CategoryWiseNewsListModel;
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.Model.VideoWebSeriesModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.DownloadFile;
import com.emobx.news.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class VideoFragment extends Fragment implements AdapterItemClickListner, SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private RecyclerView rv_video, rv_web;
    private VideoAdapter videoAdapter;
    private Activity activity;
    private NewsPreferences preferences;
    private ApiInterface apiInterface;
    private ArrayList<NewsListModelDatum> newsList = new ArrayList<>();
    private boolean hasMore = false;
    private Realm realm;
    private CategoryWiseNewsListModel categoryWiseNewsListModel;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tv_video, tv_web;
    private String status = "video";
    private WebSeriesAdapter webAdapter;
    private ArrayList<VideoWebSeriesModel.Data.Video> videoList = new ArrayList<>();
    private ArrayList<VideoWebSeriesModel.Data.Webseries> webList = new ArrayList<>();
    private String clicked = "video";

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video, container, false);
        activity = getActivity();
        realm = Utils.getRealmInstance(activity);

        init();
        listener();
        callGetVideoNews(0);
        return view;
    }


    private void listener() {
        tv_video.setOnClickListener(v -> {
            clicked = "video";
            tv_video.setBackground(getResources().getDrawable(R.drawable.round_corner_button_blue));
            tv_video.setTextColor(getResources().getColor(R.color.white));

            if (preferences.getTheme().equalsIgnoreCase("dark")) {
                tv_web.setBackgroundColor(getResources().getColor(R.color.black));
                tv_web.setTextColor(getResources().getColor(R.color.appBlackxLight));
            } else {
                tv_web.setBackgroundColor(getResources().getColor(R.color.white));
                tv_web.setTextColor(getResources().getColor(R.color.appBlue));
            }

            status = "video";
            callGetVideoNews(0);
        });

        tv_web.setOnClickListener(v -> {
            clicked = "webSeries";
            tv_web.setBackground(getResources().getDrawable(R.drawable.round_corner_button_blue));
            tv_web.setTextColor(getResources().getColor(R.color.white));

            if (preferences.getTheme().equalsIgnoreCase("dark")) {
                tv_video.setBackgroundColor(getResources().getColor(R.color.black));
                tv_video.setTextColor(getResources().getColor(R.color.appBlackxLight));
            } else {
                tv_video.setBackgroundColor(getResources().getColor(R.color.white));
                tv_video.setTextColor(getResources().getColor(R.color.appBlue));
            }

            status = "webseries";
            callGetVideoNews(0);
        });
    }

    private void callGetVideoNews(int postValue) {

        if (!Utils.checkNetworkAvailability(activity)) {
            return;
        }
        if (postValue == 0) {
            if (status.equalsIgnoreCase("video"))
                videoList.clear();
            else
                webList.clear();
        }

        Utils.showProgressDialog(activity);

        if (apiInterface == null)
            apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("post_value", postValue + "");
        map.put("post_type", "video");

        if (status.equalsIgnoreCase("video")) {
            map.put("ftype", "video");
        } else {
            map.put("ftype", "webseries");
        }
//        map.put("cat_id", "1");
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<VideoWebSeriesModel> couponsObservable
                            = s.getVideoList(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessCategoryNews, this::handleError);

    }

    private void OnSuccessCategoryNews(VideoWebSeriesModel modelResult) {
        Utils.hideProgressDialog();
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getData() != null
                && modelResult.getData().getErrorCode() != null
                && modelResult.getData().getVideo() != null) {
            if (modelResult.getData().getErrorCode().equalsIgnoreCase("200")) {
                if (status.equalsIgnoreCase("video")) {
                    videoList.addAll(modelResult.getData().getVideo());
                    setAdapterOther();
                } else {
                    webList.addAll(modelResult.getData().getWebseries());
                    setWebSeriesAdapter();
                }

            } else if (modelResult.getData().getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(activity);
            }/* else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
       */
        } /*else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    */
    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
    }

    private void setRealmData() {
        CategoryWiseNewsListModel data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", 1).findFirst();

        if (data != null && data.getData().size() != 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    data.getData().addAll(categoryWiseNewsListModel.getData());
                    HashSet hs = new HashSet();
                    hs.addAll(data.getData());
                    data.getData().clear();
                    data.getData().addAll(hs);
                    realm.copyToRealmOrUpdate(data);
                }
            });
        } else {

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    categoryWiseNewsListModel.setId(1);
                    realm.copyToRealmOrUpdate(categoryWiseNewsListModel);
                }
            });
        }

        setAdapterOther();
    }

    private void setAdapterOther() {
     /*   RealmResults<CategoryWiseNewsListModel> data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", 1).findAll();

        if (data != null)
            if (data.size() != 0)
                if (data.get(0).getData() != null) {
                    if (videoAdapter == null) {
                        videoAdapter = new VideoAdapter(activity, data.get(0).getData(), this);
                        rv_video.setLayoutManager(new LinearLayoutManager(activity));
                        rv_video.setAdapter(videoAdapter);
                    } else
                        videoAdapter.notifyDataSetChanged();

                }*/
        rv_video.setVisibility(View.VISIBLE);
        rv_web.setVisibility(View.GONE);

        if (videoAdapter == null) {
            videoAdapter = new VideoAdapter(activity, videoList, this);
            rv_video.setLayoutManager(new LinearLayoutManager(activity));
            rv_video.setAdapter(videoAdapter);
        } else
            videoAdapter.updateList(videoList);

    }

    private void setWebSeriesAdapter() {
        rv_web.setVisibility(View.VISIBLE);
        rv_video.setVisibility(View.GONE);

        if (webAdapter == null) {
            webAdapter = new WebSeriesAdapter(activity, webList, this);
            rv_web.setLayoutManager(new LinearLayoutManager(activity));
            rv_web.setAdapter(webAdapter);
        } else
            webAdapter.updateList(webList);

    }


    private void init() {
        preferences = new NewsPreferences(activity);
        rv_video = view.findViewById(R.id.rv_video);
        rv_web = view.findViewById(R.id.rv_web);
        tv_video = view.findViewById(R.id.tv_video);
        tv_web = view.findViewById(R.id.tv_web);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        rv_video.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int mCurItem = 0;
            private boolean mIsReverseScroll = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int position = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastVisibleItemPosition();
                if (categoryWiseNewsListModel != null)
                    if (categoryWiseNewsListModel.getData() != null)
                        if (hasMore && position == videoList.size() - 1) {
                            hasMore = false;
                            callGetVideoNews(position + 1);
                        }
            }
        });

        rv_web.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int mCurItem = 0;
            private boolean mIsReverseScroll = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int position = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastVisibleItemPosition();
                if (categoryWiseNewsListModel != null)
                    if (categoryWiseNewsListModel.getData() != null)
                        if (hasMore && position == webList.size() - 1) {
                            hasMore = false;
                            callGetVideoNews(position + 1);
                        }
            }
        });
    }


    @Override
    public void OnItemClick(String type, int position) {
        switch (type) {
            case "videoClick":
                startActivity(new Intent(activity, EntertainmentActivity.class));
                break;
        }
    }

    public void setFilterPopup() {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        dialog.show();

        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            dialog.findViewById(R.id.rl_address_popup).setBackgroundColor(getResources().getColor(R.color.black));
            ((TextView) dialog.findViewById(R.id.tv_address_popup)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) dialog.findViewById(R.id.tv_select_text)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) dialog.findViewById(R.id.tv_select_text)).setBackgroundColor(getResources().getColor(R.color.appBlackDark));

        } else {
            dialog.findViewById(R.id.rl_address_popup).setBackgroundColor(getResources().getColor(R.color.white));
            ((TextView) dialog.findViewById(R.id.tv_address_popup)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) dialog.findViewById(R.id.tv_select_text)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) dialog.findViewById(R.id.tv_select_text)).setBackgroundColor(getResources().getColor(R.color.grey_bg_color));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            view.findViewById(R.id.rl_main).setBackgroundColor(activity.getResources().getColor(R.color.black));
            view.findViewById(R.id.ll_buttons).setBackground(activity.getResources().getDrawable(R.drawable.round_corner_button_white_dark));
            tv_video.setBackgroundColor(activity.getResources().getColor(R.color.black));
            tv_web.setBackgroundColor(activity.getResources().getColor(R.color.black));

            if (clicked.equalsIgnoreCase("video")) {
                tv_video.setBackground(getResources().getDrawable(R.drawable.round_corner_button_blue));
                tv_video.setTextColor(getResources().getColor(R.color.white));

                tv_web.setBackgroundColor(getResources().getColor(R.color.black));
                tv_web.setTextColor(getResources().getColor(R.color.appBlackxLight));
            } else {
                tv_web.setBackground(getResources().getDrawable(R.drawable.round_corner_button_blue));
                tv_web.setTextColor(getResources().getColor(R.color.white));

                tv_video.setBackgroundColor(getResources().getColor(R.color.black));
                tv_video.setTextColor(getResources().getColor(R.color.appBlackxLight));
            }

        } else {
            view.findViewById(R.id.rl_main).setBackgroundColor(activity.getResources().getColor(R.color.white));
            view.findViewById(R.id.ll_buttons).setBackground(activity.getResources().getDrawable(R.drawable.round_corner_button_white));
            tv_video.setBackgroundColor(activity.getResources().getColor(R.color.white));
            tv_web.setBackgroundColor(activity.getResources().getColor(R.color.white));

            if (clicked.equalsIgnoreCase("video")) {
                tv_video.setBackground(getResources().getDrawable(R.drawable.round_corner_button_blue));
                tv_video.setTextColor(getResources().getColor(R.color.white));

                tv_web.setBackgroundColor(getResources().getColor(R.color.white));
                tv_web.setTextColor(getResources().getColor(R.color.appBlue));
            } else {
                tv_web.setBackground(getResources().getDrawable(R.drawable.round_corner_button_blue));
                tv_web.setTextColor(getResources().getColor(R.color.white));

                tv_video.setBackgroundColor(getResources().getColor(R.color.white));
                tv_video.setTextColor(getResources().getColor(R.color.appBlue));
            }

        }
    }

    @Override
    public void onRefresh() {
        callGetVideoNews(0);
        swipeRefresh.setRefreshing(false);
    }
}