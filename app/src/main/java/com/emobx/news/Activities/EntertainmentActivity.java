package com.emobx.news.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Adapters.EntertainmentAdapter;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.CategoryListDatumModel;
import com.emobx.news.Model.CategoryListModel;
import com.emobx.news.Model.CategoryWiseNewsListModel;
import com.emobx.news.Model.NewsBookmarkModel;
import com.emobx.news.Model.NewsLikeModel;
import com.emobx.news.Model.NewsListModel;
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.DownloadFile;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class EntertainmentActivity extends BaseActivity implements View.OnClickListener, AdapterItemClickListner {

    private EntertainmentAdapter entertainmentAdapter;
    private RecyclerView rv_video;
    private NewsPreferences preferences;
    private Realm realm;
    private RealmResults<NewsListModel> newsModelDBResult;
    private RealmList<NewsListModelDatum> data = new RealmList<>();
    private TextView tv_heading, tv_heading1, tv_likes, tv_detail, tv_time;
    private NewsListModelDatum newsDetail;
    private ImageView iv_like_click, iv_bookmark_click;
    private ApiInterface apiInterface;
    private String newId = "";
    private String categoryType = "";
    private IntentFilter filter;
    private String categoryId;
    private CategoryListDatumModel categoryListResult;
    private CategoryWiseNewsListModel categoryWiseNewsListModel;
    private int postValue = 0;
    private LinearLayoutManager layoutManager;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("UpdateLike")) {
                callNewsApi("0");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertainment);
        Utils.setStatusBarTransparent(this);

        init();
        listener();
        getIntentData();
    }

/*    private void getIntentData1() {
        if (getIntent().hasExtra("category")) {
            categoryType = getIntent().getStringExtra("category");
            findViewById(R.id.rl_detail).setVisibility(View.GONE);
            findViewById(R.id.iv_share).setVisibility(View.GONE);
            findViewById(R.id.tv_entertainmenttext).setVisibility(View.VISIBLE);
            rv_video.setVisibility(View.VISIBLE);

            if (categoryType.equalsIgnoreCase("Entertainment"))
                ((ImageView) findViewById(R.id.iv_mainImage)).setImageDrawable(getResources().getDrawable(R.drawable.entertainment));
            else if (categoryType.equalsIgnoreCase("Travel"))
                ((ImageView) findViewById(R.id.iv_mainImage)).setImageDrawable(getResources().getDrawable(R.drawable.travel));
            else if (categoryType.equalsIgnoreCase("Science"))
                ((ImageView) findViewById(R.id.iv_mainImage)).setImageDrawable(getResources().getDrawable(R.drawable.science));
            else if (categoryType.equalsIgnoreCase("Sports"))
                ((ImageView) findViewById(R.id.iv_mainImage)).setImageDrawable(getResources().getDrawable(R.drawable.sports));
            else if (categoryType.equalsIgnoreCase("Technology"))
                ((ImageView) findViewById(R.id.iv_mainImage)).setImageDrawable(getResources().getDrawable(R.drawable.technology));
            else if (categoryType.equalsIgnoreCase("Politics"))
                ((ImageView) findViewById(R.id.iv_mainImage)).setImageDrawable(getResources().getDrawable(R.drawable.politics));

            ((TextView) findViewById(R.id.tv_entertainmenttext)).setText(categoryType);

//            setAdapter();
//            callNewsApi();
        }

        if (getIntent().hasExtra("categoryId")) {
            categoryId = getIntent().getStringExtra("categoryId");
            if (categoryId != null) {
                findViewById(R.id.rl_detail).setVisibility(View.GONE);
                findViewById(R.id.iv_share).setVisibility(View.GONE);
                findViewById(R.id.tv_entertainmenttext).setVisibility(View.VISIBLE);
                rv_video.setVisibility(View.VISIBLE);

                CategoryListModel categoryListResultLoc = realm.where(CategoryListModel.class).equalTo("Id", Integer.parseInt(categoryId)).findFirst();

                if (categoryListResultLoc != null)
                    if (categoryListResultLoc.getData() != null)
                        for (int i = 0; i < categoryListResultLoc.getData().size(); i++) {

                            categoryListResult = categoryListResultLoc.getData().get(i);

                            if (categoryListResult.getId().toString().equalsIgnoreCase(categoryId)) {
                                String url = "";
                                if (categoryListResult.getCoverImage() != null && !categoryListResult.getCoverImage().isEmpty())
                                    if (categoryListResult.getCoverImage().startsWith("http"))
                                        url = categoryListResult.getCoverImage();
                                    else
                                        url = Constants.BASE_URl + "/" + categoryListResult.getCoverImage();


                                File outputFile = new File(Constants.image_dir_path
                                        + "/" + categoryListResult.getId() + "_" + Utils.getFileName(url));
                                if (outputFile != null)
                                    if (outputFile.exists())
                                        Picasso.with(this).load(outputFile).into(((ImageView) findViewById(R.id.iv_mainImage)));
                                    else
                                        Picasso.with(this).load(url).into(((ImageView) findViewById(R.id.iv_mainImage)));
                                else
                                    Picasso.with(this).load(url).into(((ImageView) findViewById(R.id.iv_mainImage)));


                                if (categoryListResult.getName() != null)
                                    ((TextView) findViewById(R.id.tv_entertainmenttext)).setText(categoryListResult.getName());


//                                    setAdapter();
//                                    callNewsApi("0");
                            }
                        }
            }
        }

    }*/

    private void getIntentData() {
        if (getIntent().hasExtra("categoryId")) {
            categoryId = getIntent().getStringExtra("categoryId");
            if (categoryId != null) {
                findViewById(R.id.rl_detail).setVisibility(View.GONE);
                findViewById(R.id.iv_share).setVisibility(View.GONE);
                findViewById(R.id.tv_entertainmenttext).setVisibility(View.VISIBLE);
                rv_video.setVisibility(View.VISIBLE);

                setData();
                callNewsApi("0");
            }
        }
    }


    private void init() {
        realm = Utils.getRealmInstance(this);

        preferences = new NewsPreferences(this);
        rv_video = findViewById(R.id.rv_video);
        tv_heading = findViewById(R.id.tv_heading);
        tv_likes = findViewById(R.id.tv_likes);
        tv_heading1 = findViewById(R.id.tv_heading1);
        tv_detail = findViewById(R.id.tv_detail);
        tv_time = findViewById(R.id.tv_time);
        iv_like_click = findViewById(R.id.iv_like_click);
        iv_bookmark_click = findViewById(R.id.iv_bookmark_click);
    }

    private void listener() {
        findViewById(R.id.tv_comments).setOnClickListener(this);
        findViewById(R.id.iv_back_arrow).setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);
        iv_like_click.setOnClickListener(this);
        iv_bookmark_click.setOnClickListener(this);

        ((SwipeRefreshLayout) findViewById(R.id.pull_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                entertainmentAdapter = null;
                callNewsApi("0");
                ((SwipeRefreshLayout) findViewById(R.id.pull_refresh)).setRefreshing(false);
            }
        });

        rv_video.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RealmResults<CategoryWiseNewsListModel> data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", Integer.parseInt(categoryId)).findAll();

                if (data != null)
                    if (data.size() != 0)
                        if (data.get(0).getHasMore())
                            if (data.get(0).getData() != null) {
                                if (((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() == data.get(0).getData().size() - 1) {
                                    if (postValue != data.get(0).getData().size()) {
                                        postValue = data.get(0).getData().size();
                                        callNewsApi(postValue + "");
                                    }
                                }
                            }

            }
        });
    }

    private void setAdapter1() {
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

//        RealmResults<CategoryWiseNewsListModel> dataLoc = realm.where(CategoryWiseNewsListModel.class).equalTo("Id", categoryListResult.getId()).findAll();

//        if (dataLoc != null)
//            if (dataLoc.size() > 0)
//                if (dataLoc.get(0).getData() != null) {
//                    if (entertainmentAdapter == null) {
//                        entertainmentAdapter = new EntertainmentAdapter(this, dataLoc.get(0).getData(), this);
//                        rv_video.setLayoutManager(new LinearLayoutManager(this));
//                        rv_video.setAdapter(entertainmentAdapter);
//                    } else
//                        entertainmentAdapter.notifyDataSetChanged();
//                }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_comments:
                if (newsDetail != null)
                    if (newsDetail.getId() != null)
                        startActivity(new Intent(this, CommentActivity.class).putExtra("newsId", newsDetail.getId() + ""));
                break;
            case R.id.iv_back_arrow:
                onBackPressed();
                break;
            case R.id.iv_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Satluj Network");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.iv_like_click:
                callLikeApi();
                break;
            case R.id.iv_bookmark_click:
                callBookmarkApi();
                break;
        }
    }

    @Override
    public void OnItemClick(String type, int position) {
        switch (type) {
            case "videoClick":
                RealmResults<CategoryWiseNewsListModel> data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", Integer.parseInt(categoryId)).findAll();

                if (data != null)
                    if (data.size() != 0)
                        if (data.get(0).getData() != null) {
                            startActivity(new Intent(this, EntertainmentDetailActivity.class)
                                    .putExtra("newsId", data.get(0).getData().get(position).getId() + "")
                                    .putExtra("catId", data.get(0).getData().get(position).getCatId() + ""));
                        }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);

        if (preferences.getTheme().equalsIgnoreCase("dark")) {
//            Utils.clearLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(this.getResources().getColor(R.color.black));
            ((TextView) findViewById(R.id.tv_heading)).setTextColor(this.getResources().getColor(R.color.appBlackxLight));
            ((TextView) findViewById(R.id.tv_heading1)).setTextColor(this.getResources().getColor(R.color.appBlackxLight));
            ((TextView) findViewById(R.id.tv_detail)).setTextColor(this.getResources().getColor(R.color.appBlackxLight));
            ((TextView) findViewById(R.id.tv_time)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.time_white, 0, 0, 0);
            ((TextView) findViewById(R.id.tv_time)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) findViewById(R.id.tv_likes)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_white, 0, 0, 0);
            ((TextView) findViewById(R.id.tv_likes)).setTextColor(getResources().getColor(R.color.appBlackxLight));
        } else {
//            Utils.setLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(this.getResources().getColor(R.color.white));
            ((TextView) findViewById(R.id.tv_heading)).setTextColor(this.getResources().getColor(R.color.appBlackDark));
            ((TextView) findViewById(R.id.tv_heading1)).setTextColor(this.getResources().getColor(R.color.appBlackDark));
            ((TextView) findViewById(R.id.tv_detail)).setTextColor(this.getResources().getColor(R.color.appBlackDark));
            ((TextView) findViewById(R.id.tv_time)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.time, 0, 0, 0);
            ((TextView) findViewById(R.id.tv_time)).setTextColor(getResources().getColor(R.color.appBlackLight));
            ((TextView) findViewById(R.id.tv_likes)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.likes, 0, 0, 0);
            ((TextView) findViewById(R.id.tv_likes)).setTextColor(getResources().getColor(R.color.appBlackLight));
        }

        filter = new IntentFilter();
        filter.addAction("UpdateLike");
        registerReceiver(receiver, filter);
    }

    private void callLikeApi() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);


        HashMap<String, String> map = new HashMap<>();
        map.put("news_id", newId);
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<NewsLikeModel> couponsObservable
                            = s.callNewsLike(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessLike, this::handleError);

    }

    private void OnSuccessLike(NewsLikeModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                if (modelResult.getData() != null)
                    if (modelResult.getData().getIsLike() != null)
                        if (!modelResult.getData().getIsLike().isEmpty())
                            if (modelResult.getData().getIsLike().equalsIgnoreCase("yes")) {
                                iv_like_click.setImageDrawable(getResources().getDrawable(R.drawable.select_like));
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        newsDetail.setIsLike("yes");
                                        if (newsDetail.getLikes() != null)
                                            newsDetail.setLikes(newsDetail.getLikes() + 1);
                                        else
                                            newsDetail.setLikes(1);
                                    }
                                });
                            } else {
                                iv_like_click.setImageDrawable(getResources().getDrawable(R.drawable.like));
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        newsDetail.setIsLike("no");

                                        if (newsDetail.getLikes() != null)
                                            if (newsDetail.getLikes() > 0)
                                                newsDetail.setLikes(newsDetail.getLikes() - 1);
                                            else
                                                newsDetail.setLikes(0);
                                    }
                                });
                            }
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            } else
                Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void callBookmarkApi() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("news_id", newId);
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<NewsBookmarkModel> couponsObservable
                            = s.callNewBookmark(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessBookmark, this::handleError);

    }

    private void OnSuccessBookmark(NewsBookmarkModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                if (modelResult.getData() != null)
                    if (modelResult.getData().getIsBookmark() != null)
                        if (!modelResult.getData().getIsBookmark().isEmpty())
                            if (modelResult.getData().getIsBookmark().equalsIgnoreCase("yes")) {
                                iv_bookmark_click.setImageDrawable(getResources().getDrawable(R.drawable.select_bookmark));
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        newsDetail.setIsBookmark("yes");

                                    }
                                });
                            } else {
                                iv_bookmark_click.setImageDrawable(getResources().getDrawable(R.drawable.copy));
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        newsDetail.setIsBookmark("no");

                                    }
                                });
                            }
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            } else
                Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void callNewsApi(String postValue) {
        if (categoryId != null) {

            if (!Utils.checkNetworkAvailability(this)) {
                setAdapter();
                return;
            }
            Utils.showProgressDialog(this);

            apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
            Utils.showProgressDialog(this);
            apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

            HashMap<String, String> map = new HashMap<>();
            map.put("post_value", postValue);
//            map.put("post_type", "image");
            map.put("cat_id", categoryId);
            map.put("login_token", preferences.getUserData().getLoginToken());
            map.put("device_type", Constants.DEVICE_TYPE);
            map.put("device_token", preferences.getDeviceToken());

            Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                    .flatMap(s -> {
                        Observable<CategoryWiseNewsListModel> couponsObservable
                                = s.getCategoryWiseNewsList(map).subscribeOn(Schedulers.io());
                        return Observable.concatArray(couponsObservable);
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessNews, this::handleError);
        }
    }

    private void OnSuccessNews(CategoryWiseNewsListModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                categoryWiseNewsListModel = modelResult;
                setRealmData(modelResult);

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
                Utils.sessionExpired(this);
            }/* else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
       */
        } /*else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    */
    }

    private void setRealmData(CategoryWiseNewsListModel modelResult) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                CategoryWiseNewsListModel data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", Integer.parseInt(categoryId)).findFirst();
                if (data != null && data.getData() != null)
                    data.getData().deleteAllFromRealm();
                modelResult.setId(Integer.parseInt(categoryId));
                realm.copyToRealmOrUpdate(modelResult);
            }
        });


        setAdapter();
    }

    private void setAdapter() {
        RealmResults<CategoryWiseNewsListModel> data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", Integer.parseInt(categoryId)).findAll();

        if (data.size() != 0)
            if (data.get(0).getData() != null) {
                if (entertainmentAdapter == null) {
                    entertainmentAdapter = new EntertainmentAdapter(this, data.get(0).getData(), this);
                    layoutManager = new LinearLayoutManager(this);
                    rv_video.setLayoutManager(layoutManager);
                    rv_video.setAdapter(entertainmentAdapter);
                } else
                    entertainmentAdapter.updateList(data.get(0).getData());

            }
    }

    private void setData() {
        CategoryListDatumModel categoryListResultLoc = realm.where(CategoryListDatumModel.class).equalTo("id", Integer.parseInt(categoryId)).findFirst();

        if (categoryListResultLoc != null)
            if (categoryListResultLoc.getName() != null && !categoryListResultLoc.getName().isEmpty())
                if (categoryListResultLoc.getCoverImage() != null && !categoryListResultLoc.getCoverImage().isEmpty()) {

                    String url = "";
                    if (categoryListResultLoc.getCoverImage() != null && !categoryListResultLoc.getCoverImage().isEmpty())
                        if (categoryListResultLoc.getCoverImage().startsWith("http"))
                            url = categoryListResultLoc.getCoverImage();
                        else
                            url = Constants.BASE_URl + "/" + categoryListResultLoc.getCoverImage();


                    File outputFile = new File(Constants.image_dir_path
                            + "/" + categoryListResultLoc.getId() + "_" + Utils.getFileName(url));
                    if (outputFile != null)
                        if (outputFile.exists())
                            Picasso.with(this).load(outputFile).into(((ImageView) findViewById(R.id.iv_mainImage)));
                        else
                            Picasso.with(this).load(url).into(((ImageView) findViewById(R.id.iv_mainImage)));
                    else
                        Picasso.with(this).load(url).into(((ImageView) findViewById(R.id.iv_mainImage)));


                    if (categoryListResultLoc.getName() != null)
                        ((TextView) findViewById(R.id.tv_entertainmenttext)).setText(categoryListResultLoc.getName());
                }
    }
}