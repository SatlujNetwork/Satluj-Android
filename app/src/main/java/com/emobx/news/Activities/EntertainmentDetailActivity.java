package com.emobx.news.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Adapters.EntertainmentAdapter;
import com.emobx.news.BuildConfig;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.CategoryWiseNewsListModel;
import com.emobx.news.Model.NewsBookmarkModel;
import com.emobx.news.Model.NewsLikeModel;
import com.emobx.news.Model.NewsListModel;
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.Model.NotificationCountModel;
import com.emobx.news.Model.NotificationListModel;
import com.emobx.news.Model.SingleNewModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.internal.Util;

public class EntertainmentDetailActivity extends BaseActivity implements View.OnClickListener, AdapterItemClickListner {

    private EntertainmentAdapter entertainmentAdapter;
    private RecyclerView rv_video;
    private NewsPreferences preferences;
    private Realm realm;
    private RealmResults<NewsListModel> newsModelDBResult;
    private ArrayList<NewsListModelDatum> data = new ArrayList<>();
    private TextView tv_heading, tv_heading1, tv_likes, tv_detail, tv_time, tv_entertainment;
    private NewsListModelDatum newsDetail;
    private ImageView iv_play, iv_like_click, iv_bookmark_click, iv_mainImage;
    private ApiInterface apiInterface;
    private String newId = "";
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("updateComment")) {
                callSingleNews();
            }
        }
    };
    private String catId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertainment_detail);
        Utils.setStatusBarTransparent(this);

        init();
        listener();
        getIntentData();
    }

    private void getIntentData() {
        if (Utils.checkNetworkAvailability(this) && getIntent().hasExtra("newsId") && !getIntent().getStringExtra("newsId").isEmpty()) {
            newId = getIntent().getStringExtra("newsId");
            callSingleNews();
        } else {
            if (getIntent().hasExtra("NotificationNews")) {
                newsDetail = (NewsListModelDatum) getIntent().getSerializableExtra("NotificationNews");

                if (newsDetail != null) {
                    if (newsDetail.getId() != null)
                        newId = newsDetail.getId().toString();

                    if (newsDetail.getTitle() != null && !newsDetail.getTitle().isEmpty())
                        tv_heading.setText(newsDetail.getTitle() + "");

                    if (newsDetail.getSubTitle() != null && !newsDetail.getSubTitle().isEmpty())
                        tv_heading1.setText(newsDetail.getSubTitle() + "");

                    if (newsDetail.getDescription() != null && !newsDetail.getDescription().isEmpty())
                        tv_detail.setText(Html.fromHtml(newsDetail.getDescription() + ""));

                    if (newsDetail.getLikes() != null && !newsDetail.getLikes().toString().isEmpty())
                        tv_likes.setText(newsDetail.getLikes() + " people like this");

                    if (newsDetail.getComments() != null && !newsDetail.getComments().toString().isEmpty())
                        if (newsDetail.getComments() == 0)
                            ((TextView) findViewById(R.id.tv_comments)).setText(" Comments");
                        else
                            ((TextView) findViewById(R.id.tv_comments)).setText(newsDetail.getComments() + " Comments");

                    if (newsDetail.getCreatedAt() != null && !newsDetail.getCreatedAt().isEmpty())
                        tv_time.setText(Utils.changeDateFormat(newsDetail.getCreatedAt(), "yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy / h:mm a") + "");

                    if (newsDetail.getCategory() != null && !newsDetail.getCategory().isEmpty())
                        tv_entertainment.setText(newsDetail.getCategory());

                    if (newsDetail.getIsLike() != null)
                        if (newsDetail.getIsLike().equalsIgnoreCase("yes"))
                            setLikeClick();
                        else
                            setLikeRemove();

                    if (newsDetail.getIsBookmark() != null)
                        if (newsDetail.getIsBookmark().equalsIgnoreCase("yes"))
                            setBookmarkClick();
                        else
                            setBookmarkRemove();

//                            if (newsDetail.getCoverImage() != null && !newsDetail.getCoverImage().isEmpty())
//                                if (newsDetail.getCoverImage().startsWith("http"))
//                                    Picasso.with(this).load(newsDetail.getCoverImage()).into(iv_mainImage);
//                                else
//                                    Picasso.with(this).load(Constants.BASE_URl + "/" + newsDetail.getCoverImage()).into(iv_mainImage);


                    String url = "";
                    if (newsDetail.getCoverImage() != null && !newsDetail.getCoverImage().isEmpty())
                        if (newsDetail.getCoverImage().startsWith("http"))
                            url = newsDetail.getCoverImage();
                        else
                            url = Constants.BASE_URl + "/" + newsDetail.getCoverImage();


                    File outputFile = new File(Constants.image_dir_path
                            + "/" + newsDetail.getId() + "_" + Utils.getFileName(url));
                    if (outputFile != null)
                        if (outputFile.exists())
                            Picasso.with(this).load(outputFile).into(iv_mainImage);
                        else
                            Picasso.with(this).load(url).into(iv_mainImage);
                    else
                        Picasso.with(this).load(url).into(iv_mainImage);
                }
            }

            if (getIntent().hasExtra("newsId") && getIntent().hasExtra("catId")) {
                newId = getIntent().getStringExtra("newsId");
                catId = getIntent().getStringExtra("catId");
                findViewById(R.id.rl_detail).setVisibility(View.VISIBLE);
                findViewById(R.id.iv_share).setVisibility(View.VISIBLE);
                rv_video.setVisibility(View.GONE);

                RealmResults<CategoryWiseNewsListModel> newsModelDBResult = realm.where(CategoryWiseNewsListModel.class).equalTo("id", Integer.parseInt(catId)).findAll();

                if (newsModelDBResult != null && newsModelDBResult.size() != 0)
                    if (newsModelDBResult.get(0).getData() != null) {
                        for (int i = 0; i < newsModelDBResult.get(0).getData().size(); i++) {
                            if (newsModelDBResult.get(0).getData().get(i).getId().toString()
                                    .equalsIgnoreCase(newId)) {
                                newsDetail = newsModelDBResult.get(0).getData().get(i);

                                if (newsDetail.getTitle() != null && !newsDetail.getTitle().isEmpty())
                                    tv_heading.setText(newsDetail.getTitle() + "");

                                if (newsDetail.getSubTitle() != null && !newsDetail.getSubTitle().isEmpty())
                                    tv_heading1.setText(newsDetail.getSubTitle() + "");

                                if (newsDetail.getDescription() != null && !newsDetail.getDescription().isEmpty())
                                    tv_detail.setText(Html.fromHtml(newsDetail.getDescription() + ""));

                                if (newsDetail.getLikes() != null && !newsDetail.getLikes().toString().isEmpty())
                                    tv_likes.setText(newsDetail.getLikes() + " people like this");

                                if (newsDetail.getComments() != null && !newsDetail.getComments().toString().isEmpty())
                                    if (newsDetail.getComments() == 0)
                                        ((TextView) findViewById(R.id.tv_comments)).setText(" Comments");
                                    else
                                        ((TextView) findViewById(R.id.tv_comments)).setText(newsDetail.getComments() + " Comments");

                                if (newsDetail.getCreatedAt() != null && !newsDetail.getCreatedAt().isEmpty())
                                    tv_time.setText(Utils.changeDateFormat(newsDetail.getCreatedAt(), "yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy / h:mm a") + "");

                                if (newsDetail.getCategory() != null && !newsDetail.getCategory().isEmpty())
                                    tv_entertainment.setText(newsDetail.getCategory());

                                if (newsDetail.getIsLike() != null)
                                    if (newsDetail.getIsLike().equalsIgnoreCase("yes"))
                                        setLikeClick();
                                    else
                                        setLikeRemove();

                                if (newsDetail.getIsBookmark() != null)
                                    if (newsDetail.getIsBookmark().equalsIgnoreCase("yes"))
                                        setBookmarkClick();
                                    else
                                        setBookmarkRemove();

//                            if (newsDetail.getCoverImage() != null && !newsDetail.getCoverImage().isEmpty())
//                                if (newsDetail.getCoverImage().startsWith("http"))
//                                    Picasso.with(this).load(newsDetail.getCoverImage()).into(iv_mainImage);
//                                else
//                                    Picasso.with(this).load(Constants.BASE_URl + "/" + newsDetail.getCoverImage()).into(iv_mainImage);


                                String url = "";
                                if (newsDetail.getCoverImage() != null && !newsDetail.getCoverImage().isEmpty())
                                    if (newsDetail.getCoverImage().startsWith("http"))
                                        url = newsDetail.getCoverImage();
                                    else
                                        url = Constants.BASE_URl + "/" + newsDetail.getCoverImage();


                                File outputFile = new File(Constants.image_dir_path
                                        + "/" + newsDetail.getId() + "_" + Utils.getFileName(url));
                                if (outputFile != null)
                                    if (outputFile.exists())
                                        Picasso.with(this).load(outputFile).into(iv_mainImage);
                                    else
                                        Picasso.with(this).load(url).into(iv_mainImage);
                                else
                                    Picasso.with(this).load(url).into(iv_mainImage);
                            }
                        }
                    }
            }

            if (getIntent().hasExtra("newsId")) {
                newId = getIntent().getStringExtra("newsId");
                findViewById(R.id.rl_detail).setVisibility(View.VISIBLE);
                findViewById(R.id.iv_share).setVisibility(View.VISIBLE);
                rv_video.setVisibility(View.GONE);

                newsModelDBResult = realm.where(NewsListModel.class).findAll();
                if (newsModelDBResult != null && newsModelDBResult.size() != 0)
                    if (newsModelDBResult.get(0).getData() != null) {
                        for (int i = 0; i < newsModelDBResult.get(0).getData().size(); i++) {
                            if (newsModelDBResult.get(0).getData().get(i).getId().toString()
                                    .equalsIgnoreCase(newId)) {
                                newsDetail = newsModelDBResult.get(0).getData().get(i);

                                if (newsDetail.getTitle() != null && !newsDetail.getTitle().isEmpty())
                                    tv_heading.setText(newsDetail.getTitle() + "");

                                if (newsDetail.getSubTitle() != null && !newsDetail.getSubTitle().isEmpty())
                                    tv_heading1.setText(newsDetail.getSubTitle() + "");

                                if (newsDetail.getDescription() != null && !newsDetail.getDescription().isEmpty())
                                    tv_detail.setText(Html.fromHtml(newsDetail.getDescription() + ""));

                                if (newsDetail.getLikes() != null && !newsDetail.getLikes().toString().isEmpty())
                                    tv_likes.setText(newsDetail.getLikes() + " people like this");

                                if (newsDetail.getComments() != null && !newsDetail.getComments().toString().isEmpty())
                                    if (newsDetail.getComments() == 0)
                                        ((TextView) findViewById(R.id.tv_comments)).setText(" Comments");
                                    else
                                        ((TextView) findViewById(R.id.tv_comments)).setText(newsDetail.getComments() + " Comments");

                                if (newsDetail.getCreatedAt() != null && !newsDetail.getCreatedAt().isEmpty())
                                    tv_time.setText(Utils.changeDateFormat(newsDetail.getCreatedAt(), "yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy / h:mm a") + "");

                                if (newsDetail.getCategory() != null && !newsDetail.getCategory().isEmpty())
                                    tv_entertainment.setText(newsDetail.getCategory());

                                if (newsDetail.getIsLike() != null)
                                    if (newsDetail.getIsLike().equalsIgnoreCase("yes"))
                                        setLikeClick();
                                    else
                                        setLikeRemove();

                                if (newsDetail.getIsBookmark() != null)
                                    if (newsDetail.getIsBookmark().equalsIgnoreCase("yes"))
                                        setBookmarkClick();
                                    else
                                        setBookmarkRemove();

//                            if (newsDetail.getCoverImage() != null && !newsDetail.getCoverImage().isEmpty())
//                                if (newsDetail.getCoverImage().startsWith("http"))
//                                    Picasso.with(this).load(newsDetail.getCoverImage()).into(iv_mainImage);
//                                else
//                                    Picasso.with(this).load(Constants.BASE_URl + "/" + newsDetail.getCoverImage()).into(iv_mainImage);


                                String url = "";
                                if (newsDetail.getCoverImage() != null && !newsDetail.getCoverImage().isEmpty())
                                    if (newsDetail.getCoverImage().startsWith("http"))
                                        url = newsDetail.getCoverImage();
                                    else
                                        url = Constants.BASE_URl + "/" + newsDetail.getCoverImage();


                                File outputFile = new File(Constants.image_dir_path
                                        + "/" + newsDetail.getId() + "_" + Utils.getFileName(url));
                                if (outputFile != null)
                                    if (outputFile.exists())
                                        Picasso.with(this).load(outputFile).into(iv_mainImage);
                                    else
                                        Picasso.with(this).load(url).into(iv_mainImage);
                                else
                                    Picasso.with(this).load(url).into(iv_mainImage);
                            }
                        }
                    }
            }
        }
        if (newsDetail != null && newsDetail.getType().equalsIgnoreCase("video")) {
            iv_play.setVisibility(View.VISIBLE);
        } else
            iv_play.setVisibility(View.GONE);

    }

    private void setBookmarkClick() {
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            iv_bookmark_click.setImageDrawable(getResources().getDrawable(R.drawable.selected_bookmark_white));

        } else {
            iv_bookmark_click.setImageDrawable(getResources().getDrawable(R.drawable.select_bookmark));

        }
    }

    private void setBookmarkRemove() {
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            iv_bookmark_click.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_white));

        } else {
            iv_bookmark_click.setImageDrawable(getResources().getDrawable(R.drawable.copy));

        }
    }

    private void setLikeClick() {
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            iv_like_click.setImageDrawable(getResources().getDrawable(R.drawable.select_like));

        } else {
            iv_like_click.setImageDrawable(getResources().getDrawable(R.drawable.select_like));

        }
    }

    private void setLikeRemove() {
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            iv_like_click.setImageDrawable(getResources().getDrawable(R.drawable.like_white_detail));

        } else {
            iv_like_click.setImageDrawable(getResources().getDrawable(R.drawable.like));

        }
    }

    private void init() {
        realm = Utils.getRealmInstance(this);

        preferences = new NewsPreferences(this);
        iv_play = findViewById(R.id.iv_play);
        rv_video = findViewById(R.id.rv_video);
        tv_heading = findViewById(R.id.tv_heading);
        tv_likes = findViewById(R.id.tv_likes);
        tv_heading1 = findViewById(R.id.tv_heading1);
        tv_detail = findViewById(R.id.tv_detail);
        tv_time = findViewById(R.id.tv_time);
        iv_like_click = findViewById(R.id.iv_like_click);
        iv_bookmark_click = findViewById(R.id.iv_bookmark_click);
        iv_mainImage = findViewById(R.id.iv_mainImage);
        tv_entertainment = findViewById(R.id.tv_entertainment);
    }

    private void listener() {
        findViewById(R.id.iv_mainImage).setOnClickListener(this);
        findViewById(R.id.tv_comments).setOnClickListener(this);
        findViewById(R.id.iv_back_arrow).setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);
        iv_like_click.setOnClickListener(this);
        iv_bookmark_click.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_mainImage:
                if (newsDetail != null && newsDetail.getType().equalsIgnoreCase("video")) {
//                    Toast.makeText(this, "In progress...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, PlayerActivity.class)
                            .putExtra("newsId", newsDetail.getId() + "")
                            .putExtra("videoUrl", newsDetail.getContentFile() + ""));
                }
                break;
            case R.id.tv_comments:
                if (Utils.getLoginToken(this).isEmpty())
                    Utils.setLoginPopup(this, preferences);
                else if (newsDetail != null)
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
                        "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.iv_like_click:
                if (Utils.getLoginToken(this).isEmpty())
                    Utils.setLoginPopup(this, preferences);
                else
                    callLikeApi();
                break;
            case R.id.iv_bookmark_click:
                if (Utils.getLoginToken(this).isEmpty())
                    Utils.setLoginPopup(this, preferences);
                else
                    callBookmarkApi();
                break;
        }
    }

    @Override
    public void OnItemClick(String type, int position) {
        switch (type) {
            case "videoClick":
                startActivity(new Intent(this, EntertainmentDetailActivity.class)
                        .putExtra("newsId", data.get(position).getId() + ""));
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

        IntentFilter filter = new IntentFilter();
        filter.addAction("updateComment");
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
                        if (!modelResult.getData().getIsLike().isEmpty()) {
                            if (modelResult.getData().getIsLike().equalsIgnoreCase("yes")) {
                                setLikeClick();
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
                                setLikeRemove();
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
                            if (newsDetail.getLikes() != null && !newsDetail.getLikes().toString().isEmpty())
                                tv_likes.setText(newsDetail.getLikes() + " people like this");
                            sendBroadcast(new Intent("UpdateLike"));
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
                                setBookmarkClick();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        newsDetail.setIsBookmark("yes");

                                    }
                                });
                            } else {
                                setBookmarkRemove();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        newsDetail.setIsBookmark("no");

                                    }
                                });
                            }
                sendBroadcast(new Intent("updateBookmarkList"));
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            } else
                Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void callSingleNews() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("id", newId);
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<SingleNewModel> couponsObservable
                            = s.callSingleNews(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessSingleNews, this::handleError);

    }

    private void OnSuccessSingleNews(SingleNewModel singleNewModel) {
        Utils.hideProgressDialog();
        if (singleNewModel != null && singleNewModel.getErrorCode() != null) {
            if (singleNewModel.getErrorCode().equalsIgnoreCase("200")) {
                if (singleNewModel.getData() != null && singleNewModel.getData().getId() != null) {
                    newsDetail = singleNewModel.getData();
                    setData();
                }
            } else if (singleNewModel.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            } else
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    private void setData() {
        if (newsDetail != null) {
            if (newsDetail.getType().equalsIgnoreCase("video")) {
                iv_play.setVisibility(View.VISIBLE);
            } else
                iv_play.setVisibility(View.GONE);

            if (newsDetail.getId() != null)
                newId = newsDetail.getId().toString();

            if (newsDetail.getTitle() != null && !newsDetail.getTitle().isEmpty())
                tv_heading.setText(newsDetail.getTitle() + "");

            if (newsDetail.getSubTitle() != null && !newsDetail.getSubTitle().isEmpty())
                tv_heading1.setText(newsDetail.getSubTitle() + "");

            if (newsDetail.getDescription() != null && !newsDetail.getDescription().isEmpty())
                tv_detail.setText(Html.fromHtml(newsDetail.getDescription() + ""));

            if (newsDetail.getLikes() != null && !newsDetail.getLikes().toString().isEmpty())
                tv_likes.setText(newsDetail.getLikes() + " people like this");

            if (newsDetail.getComments() != null && !newsDetail.getComments().toString().isEmpty())
                if (newsDetail.getComments() == 0)
                    ((TextView) findViewById(R.id.tv_comments)).setText(" Comments");
                else
                    ((TextView) findViewById(R.id.tv_comments)).setText(newsDetail.getComments() + " Comments");

            if (newsDetail.getCreatedAt() != null && !newsDetail.getCreatedAt().isEmpty())
                tv_time.setText(Utils.changeDateFormat(newsDetail.getCreatedAt(), "yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy / h:mm a") + "");

            if (newsDetail.getCategory() != null && !newsDetail.getCategory().isEmpty())
                tv_entertainment.setText(newsDetail.getCategory());
            else if (newsDetail.getCategoryName() != null && !newsDetail.getCategoryName().isEmpty())
                tv_entertainment.setText(newsDetail.getCategoryName());

            if (newsDetail.getIsLike() != null)
                if (newsDetail.getIsLike().equalsIgnoreCase("yes"))
                    setLikeClick();
                else
                    setLikeRemove();

            if (newsDetail.getIsBookmark() != null)
                if (newsDetail.getIsBookmark().equalsIgnoreCase("yes"))
                    setBookmarkClick();
                else
                    setBookmarkRemove();

//                            if (newsDetail.getCoverImage() != null && !newsDetail.getCoverImage().isEmpty())
//                                if (newsDetail.getCoverImage().startsWith("http"))
//                                    Picasso.with(this).load(newsDetail.getCoverImage()).into(iv_mainImage);
//                                else
//                                    Picasso.with(this).load(Constants.BASE_URl + "/" + newsDetail.getCoverImage()).into(iv_mainImage);


            String url = "";
            if (newsDetail.getCoverImage() != null && !newsDetail.getCoverImage().isEmpty())
                if (newsDetail.getCoverImage().startsWith("http"))
                    url = newsDetail.getCoverImage();
                else
                    url = Constants.BASE_URl + "/" + newsDetail.getCoverImage();


            File outputFile = new File(Constants.image_dir_path
                    + "/" + newsDetail.getId() + "_" + Utils.getFileName(url));
            if (outputFile != null)
                if (outputFile.exists())
                    Picasso.with(this).load(outputFile).into(iv_mainImage);
                else
                    Picasso.with(this).load(url).into(iv_mainImage);
            else
                Picasso.with(this).load(url).into(iv_mainImage);
        }
    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}