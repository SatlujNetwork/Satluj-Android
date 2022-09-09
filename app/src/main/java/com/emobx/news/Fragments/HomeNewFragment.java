package com.emobx.news.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.collection.CircularArray;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Activities.HomeActivity;
import com.emobx.news.Adapters.DynamicFragmentAdapter;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.CategoryListDatumModel;
import com.emobx.news.Model.CategoryListModel;
import com.emobx.news.Model.LiveStreamModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.DownloadFile;
import com.emobx.news.Utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class HomeNewFragment extends Fragment {

    public RealmResults<CategoryListModel> categoryListModel;
    private View view;
    private ViewPager viewPager;
    private TabLayout mTabLayout;
    private SampleFragmentPagerAdapter pagerAdapter;
    private Activity activity;
    private NewsPreferences preferences;
    private ArrayList<String> id = new ArrayList<>();
    private ArrayList<String> url = new ArrayList<>();
    private Realm realm;
    private String videoUrl = "";
    private YouTubePlayerView youTubePlayerView;
    private ImageView iv_close_live;

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
        view = inflater.inflate(R.layout.fragment_home_new, container, false);
        initViews();

        return view;
    }


    private void initViews() {
        activity = getActivity();
        realm = Utils.getRealmInstance(activity);

        preferences = new NewsPreferences(activity);
        // initialise the layout
        viewPager = view.findViewById(R.id.viewpager);
        mTabLayout = view.findViewById(R.id.tabs);
        youTubePlayerView = view.findViewById(R.id.youTubePlayerView);
        iv_close_live = view.findViewById(R.id.iv_close_live);

        // setOffscreenPageLimit means number
        // of tabs to be shown in one page
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // setCurrentItem as the tab position
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        callLiveApi();
        callCategoryListApi();

        view.findViewById(R.id.live_news).setOnClickListener(v -> {
            if (!Utils.checkNetworkAvailability(activity)) {
                return;
            }
            if (!videoUrl.isEmpty()) {
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
        });
        iv_close_live.setOnClickListener(v -> {
            view.findViewById(R.id.rl_youTubePlayerView).setVisibility(View.GONE);
            youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
                @Override
                public void onYouTubePlayer(YouTubePlayer youTubePlayer) {
                    youTubePlayer.pause();
                }
            });
        });

        setDefaultButton();
    }

    private void setDefaultButton() {
        view.findViewById(R.id.live_news).setVisibility(View.GONE);
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

    // show all the tab using DynamicFragmnetAdapter
    private void setDynamicFragmentToTabLayout(RealmList<CategoryListDatumModel> data) {

//        SampleFragmentPagerAdapter pagerAdapter = new SampleFragmentPagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), data);
        // here we have given 10 as the tab number
        // you can give any number here
        ArrayList<String> nameList = new ArrayList<>();

        nameList.add("Explore");
        for (int i = 0; i < data.size(); i++) {
            nameList.add(data.get(i).getName());
        }
        for (int i = 0; i < nameList.size(); i++) {
//            // set the tab name as "Page: " + i
            mTabLayout.addTab(mTabLayout.newTab().setText(nameList.get(i)));

//            TabLayout.Tab tab = mTabLayout.getTabAt(i);
//            tab.setCustomView(pagerAdapter.getTabView(i));
        }
        viewPager.setOffscreenPageLimit(nameList.size());
        DynamicFragmentAdapter mDynamicFragmentAdapter = new DynamicFragmentAdapter(getActivity().getSupportFragmentManager(), mTabLayout.getTabCount(), data);

        // set the adapter
        viewPager.setAdapter(mDynamicFragmentAdapter);

        // set the current item as 0 (when app opens for first time)
        viewPager.setCurrentItem(0);

    }

    private void callCategoryListApi() {
        if (!Utils.checkNetworkAvailability(activity)) {
            setAdapter();
            return;
        }
        Utils.showProgressDialog(activity);

        ApiInterface apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
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
                viewPager.setOffscreenPageLimit(categoryListModel.getData().size() - 1);
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
            if (realm.isInTransaction()) {
                realm.commitTransaction();
            }
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    categoryListModel.setId(1);
                    realm.copyToRealm(categoryListModel);
                }
            });
        }
        setAdapter();
    }

    private void setAdapter() {
        categoryListModel = realm.where(CategoryListModel.class).findAll();
        if (categoryListModel != null && categoryListModel.size() != 0)
            if (categoryListModel.get(0).getData() != null) {
                setDynamicFragmentToTabLayout(categoryListModel.get(0).getData());
            }
    }

    private void callLiveApi() {
        if (!Utils.checkNetworkAvailability(activity)) {
            return;
        }
        Utils.showProgressDialog(activity);

        ApiInterface apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
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

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(activity, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void setLiveStreamButton(LiveStreamModel modelResult) {
        if (modelResult.getData() != null)
            if (modelResult.getData().getBtnposition() != null && !modelResult.getData().getBtnposition().isEmpty())
                if (modelResult.getData().getLiveurl() != null && !modelResult.getData().getLiveurl().isEmpty()) {
                    if (modelResult.getData().getBtnposition().equalsIgnoreCase("close")) {
                        view.findViewById(R.id.live_news).setVisibility(View.GONE);
                    } else if (modelResult.getData().getBtnposition().equalsIgnoreCase("left")) {
                        view.findViewById(R.id.live_news).setVisibility(View.VISIBLE);
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
                        view.findViewById(R.id.live_news).setVisibility(View.VISIBLE);
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

    @Override
    public void onResume() {
        super.onResume();
        if (preferences.getTheme().equalsIgnoreCase("dark")) {

            view.findViewById(R.id.rl_main).setBackgroundColor(activity.getResources().getColor(R.color.black));
            view.findViewById(R.id.tabs).setBackgroundColor(activity.getResources().getColor(R.color.black));
            ((TabLayout) view.findViewById(R.id.tabs)).setTabTextColors(activity.getResources().getColor(R.color.appBlackxLight), activity.getResources().getColor(R.color.appBlue));
        } else {

            view.findViewById(R.id.rl_main).setBackgroundColor(activity.getResources().getColor(R.color.white));
            view.findViewById(R.id.tabs).setBackgroundColor(activity.getResources().getColor(R.color.white));
            ((TabLayout) view.findViewById(R.id.tabs)).setTabTextColors(activity.getResources().getColor(R.color.appBlackDark), activity.getResources().getColor(R.color.appBlue));
        }
    }
}


class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    Context context;
    RealmList<CategoryListDatumModel> data;
    ArrayList<String> nameList = new ArrayList<>();

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context, RealmList<CategoryListDatumModel> data) {
        super(fm);
        this.context = context;
        this.data = data;
        nameList.add("Explore");
        for (int i = 0; i < data.size(); i++) {
            nameList.add(data.get(i).getName());
        }
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) v.findViewById(R.id.tv_explore);
        tv.setText(nameList.get(position));
        return v;
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return data.size() + 1;
    }


}