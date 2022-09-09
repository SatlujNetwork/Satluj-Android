package com.emobx.news.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Activities.EntertainmentDetailActivity;
import com.emobx.news.Activities.PlayerActivity;
import com.emobx.news.Activities.WebSeriesDetailActivity;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Adapters.CategoriesHomeAdapter;
import com.emobx.news.Adapters.EntertainmentAdapter;
import com.emobx.news.Adapters.ExploreViewPagerAdapter;
import com.emobx.news.Adapters.FullscreenViewPagerAdapter;
import com.emobx.news.Adapters.PopularNewsAdapter;
import com.emobx.news.Adapters.SelectorAdapter;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.BannerModel;
import com.emobx.news.Model.CategoryListDatumModel;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

public class DynamicFragment extends Fragment implements View.OnClickListener, AdapterItemClickListner, View.OnTouchListener {

    public final static int REQUEST_CODE = 1010;
    ViewGroup _root;
    RealmList<NewsListModelDatum> newsList = new RealmList<>();
    RealmResults<CategoryWiseNewsListModel> dataOther;
    private View view;
    private TextView tv_explore, tv_entertainment, tv_sports, tv_politics, tv_science, tv_technology, tv_travel, tv_viewall;
    private View vi_explore, vi_entertainment, vi_sports, vi_politics, vi_science, vi_technology, vi_travel;
    private Activity activity;
    private ViewPager view_pager, full_screen_view_pager;
    private RecyclerView rv_popular_news, rv_selector, rv_tab_view;
    private PopularNewsAdapter popularNewsAdapter;
    private SelectorAdapter selectorAdapter;
    private NewsPreferences preferences;
    private ApiInterface apiInterface;
    private RealmResults<NewsListModel> newsModelDBResult;
    private Realm realm;
    //    private RealmResults<NewsSliderListModel> newsSliderModelDBResult;
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
    private int categoryId = -1;
    private int postValue = 0;
    private CategoryWiseNewsListModel categoryWiseNewsListModel;
    private LinearLayoutManager layoutManager;
    private String videoUrl = "";
    private boolean liveVideoPlay = false;
    private int _xDelta;
    private int _yDelta;
    private YouTubePlayerView youTubePlayerView;
    private RelativeLayout rl_progress_view;
    private NestedScrollView nested_scroll_view;
    private boolean popularNews = true;
    private PopularNewsAdapter popularNewsAdapterViewAll;
    private RealmResults<BannerModel> bannerModelResult;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("UpdateLike")) {
                setData();
            } else if (action.equals("updateLiveVideoStatus")) {
//                liveVideoPlay = false;
            }
        }
    };

    public static DynamicFragment newInstance(Integer id) {
        DynamicFragment fragment = new DynamicFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fragment.setArguments(args);
        return fragment;
    }

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // adding the layout with inflater
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        initViews(view);

        return view;
    }

    // initialise the categories
    private void initViews(View view) {
//        TextView textView = view.findViewById(R.id.commonTextView);
//        textView.setText(String.valueOf("Category : " + getArguments().getInt("position")));

        init();
        listener();

        categoryId = getArguments().getInt("id", -1);
        setData();

    }

    private void setData() {
        if (categoryId <= 0) {
            view.findViewById(R.id.ll_top_slider).setVisibility(View.VISIBLE);
            popularNews = true;

            callNewsApi("0");
            setCategoryAdapter();
            callCategoryListApi();
        } else {
            // setAdapterOther();
            view.findViewById(R.id.ll_top_slider).setVisibility(View.GONE);

            callCategoryNewsApi("0", getArguments().getInt("id", -1));
        }
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
        rl_progress_view = view.findViewById(R.id.rl_progress_view);
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
        tv_viewall = view.findViewById(R.id.tv_viewall);
        rv_tab_view = view.findViewById(R.id.rv_tab_view);
        youTubePlayerView = view.findViewById(R.id.youTubePlayerView);
        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);

        setTabColor();
        tv_explore.setTextColor(getResources().getColor(R.color.appBlue));
        vi_explore.setVisibility(View.VISIBLE);

//        callLiveApi();
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

        tv_viewall.setOnClickListener(this);

        ((SwipeRefreshLayout) view.findViewById(R.id.explore_pull_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (categoryId > 0)
                    callCategoryNewsApi("0", categoryId);
                else
                    callNewsApi("0");
                ((SwipeRefreshLayout) view.findViewById(R.id.explore_pull_refresh)).setRefreshing(false);
            }
        });

        nested_scroll_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (categoryId <= 0) {

                    } else {
                        if (dataOther != null)
                            if (dataOther.size() != 0)
                                if (dataOther.get(0).getData() != null) {
                                    if (dataOther.get(0).getHasMore())
                                        callCategoryNewsApi(dataOther.get(0).getData().size() - 1 + "", getArguments().getInt("id", -1));
                                }
                    }
                }
            }
        });

    }

    private void setAdapter() {
        if (popularNews) {
            newsModelDBResult = realm.where(NewsListModel.class).findAll();
            if (newsModelDBResult != null && newsModelDBResult.size() != 0)
                if (newsModelDBResult.get(0).getData() != null) {
                    RealmList<NewsListModelDatum> locData = new RealmList<>();
                    for (int i = 0; i < 10; i++) {
                        if (i < newsModelDBResult.get(0).getData().size())
                            locData.add(newsModelDBResult.get(0).getData().get(i));
                    }
                    if (popularNewsAdapter == null) {
                        popularNewsAdapter = new PopularNewsAdapter(activity, locData, this);
                        rv_popular_news.setLayoutManager(new LinearLayoutManager(activity));
                        rv_popular_news.setAdapter(popularNewsAdapter);
                    } else
                        popularNewsAdapter.updateList(locData);
                }
        } else {
            newsModelDBResult = realm.where(NewsListModel.class).findAll();
            if (newsModelDBResult != null && newsModelDBResult.size() != 0)
                if (newsModelDBResult.get(0).getData() != null) {
                    if (popularNewsAdapterViewAll == null) {
                        popularNewsAdapterViewAll = new PopularNewsAdapter(activity, newsModelDBResult.get(0).getData(), this);
                        rv_popular_news.setLayoutManager(new LinearLayoutManager(activity));
                        rv_popular_news.setAdapter(popularNewsAdapterViewAll);
                    } else
                        popularNewsAdapterViewAll.updateList(newsModelDBResult.get(0).getData());
                }
        }
    }

    private void setViewAllAdapter() {

        newsModelDBResult = realm.where(NewsListModel.class).findAll();

        if (newsModelDBResult != null && newsModelDBResult.size() != 0)
            if (newsModelDBResult.get(0).getData() != null) {
                if (viewAllNewsAdapter == null) {
                    viewAllNewsAdapter = new PopularNewsAdapter(activity, newsModelDBResult.get(0).getData(), this);
                    rv_popular_news.setLayoutManager(new LinearLayoutManager(activity));
                    rv_popular_news.setAdapter(viewAllNewsAdapter);
                } else
                    viewAllNewsAdapter.updateList(newsModelDBResult.get(0).getData());
            }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_viewall:
//                setTabColor();
//                setAdapterOther();
//                tv_explore.setTextColor(getResources().getColor(R.color.appBlue));
//                vi_explore.setVisibility(View.VISIBLE);

                view.findViewById(R.id.ll_top_slider).setVisibility(View.GONE);
                popularNews = false;
                callNewsApi("0");
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
        bannerModelResult = realm.where(BannerModel.class).findAll();
        if (bannerModelResult != null && bannerModelResult.size() >= 0)
            if (bannerModelResult.get(0).getData() != null
                    && bannerModelResult.get(0).getData().size() > 0) {
                ExploreViewPagerAdapter adapter = new ExploreViewPagerAdapter(activity, bannerModelResult.get(0).getData(), this);
                view_pager.setAdapter(adapter);

                setSelectorAdapter(bannerModelResult.get(0).getData().size());

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

                setHandler(bannerModelResult.get(0).getData().size());
            }

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
        if (selectorAdapter == null) {
            selectorAdapter = new SelectorAdapter(activity, size, view_pager.getCurrentItem(), this);
            rv_selector.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            rv_selector.setAdapter(selectorAdapter);
        } else {
            if (view_pager != null)
                selectorAdapter.currentDot = view_pager.getCurrentItem();
            selectorAdapter.data = size;
            selectorAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void OnItemClick(String type, int position) {
        switch (type) {
            case "viewPagerClick":
                if (bannerModelResult.get(0).getData().get(position).getTypeContent() != null)
                    switch (bannerModelResult.get(0).getData().get(position).getType()) {
                        case "News":
                            startActivity(new Intent(activity, EntertainmentDetailActivity.class)
                                    .putExtra("newsId", bannerModelResult.get(0).getData().get(position).getTypeContent() + ""));
                            break;
                        case "Webseries":
                            startActivity(new Intent(activity, WebSeriesDetailActivity.class)
                                    .putExtra("seriesId", bannerModelResult.get(0).getData().get(position).getTypeContent() + ""));
                            break;
                        case "Link":
                            String url = bannerModelResult.get(0).getData().get(position).getTypeContent() + "";
                            if (!url.startsWith("http://") && !url.startsWith("https://"))
                                url = Constants.BASE_URl + url;
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);
                            break;
                        default:
                            startActivity(new Intent(activity, EntertainmentDetailActivity.class)
                                    .putExtra("newsId", bannerModelResult.get(0).getData().get(position).getTypeContent() + ""));
                            break;
                    }
                break;
            case "popularNews":
                startActivity(new Intent(activity, EntertainmentDetailActivity.class)
                        .putExtra("newsId", newsModelDBResult.get(0).getData().get(position).getId() + ""));
                break;
            case "videoClick":
//                startActivity(new Intent(activity, EntertainmentDetailActivity.class)
//                        .putExtra("newsId", dataOther.get(0).getData().get(position).getId() + ""));

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
              /*  if (position == -1) {
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
                                //  categoryId = categoryListModel.get(0).getData().get(position).getId();
                                setAdapterOther();
                                callCategoryNewsApi("0");
                            }
                        }
                }*/
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
            setAdapter();
            setViewPager();
            return;
        }

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        rl_progress_view.setVisibility(View.VISIBLE);


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
                callGetBannerApi();

            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                rl_progress_view.setVisibility(View.GONE);
                Utils.sessionExpired(activity);
            } else
                rl_progress_view.setVisibility(View.GONE);
        } /*else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    */
    }

    private void callCategoryNewsApi(String postValue, int id) {

        if (!Utils.checkNetworkAvailability(activity)) {
            setAdapterOther();
            return;
        }

        if (id > 0) {
            if (postValue.equalsIgnoreCase("0"))
                newsList.clear();
            Utils.showProgressDialog(activity);
            apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

            HashMap<String, String> map = new HashMap<>();
            map.put("post_value", postValue);
//            map.put("post_type", "image");
            map.put("cat_id", id + "");
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
                newsList.addAll(modelResult.getData());
                setRealmData(categoryWiseNewsListModel);

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

    private void setRealmData(CategoryWiseNewsListModel categoryWiseNewsListModel) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                CategoryWiseNewsListModel data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", categoryId).findFirst();
                if (data != null && data.getData() != null)
                    data.deleteFromRealm();
                categoryWiseNewsListModel.setId(categoryId);
                realm.copyToRealmOrUpdate(categoryWiseNewsListModel);
            }
        });

        setAdapterOther();
    }

    private void setAdapterOther() {
        dataOther = realm.where(CategoryWiseNewsListModel.class).equalTo("id", categoryId).findAll();

        if (dataOther != null)
            if (dataOther.size() != 0)
                if (dataOther.get(0).getData() != null) {
                    if (entertainmentAdapter == null) {
                        entertainmentAdapter = new EntertainmentAdapter(activity, dataOther.get(0).getData(), this);
                        layoutManager = new LinearLayoutManager(activity);
                        rv_popular_news.setLayoutManager(layoutManager);
                        rv_popular_news.setAdapter(entertainmentAdapter);
                    } else
                        entertainmentAdapter.updateList(dataOther.get(0).getData());

                }
    }

    private void callGetBannerApi() {
        if (!Utils.checkNetworkAvailability(activity)) {
            return;
        }

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<BannerModel> couponsObservable
                            = s.getBanner().subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessGetBanner, this::handleError);

    }

    private void OnSuccessGetBanner(BannerModel modelResult) {
        Utils.hideProgressDialog();
        rl_progress_view.setVisibility(View.GONE);

        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                BannerModel result = modelResult;
                setRealmBannerData(result);

                if (result.getData() != null) {
                    for (int i = 0; i < result.getData().size(); i++) {

                        id.add(result.getData().get(i).getId() + "");

                        if (result.getData().get(i).getCoverImage().startsWith("http"))
                            url.add(result.getData().get(i).getCoverImage());
                        else
                            url.add(Constants.BASE_URl + "/" + result.getData().get(i).getCoverImage());

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

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<NewsListModel> data = realm.where(NewsListModel.class).findAll();
                if (data != null)
                    data.deleteAllFromRealm();
                newsModelResult.setId(1);
                realm.copyToRealmOrUpdate(newsModelResult);
            }
        });

        setAdapter();
//        setViewAllAdapter();
    }

    private void setRealmBannerData(BannerModel result) {
        RealmResults<BannerModel> data = realm.where(BannerModel.class).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<BannerModel> data = realm.where(BannerModel.class).findAll();
                if (data != null)
                    data.deleteAllFromRealm();
                result.setId(1);
                realm.copyToRealmOrUpdate(result);
            }
        });
        setViewPager();
    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        rl_progress_view.setVisibility(View.GONE);
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

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<CategoryListModel> data = realm.where(CategoryListModel.class).findAll();
                if (data != null)
                    data.deleteAllFromRealm();
                categoryListModel.setId(1);
                realm.copyToRealmOrUpdate(categoryListModel);
            }
        });

        setCategoryAdapter();

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
                    categoriesHomeAdapter.updateList(categoryListModel.get(0).getData());
            }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (activity != null && !isVisibleToUser) {
            if (categoryId <= 0) {
                view.findViewById(R.id.ll_top_slider).setVisibility(View.VISIBLE);
                popularNews = true;
            }
        }
    }
}
