package com.emobx.news.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Activities.EntertainmentDetailActivity;
import com.emobx.news.Activities.LiveStreamActivity;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Adapters.CategoriesHomeAdapter;
import com.emobx.news.Adapters.EntertainmentAdapter;
import com.emobx.news.Adapters.ExploreViewPagerAdapter;
import com.emobx.news.Adapters.FullscreenViewPagerAdapter;
import com.emobx.news.Adapters.PopularNewsAdapter;
import com.emobx.news.Adapters.SelectorAdapter;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.CategoryListModel;
import com.emobx.news.Model.CategoryWiseNewsListModel;
import com.emobx.news.Model.LiveStreamModel;
import com.emobx.news.Model.NewsListModel;
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.Model.NewsSliderListModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.DownloadFile;
import com.emobx.news.Utils.Utils;
import com.emobx.news.YoutubeLiveVideoActivity;
import com.google.android.material.tabs.TabLayout;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class HomeFragment extends Fragment implements View.OnClickListener, AdapterItemClickListner, View.OnTouchListener {

    public final static int REQUEST_CODE = 1010;
    private static int categoryId = -1;
    private static int oldCategoryId = -1;
    ViewGroup _root;
    private View view;
    private TextView tv_explore, tv_entertainment, tv_sports, tv_politics, tv_science, tv_technology, tv_travel, tv_viewall;
    private View vi_explore, vi_entertainment, vi_sports, vi_politics, vi_science, vi_technology, vi_travel;
    private Activity activity;
    private ViewPager view_pager, full_screen_view_pager;
    private RecyclerView rv_popular_news, rv_selector, rv_other_tab, rv_viewall, rv_tab_view;
    private PopularNewsAdapter popularNewsAdapter;
    private SelectorAdapter selectorAdapter;
    private NewsPreferences preferences;
    private ApiInterface apiInterface;
    private RealmResults<NewsListModel> newsModelDBResult;
    private Realm realm;
    private RealmResults<NewsSliderListModel> newsSliderModelDBResult;
    private ArrayList<NewsListModelDatum> data = new ArrayList<>();
    private EntertainmentAdapter entertainmentAdapter;
    private String currentTab = "";
    private ArrayList<String> id = new ArrayList<>();
    private ArrayList<String> url = new ArrayList<>();
    private IntentFilter filter;
    private boolean handlerActive = false;
    private PopularNewsAdapter viewAllNewsAdapter;
    private CategoriesHomeAdapter categoriesHomeAdapter;
    private RealmResults<CategoryListModel> categoryListModel;
    private int postValue = 0;
    private int otherPostValue = 0;
    private CategoryWiseNewsListModel categoryWiseNewsListModel;
    private LinearLayoutManager layoutManager;
    private int oldId = -1;
    private String videoUrl = "";
    private boolean liveVideoPlay = false;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("UpdateLike")) {
                setAdapter();
                callNewsApi("0");
                setAdapterOther();
                callCategoryNewsApi("0");
            } else if (action.equals("updateLiveVideoStatus")) {
                liveVideoPlay = false;
            }
        }
    };
    private int _xDelta;
    private int _yDelta;
    private YouTubePlayerView youTubePlayerView;

    public static String getYoutubeVideoId(String youtubeUrl) {
        String video_id = "";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http")) {

            String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        init();
        listener();

        setViewPager();
        setAdapter();

        callNewsApi("0");

        setCategoryAdapter();
        callCategoryListApi();

        youTubePlayerView = view.findViewById(R.id.youTubePlayerView);

//        _root = (ViewGroup) view.findViewById(R.id.rl_main);
//
//        _view = view.findViewById(R.id.youTubePlayerView);
//
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
////        layoutParams.leftMargin = 50;
////        layoutParams.topMargin = 50;
////        layoutParams.bottomMargin = -250;
////        layoutParams.rightMargin = -250;
////        _view.setLayoutParams(layoutParams);
//
//        _view.setOnTouchListener(this);
//        _root.addView(_view);

        setFullScreenViewpager();

        return view;
    }

    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        Log.e("value>>", X + "," + Y);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if (X > 200 && Y > 400 && X < 900 && Y < 1400) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);
                }
                break;
        }
        _root.invalidate();
        return true;
    }


    private void init() {
        activity = getActivity();
        realm = Utils.getRealmInstance(activity);
        preferences = new NewsPreferences(activity);

        tv_explore = view.findViewById(R.id.tv_explore);
        tv_entertainment = view.findViewById(R.id.tv_entertainment);
        tv_sports = view.findViewById(R.id.tv_sports);
        tv_politics = view.findViewById(R.id.tv_politics);
        tv_science = view.findViewById(R.id.tv_science);
        tv_technology = view.findViewById(R.id.tv_technology);
        tv_travel = view.findViewById(R.id.tv_travel);

        vi_explore = view.findViewById(R.id.vi_explore);
        vi_entertainment = view.findViewById(R.id.vi_entertainment);
        vi_sports = view.findViewById(R.id.vi_sports);
        vi_politics = view.findViewById(R.id.vi_politics);
        vi_science = view.findViewById(R.id.vi_science);
        vi_technology = view.findViewById(R.id.vi_technology);
        vi_travel = view.findViewById(R.id.vi_travel);

        view_pager = view.findViewById(R.id.view_pager);
        full_screen_view_pager = view.findViewById(R.id.full_screen_view_pager);
        rv_popular_news = view.findViewById(R.id.rv_popular_news);
        rv_selector = view.findViewById(R.id.rv_selector);
        rv_other_tab = view.findViewById(R.id.rv_other_tab);
        tv_viewall = view.findViewById(R.id.tv_viewall);
        rv_viewall = view.findViewById(R.id.rv_viewall);
        rv_tab_view = view.findViewById(R.id.rv_tab_view);

        setTabColor();
        tv_explore.setTextColor(getResources().getColor(R.color.appBlue));
        vi_explore.setVisibility(View.VISIBLE);

        callLiveApi();
    }

    private void setLiveStreamButton(LiveStreamModel modelResult) {
        if (modelResult.getData() != null)
            if (modelResult.getData().getBtnposition() != null && !modelResult.getData().getBtnposition().isEmpty())
                if (modelResult.getData().getLiveurl() != null && !modelResult.getData().getLiveurl().isEmpty()) {
                    if (modelResult.getData().getBtnposition().equalsIgnoreCase("left")) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        params.addRule(RelativeLayout.ALIGN_PARENT_START);
                        params.topMargin = 10;
                        params.bottomMargin = 10;
                        params.leftMargin = 10;
                        params.rightMargin = 10;
                        view.findViewById(R.id.live_news).setLayoutParams(params);

                        RelativeLayout.LayoutParams btnBarParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        btnBarParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        btnBarParams.addRule(RelativeLayout.RIGHT_OF, R.id.live_news);
                        btnBarParams.addRule(RelativeLayout.END_OF, R.id.live_news);
                        btnBarParams.topMargin = 10;
                        btnBarParams.bottomMargin = 10;
                        btnBarParams.leftMargin = 10;
                        btnBarParams.rightMargin = 10;
                        view.findViewById(R.id.rl_youTubePlayerView).setLayoutParams(btnBarParams);
                    } else {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        params.addRule(RelativeLayout.ALIGN_PARENT_END);
                        params.topMargin = 10;
                        params.bottomMargin = 10;
                        params.leftMargin = 10;
                        params.rightMargin = 10;
                        view.findViewById(R.id.live_news).setLayoutParams(params);

                        RelativeLayout.LayoutParams btnBarParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        btnBarParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        btnBarParams.addRule(RelativeLayout.LEFT_OF, R.id.live_news);
                        btnBarParams.addRule(RelativeLayout.START_OF, R.id.live_news);
                        btnBarParams.topMargin = 10;
                        btnBarParams.bottomMargin = 10;
                        btnBarParams.leftMargin = 10;
                        btnBarParams.rightMargin = 10;
                        view.findViewById(R.id.rl_youTubePlayerView).setLayoutParams(btnBarParams);
                    }


                    String url1 = "";
                    try {
                        url1 = URLDecoder.decode(modelResult.getData().getLiveurl(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (!url1.startsWith("http"))
                        url1 = "http/" + url1;
                    videoUrl = getYoutubeVideoId(url1);


                }
    }

    private void listener() {
        view.findViewById(R.id.ll_explore).setOnClickListener(this);
        view.findViewById(R.id.ll_entertainment).setOnClickListener(this);
        view.findViewById(R.id.ll_sports).setOnClickListener(this);
        view.findViewById(R.id.ll_politics).setOnClickListener(this);
        view.findViewById(R.id.ll_science).setOnClickListener(this);
        view.findViewById(R.id.ll_technology).setOnClickListener(this);
        view.findViewById(R.id.ll_travel).setOnClickListener(this);
        view.findViewById(R.id.live_news).setOnClickListener(this);
        tv_viewall.setOnClickListener(this);

        ((SwipeRefreshLayout) view.findViewById(R.id.explore_pull_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                callNewsApi("0");
                ((SwipeRefreshLayout) view.findViewById(R.id.explore_pull_refresh)).setRefreshing(false);
            }
        });

        ((SwipeRefreshLayout) view.findViewById(R.id.viewall_pull_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                callNewsApi("0");

                ((SwipeRefreshLayout) view.findViewById(R.id.viewall_pull_refresh)).setRefreshing(false);
            }
        });

        ((SwipeRefreshLayout) view.findViewById(R.id.other_pull_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (categoryId != -1) {
                    oldId = -1;
                    callCategoryNewsApi("0");
                }
                ((SwipeRefreshLayout) view.findViewById(R.id.other_pull_refresh)).setRefreshing(false);
            }
        });

        rv_other_tab.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RealmResults<CategoryWiseNewsListModel> data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", categoryId).findAll();

                if (data != null)
                    if (data.size() != 0)
                        if (data.get(0).getHasMore())
                            if (data.get(0).getData() != null) {
                                if (((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() == data.get(0).getData().size() - 1) {
                                    if (postValue != data.get(0).getData().size()) {
                                        postValue = data.get(0).getData().size();
                                        callCategoryNewsApi(postValue + "");
                                    }
                                }
                            }
            }
        });

        rv_viewall.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newsModelDBResult != null && newsModelDBResult.size() != 0)
                    if (newsModelDBResult.get(0).getHasMore())
                        if (newsModelDBResult.get(0).getData() != null) {

                            if (((LinearLayoutManager) rv_viewall.getLayoutManager()).findLastVisibleItemPosition() == newsModelDBResult.get(0).getData().size() - 1) {
                                if (postValue != newsModelDBResult.get(0).getData().size()) {
                                    postValue = newsModelDBResult.get(0).getData().size();
                                    callNewsApi(postValue + "");
                                }
                            }
                        }

            }
        });

        view.findViewById(R.id.iv_close_live).setOnClickListener(v -> {
//            view.findViewById(R.id.rl_coverView).setVisibility(View.GONE);
            view.findViewById(R.id.rl_youTubePlayerView).setVisibility(View.GONE);
            youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
                @Override
                public void onYouTubePlayer(YouTubePlayer youTubePlayer) {
                    youTubePlayer.pause();
                }
            });
        });
    }

    private void setAdapter() {
        newsModelDBResult = realm.where(NewsListModel.class).findAll();
        if (newsModelDBResult != null && newsModelDBResult.size() != 0)
            if (newsModelDBResult.get(0).getData() != null) {
                RealmList<NewsListModelDatum> locData = new RealmList<>();
                for (int i = 0; i < 10; i++) {
                    if (i < newsModelDBResult.get(0).getData().size())
                        locData.add(newsModelDBResult.get(0).getData().get(i));
                }
                if (popularNewsAdapter == null) {
//                    popularNewsAdapter = new PopularNewsAdapter(activity, locData, this);
                    rv_popular_news.setLayoutManager(new LinearLayoutManager(activity));
                    rv_popular_news.setAdapter(popularNewsAdapter);
                } else
                    popularNewsAdapter.notifyDataSetChanged();
            }
    }

    private void setViewAllAdapter() {

        newsModelDBResult = realm.where(NewsListModel.class).findAll();

        if (newsModelDBResult != null && newsModelDBResult.size() != 0)
            if (newsModelDBResult.get(0).getData() != null) {
                if (viewAllNewsAdapter == null) {
//                    viewAllNewsAdapter = new PopularNewsAdapter(activity, newsModelDBResult.get(0).getData(), this);
                    rv_viewall.setLayoutManager(new LinearLayoutManager(activity));
                    rv_viewall.setAdapter(viewAllNewsAdapter);
                } else
                    viewAllNewsAdapter.notifyDataSetChanged();
            }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_explore:
                view.findViewById(R.id.explore_pull_refresh).setVisibility(View.VISIBLE);
                view.findViewById(R.id.other_pull_refresh).setVisibility(View.GONE);
                view.findViewById(R.id.viewall_pull_refresh).setVisibility(View.GONE);
                setTabColor();
                tv_explore.setTextColor(getResources().getColor(R.color.appBlue));
                vi_explore.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_entertainment:
                currentTab = "Entertainment";
//                callCategoryNewsApi("0", "1");
//                setOtherTabAdapter(currentTab);
                setTabColor();
                tv_entertainment.setTextColor(getResources().getColor(R.color.appBlue));
                vi_entertainment.setVisibility(View.VISIBLE);
//                setEntertainmentScreen();
                break;
            case R.id.ll_sports:
                currentTab = "Sports";
//                setOtherTabAdapter(currentTab);
                setTabColor();
                tv_sports.setTextColor(getResources().getColor(R.color.appBlue));
                vi_sports.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_politics:
                currentTab = "Politics";
//                setOtherTabAdapter(currentTab);
                setTabColor();
                tv_politics.setTextColor(getResources().getColor(R.color.appBlue));
                vi_politics.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_science:
                currentTab = "Science";
//                setOtherTabAdapter(currentTab);
                setTabColor();
                tv_science.setTextColor(getResources().getColor(R.color.appBlue));
                vi_science.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_technology:
                currentTab = "Technology";
//                setOtherTabAdapter(currentTab);
                setTabColor();
                tv_technology.setTextColor(getResources().getColor(R.color.appBlue));
                vi_technology.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_travel:
                currentTab = "Travel";
//                setOtherTabAdapter(currentTab);
                setTabColor();
                tv_travel.setTextColor(getResources().getColor(R.color.appBlue));
                vi_travel.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_viewall:
                view.findViewById(R.id.explore_pull_refresh).setVisibility(View.GONE);
                view.findViewById(R.id.other_pull_refresh).setVisibility(View.GONE);
                view.findViewById(R.id.viewall_pull_refresh).setVisibility(View.VISIBLE);
                setTabColor();
                setViewAllAdapter();
                tv_explore.setTextColor(getResources().getColor(R.color.appBlue));
                vi_explore.setVisibility(View.VISIBLE);
                break;

            case R.id.live_news:
                if (!videoUrl.isEmpty())
                    if (!liveVideoPlay) {
//                        checkDrawOverlayPermission();
//                    view.findViewById(R.id.rl_coverView).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.rl_youTubePlayerView).setVisibility(View.VISIBLE);
                        getLifecycle().addObserver(youTubePlayerView);
                        youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
                            @Override
                            public void onYouTubePlayer(YouTubePlayer youTubePlayer) {
                                youTubePlayer.loadVideo(videoUrl, 0);
                                youTubePlayer.play();
                            }
                        });
                    }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        Log.v("App", "Package Name: " + activity.getApplicationContext().getPackageName());

        // check if we already  have permission to draw over other apps
        if (!Settings.canDrawOverlays(activity)) {
            Log.v("App", "Requesting Permission" + Settings.canDrawOverlays(activity));
            // if not construct intent to request permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.getApplicationContext().getPackageName()));
            // request permission via start activity for result
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            Log.v("App", "We already have permission for it.");
//            liveVideoPlay=true;
            startActivity(new Intent(activity, YoutubeLiveVideoActivity.class).putExtra("videoID", videoUrl));
        }
    }


/*
    private void setOtherTabAdapter() {
//        view.findViewById(R.id.explore_pull_refresh).setVisibility(View.GONE);
//        view.findViewById(R.id.other_pull_refresh).setVisibility(View.VISIBLE);
//        view.findViewById(R.id.viewall_pull_refresh).setVisibility(View.GONE);
//        data.clear();
//        newsModelDBResult = realm.where(NewsListModel.class).findAll();
//
//        if (newsModelDBResult != null && newsModelDBResult.size() != 0)
//            if (newsModelDBResult.get(0).getData() != null) {
//                for (int i = 0; i < newsModelDBResult.get(0).getData().size(); i++) {
//                    if (newsModelDBResult.get(0).getData().get(i).getCategory() != null)
//                        if (newsModelDBResult.get(0).getData().get(i).getCategory().equalsIgnoreCase(categoryType))
//                            data.add(newsModelDBResult.get(0).getData().get(i));
//                }

        RealmResults<CategoryWiseNewsListModel> data = realm.where(CategoryWiseNewsListModel.class).findAll();

        if (data != null && data.size() != 0)
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getId() == categoryId) {
                    if (entertainmentAdapter == null || oldCategoryId != categoryId) {
                        otherPostValue = 0;
                        oldCategoryId = categoryId;
                        entertainmentAdapter = new EntertainmentAdapter(activity, data.get(i).getData(), this);
                        rv_other_tab.setLayoutManager(new LinearLayoutManager(activity));
                        rv_other_tab.setAdapter(entertainmentAdapter);
                    } else
                        entertainmentAdapter.notifyDataSetChanged();
                    break;
                }
            }
    }
*/

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("App", "OnActivity Result.");
        //check if received result code
        //  is equal our requested code for draw permission
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(activity)) {
//                    liveVideoPlay=true;
                    startActivity(new Intent(activity, YoutubeLiveVideoActivity.class).putExtra("videoID", videoUrl));
                }
            }
        }
    }

    private void setTabColor() {
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            tv_explore.setTextColor(getResources().getColor(R.color.appBlackxLight));
            tv_entertainment.setTextColor(getResources().getColor(R.color.appBlackxLight));
            tv_sports.setTextColor(getResources().getColor(R.color.appBlackxLight));
            tv_politics.setTextColor(getResources().getColor(R.color.appBlackxLight));
            tv_science.setTextColor(getResources().getColor(R.color.appBlackxLight));
            tv_technology.setTextColor(getResources().getColor(R.color.appBlackxLight));
            tv_travel.setTextColor(getResources().getColor(R.color.appBlackxLight));
        } else {
            tv_explore.setTextColor(getResources().getColor(R.color.appBlackDark));
            tv_entertainment.setTextColor(getResources().getColor(R.color.appBlackDark));
            tv_sports.setTextColor(getResources().getColor(R.color.appBlackDark));
            tv_politics.setTextColor(getResources().getColor(R.color.appBlackDark));
            tv_science.setTextColor(getResources().getColor(R.color.appBlackDark));
            tv_technology.setTextColor(getResources().getColor(R.color.appBlackDark));
            tv_travel.setTextColor(getResources().getColor(R.color.appBlackDark));
        }
        vi_explore.setVisibility(View.GONE);
        vi_entertainment.setVisibility(View.GONE);
        vi_sports.setVisibility(View.GONE);
        vi_politics.setVisibility(View.GONE);
        vi_science.setVisibility(View.GONE);
        vi_technology.setVisibility(View.GONE);
        vi_travel.setVisibility(View.GONE);
    }

    private void setFullScreenViewpager() {
        FullscreenViewPagerAdapter adapter = new FullscreenViewPagerAdapter(activity, categoryListModel.get(0).getData(), this);
        full_screen_view_pager.setAdapter(adapter);

        full_screen_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (categoriesHomeAdapter != null) {
                    ((CategoriesHomeAdapter) categoriesHomeAdapter).selectedPos = position;
                    ((CategoriesHomeAdapter) categoriesHomeAdapter).notifyDataSetChanged();
                    rv_tab_view.scrollToPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setViewPager() {
       /* newsSliderModelDBResult = realm.where(NewsSliderListModel.class).findAll();
        if (newsSliderModelDBResult != null && newsSliderModelDBResult.size() != 0)
            if (newsSliderModelDBResult.get(0).getData() != null) {
                ExploreViewPagerAdapter adapter = new ExploreViewPagerAdapter(activity, newsSliderModelDBResult.get(0).getData(), this);
                view_pager.setAdapter(adapter);

                setSelectorAdapter(newsSliderModelDBResult.get(0).getData().size());

                view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        selectorAdapter.currentDot = view_pager.getCurrentItem();
                        selectorAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
                tabLayout.setupWithViewPager(view_pager, true);

                setHandler(newsSliderModelDBResult.get(0).getData().size());
            }*/

    }

    private void setHandler(int size) {
        if (!handlerActive) {
            handlerActive = true;
            Handler handler = new Handler();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (view_pager.getCurrentItem() != size - 1)
                        view_pager.setCurrentItem(view_pager.getCurrentItem() + 1);
                    else
                        view_pager.setCurrentItem(0);
                    handler.postDelayed(this, 5000);
                }
            };
            handler.post(runnable);
        }
    }

    private void setSelectorAdapter(int size) {
//        if (selectorAdapter == null) {
//            selectorAdapter = new SelectorAdapter(activity, size, view_pager.getCurrentItem(), this);
//            rv_selector.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
//            rv_selector.setAdapter(selectorAdapter);
//        } else {
//            if (view_pager != null)
//                selectorAdapter.currentDot = view_pager.getCurrentItem();
//            selectorAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void OnItemClick(String type, int position) {
        switch (type) {
            case "viewPagerClick":
                startActivity(new Intent(activity, EntertainmentDetailActivity.class)
                        .putExtra("newsId", newsSliderModelDBResult.get(0).getData().get(position).getId() + ""));
                break;
            case "popularNews":
                startActivity(new Intent(activity, EntertainmentDetailActivity.class)
                        .putExtra("newsId", newsModelDBResult.get(0).getData().get(position).getId() + ""));
                break;
            case "videoClick":
                RealmResults<CategoryWiseNewsListModel> data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", categoryId).findAll();

                if (data != null)
                    if (data.size() != 0)
                        if (data.get(0).getData() != null) {
                            startActivity(new Intent(activity, EntertainmentDetailActivity.class)
                                    .putExtra("newsId", data.get(0).getData().get(position).getId() + "")
                                    .putExtra("catId", data.get(0).getData().get(position).getCatId() + ""));
                        }

                break;
            case "categoryClick":
                if (position == -1) {
                    view.findViewById(R.id.explore_pull_refresh).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.other_pull_refresh).setVisibility(View.GONE);
                    view.findViewById(R.id.viewall_pull_refresh).setVisibility(View.GONE);
                    oldId = -1;
                } else {
                    RealmResults<CategoryListModel> categoryListModel = realm.where(CategoryListModel.class).findAll();

                    if (categoryListModel != null && categoryListModel.size() > 0)
                        if (categoryListModel.get(0).getData() != null) {
                            view.findViewById(R.id.explore_pull_refresh).setVisibility(View.GONE);
                            view.findViewById(R.id.other_pull_refresh).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.viewall_pull_refresh).setVisibility(View.GONE);
                            if (categoryListModel.get(0).getData().get(position).getId() != null) {
                                categoryId = categoryListModel.get(0).getData().get(position).getId();
                                setAdapterOther();
                                callCategoryNewsApi("0");
                            }
                        }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (preferences.getTheme().equalsIgnoreCase("dark")) {

            view.findViewById(R.id.rl_main).setBackgroundColor(activity.getResources().getColor(R.color.black));
            ((TextView) view.findViewById(R.id.tv_popular_news)).setTextColor(activity.getResources().getColor(R.color.appBlackxLight));
        } else {

            view.findViewById(R.id.rl_main).setBackgroundColor(activity.getResources().getColor(R.color.white));
            ((TextView) view.findViewById(R.id.tv_popular_news)).setTextColor(activity.getResources().getColor(R.color.appBlackDark));
        }

        filter = new IntentFilter();
        filter.addAction("UpdateLike");
        filter.addAction("updateLiveVideoStatus");
        activity.registerReceiver(receiver, filter);

//        youTubePlayerView.release();

    }

    @Override
    public void onPause() {
        super.onPause();
//        youTubePlayerView.release();

    }

    private void callNewsApi(String postValue) {
        if (!Utils.checkNetworkAvailability(activity)) {
            return;
        }
        Utils.showProgressDialog(activity);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(activity);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("post_value", postValue);
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<NewsListModel> couponsObservable
                            = s.getNewsList(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessNews, this::handleError);

    }

    private void OnSuccessNews(NewsListModel modelResult) {
//        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                NewsListModel newsModelResult = modelResult;
                setRealmData(newsModelResult);

                if (newsModelResult.getData() != null) {
                    id.clear();
                    url.clear();
                    for (int i = 0; i < newsModelResult.getData().size(); i++) {

                        id.add(newsModelResult.getData().get(i).getId() + "");

                        if (newsModelResult.getData().get(i).getCoverImage().startsWith("http"))
                            url.add(newsModelResult.getData().get(i).getCoverImage());
                        else
                            url.add(Constants.BASE_URl + "/" + newsModelResult.getData().get(i).getCoverImage());

                    }
                }
                callNewsSliderApi();

            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(activity);
            }/* else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
       */
        } /*else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    */
    }

    private void callCategoryNewsApi(String postValue) {

        if (categoryId != -1) {
            if (!Utils.checkNetworkAvailability(activity)) {
                return;
            }
            Utils.showProgressDialog(activity);

            apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
            Utils.showProgressDialog(activity);
            apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

            HashMap<String, String> map = new HashMap<>();
            map.put("post_value", postValue);
//            map.put("post_type", "image");
            map.put("cat_id", categoryId + "");
            map.put("login_token", preferences.getUserData().getLoginToken());
            map.put("device_type", Constants.DEVICE_TYPE);
            map.put("device_token", preferences.getDeviceToken());

            Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                    .flatMap(s -> {
                        Observable<CategoryWiseNewsListModel> couponsObservable
                                = s.getCategoryWiseNewsList(map).subscribeOn(Schedulers.io());
                        return Observable.concatArray(couponsObservable);
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessCategoryNews, this::handleError);
        }
    }

    private void OnSuccessCategoryNews(CategoryWiseNewsListModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                categoryWiseNewsListModel = modelResult;
                setRealmData();

                ArrayList<String> id = new ArrayList<>();
                ArrayList<String> url = new ArrayList<>();
                if (modelResult.getData() != null) {
                    for (int i = 0; i < modelResult.getData().size(); i++) {

                        id.add(modelResult.getData().get(i).getId() + "");

                        if (modelResult.getData().get(i).getCoverImage().startsWith("http"))
                            url.add(modelResult.getData().get(i).getCoverImage());
                        else
                            url.add(Constants.BASE_URl + "/" + modelResult.getData().get(i).getCoverImage());

                    }
                    new DownloadFile(id, url);
                }

            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(activity);
            }/* else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
       */
        } /*else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    */
    }

    private void setRealmData() {
        CategoryWiseNewsListModel data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", categoryId).findFirst();

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

                    categoryWiseNewsListModel.setId(categoryId);
                    realm.copyToRealmOrUpdate(categoryWiseNewsListModel);
                }
            });
        }

        setAdapterOther();
    }

    private void setAdapterOther() {
        RealmResults<CategoryWiseNewsListModel> data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", categoryId).findAll();

        if (data != null)
            if (data.size() != 0)
                if (data.get(0).getData() != null) {
                    if (entertainmentAdapter == null || oldId != categoryId) {
                        oldId = categoryId;
                        entertainmentAdapter = new EntertainmentAdapter(activity, data.get(0).getData(), this);
                        layoutManager = new LinearLayoutManager(activity);
                        rv_other_tab.setLayoutManager(layoutManager);
                        rv_other_tab.setAdapter(entertainmentAdapter);
                    } else
                        entertainmentAdapter.notifyDataSetChanged();

                }
    }

    private void callNewsSliderApi() {
        if (!Utils.checkNetworkAvailability(activity)) {
            return;
        }
        Utils.showProgressDialog(activity);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(activity);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<NewsSliderListModel> couponsObservable
                            = s.getNewSliderList(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessNewsSlider, this::handleError);

    }

    private void OnSuccessNewsSlider(NewsSliderListModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                NewsSliderListModel newsSliderModelResult = modelResult;
                setRealmDataNewsSlider(newsSliderModelResult);

                if (newsSliderModelResult.getData() != null) {
                    for (int i = 0; i < newsSliderModelResult.getData().size(); i++) {

                        id.add(newsSliderModelResult.getData().get(i).getId() + "");

                        if (newsSliderModelResult.getData().get(i).getCoverImage().startsWith("http"))
                            url.add(newsSliderModelResult.getData().get(i).getCoverImage());
                        else
                            url.add(Constants.BASE_URl + "/" + newsSliderModelResult.getData().get(i).getCoverImage());

                    }
                    new DownloadFile(id, url);
                }
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(activity);
            }/* else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
       */
        } /*else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    */
    }

    private void setRealmData(NewsListModel newsModelResult) {
        RealmResults<NewsListModel> data = realm.where(NewsListModel.class).findAll();

        if (data.size() != 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    data.get(0).getData().addAll(newsModelResult.getData());
                    HashSet hs = new HashSet();
                    hs.addAll(data.get(0).getData());
                    data.get(0).getData().clear();
                    data.get(0).getData().addAll(hs);
                    realm.copyToRealmOrUpdate(data.get(0));
                }
            });
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    newsModelResult.setId(1);
                    realm.copyToRealmOrUpdate(newsModelResult);
                }
            });
        }
        setAdapter();
        setViewAllAdapter();
    }

    private void setRealmDataNewsSlider(NewsSliderListModel newsSliderModelResult) {
        RealmResults<NewsSliderListModel> data = realm.where(NewsSliderListModel.class).findAll();

        if (data.size() != 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    data.deleteAllFromRealm();
                    newsSliderModelResult.setId(1);
                    realm.copyToRealmOrUpdate(newsSliderModelResult);
                }
            });
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    newsSliderModelResult.setId(1);
                    realm.copyToRealm(newsSliderModelResult);
                }
            });
        }
        setViewPager();
    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(activity, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void callCategoryListApi() {
        if (!Utils.checkNetworkAvailability(activity)) {
            return;
        }
        Utils.showProgressDialog(activity);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(activity);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<CategoryListModel> couponsObservable
                            = s.getCategoryList(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessCategoryList, this::handleError);

    }

    private void OnSuccessCategoryList(CategoryListModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                CategoryListModel categoryListModel = modelResult;
                setRealmCategoryData(categoryListModel);

                if (categoryListModel.getData() != null) {
                    for (int i = 0; i < categoryListModel.getData().size(); i++) {

                        id.add(categoryListModel.getData().get(i).getId() + "");

                        if (categoryListModel.getData().get(i).getCoverImage().startsWith("http"))
                            url.add(categoryListModel.getData().get(i).getCoverImage());
                        else
                            url.add(Constants.BASE_URl + "/" + categoryListModel.getData().get(i).getCoverImage());

                    }
                    new DownloadFile(id, url);
                }
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(activity);
            }/* else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
       */
        } /*else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    */
    }

    private void setRealmCategoryData(CategoryListModel categoryListModel) {
        RealmResults<CategoryListModel> data = realm.where(CategoryListModel.class).findAll();

        if (data.size() != 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    data.deleteAllFromRealm();
                    categoryListModel.setId(1);
                    realm.copyToRealmOrUpdate(categoryListModel);
                }
            });
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    categoryListModel.setId(1);
                    realm.copyToRealm(categoryListModel);
                }
            });
        }
        setCategoryAdapter();
    }

    private void setRealmCategoryNewsData(CategoryWiseNewsListModel categoryWiseNewsListModel) {
        RealmResults<CategoryWiseNewsListModel> data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", categoryId).findAll();

        if (data.size() != 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (int i = 0; i < categoryWiseNewsListModel.getData().size(); i++) {
                        boolean valPres = false;
                        for (int j = 0; j < data.get(0).getData().size(); j++) {
                            if (categoryWiseNewsListModel.getData().get(i).getId() == data.get(0).getData().get(j).getId()) {
                                valPres = true;
                                data.get(0).getData().set(j, categoryWiseNewsListModel.getData().get(i));
                            }
                        }
                        if (!valPres)
                            data.get(0).getData().add(categoryWiseNewsListModel.getData().get(i));
                    }


                    data.get(0).setHasMore(categoryWiseNewsListModel.getHasMore());
                    data.get(0).setTotalLikes(categoryWiseNewsListModel.getTotalLikes());
                    data.get(0).setErrorCode(categoryWiseNewsListModel.getErrorCode());
                    realm.copyToRealmOrUpdate(categoryWiseNewsListModel);
                }
            });
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    categoryWiseNewsListModel.setId(categoryId);
                    realm.copyToRealm(categoryWiseNewsListModel);
                }
            });
        }
//        setOtherTabAdapter();
    }

    private void setCategoryAdapter() {
        categoryListModel = realm.where(CategoryListModel.class).findAll();
        if (categoryListModel != null && categoryListModel.size() != 0)
            if (categoryListModel.get(0).getData() != null) {
                if (categoriesHomeAdapter == null) {
                    categoriesHomeAdapter = new CategoriesHomeAdapter(activity, categoryListModel.get(0).getData(), this, 0);
                    rv_tab_view.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                    rv_tab_view.setAdapter(categoriesHomeAdapter);
                } else
                    categoriesHomeAdapter.notifyDataSetChanged();
            }
    }


    private void callLiveApi() {
        if (!Utils.checkNetworkAvailability(activity)) {
            return;
        }
        Utils.showProgressDialog(activity);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(activity);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<LiveStreamModel> couponsObservable
                            = s.callLiveStream(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessLive, this::handleError);

    }

    private void OnSuccessLive(LiveStreamModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                setLiveStreamButton(modelResult);
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(activity);
            } /*else
                Toast.makeText(activity, modelResult.getMessage(), Toast.LENGTH_SHORT).show();*/
        } /*else
            Toast.makeText(activity, modelResult.getMessage(), Toast.LENGTH_SHORT).show();*/
    }

}