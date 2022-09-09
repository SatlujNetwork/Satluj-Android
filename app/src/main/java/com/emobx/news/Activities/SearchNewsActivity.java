package com.emobx.news.Activities;

import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Adapters.SearchNewsAdapter;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.NewsListModel;
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.Model.SearchNewsData;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.DownloadFile;
import com.emobx.news.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class SearchNewsActivity extends BaseActivity {

    private NewsPreferences preferences;
    private EditText tv_title_left;
    private ArrayList<NewsListModelDatum> searchNewsData = new ArrayList<>();
    private SearchNewsAdapter searchNewsAdapter;
    private RecyclerView rv_news;
    private ApiInterface apiInterface;
    private boolean hasMore = false;
    private ImageView iv_cross;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_news);
        init();
        listener();

    }


    private void init() {
        preferences = new NewsPreferences(this);
        rv_news = findViewById(R.id.rv_news);
        tv_title_left = findViewById(R.id.tv_title_left);
        iv_cross = findViewById(R.id.iv_cross);
        setSearchAdapter();
    }

    private void setSearchAdapter() {

        if (searchNewsAdapter == null) {
            searchNewsAdapter = new SearchNewsAdapter(this, searchNewsData);
            rv_news.setLayoutManager(new LinearLayoutManager(this));
            rv_news.setAdapter(searchNewsAdapter);
        } else
            searchNewsAdapter.notifyDataSetChanged();

    }

    private void listener() {
        iv_cross.setOnClickListener(v -> {
            tv_title_left.setText("");
        });

        findViewById(R.id.iv_back).setOnClickListener(v -> {
            onBackPressed();
        });

        rv_news.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int position = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastVisibleItemPosition();
                if (hasMore && position == searchNewsData.size() - 1) {
                    hasMore = false;
                    callsearchNews(tv_title_left.getText().toString(), true);
                }
            }
        });

        tv_title_left.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    iv_cross.setVisibility(View.GONE);
                    rv_news.setVisibility(View.GONE);
                    findViewById(R.id.ll_search_news).setVisibility(View.VISIBLE);
                } else {
                    iv_cross.setVisibility(View.VISIBLE);
                    searchNewsData.clear();
                    hasMore = false;
                    setSearchAdapter();
                    rv_news.setVisibility(View.VISIBLE);
                    findViewById(R.id.ll_search_news).setVisibility(View.GONE);
                    callsearchNews(s.toString(), false);


                }

            }
        });
    }

    private void callsearchNews(String key, boolean show) {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        if (show)
            Utils.showProgressDialog(this);
        if (apiInterface == null)
            apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("search_key", key);
        map.put("post_value", searchNewsData.size() + "");

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<NewsListModel> couponsObservable
                            = s.searchNews(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessCategoryList, this::handleError);

    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void OnSuccessCategoryList(NewsListModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                searchNewsData.addAll(modelResult.getData());
                hasMore = modelResult.getHasMore();
                setSearchAdapter();
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            Utils.clearLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(this.getResources().getColor(R.color.black));
            findViewById(R.id.rl_top_view).setBackgroundColor(this.getResources().getColor(R.color.black));
            ((EditText) findViewById(R.id.tv_title_left)).setTextColor(this.getResources().getColor(R.color.appBlackxLight));
            ((EditText) findViewById(R.id.tv_title_left)).setBackgroundColor(this.getResources().getColor(R.color.black));
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_white));
            ((ImageView) findViewById(R.id.iv_cross)).setImageDrawable(getResources().getDrawable(R.drawable.cross_white));
            ((ImageView) findViewById(R.id.iv_search)).setImageDrawable(getResources().getDrawable(R.drawable.placeholder_search));
        } else {
            Utils.setLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(this.getResources().getColor(R.color.white));
            findViewById(R.id.rl_top_view).setBackgroundColor(this.getResources().getColor(R.color.white));
            ((EditText) findViewById(R.id.tv_title_left)).setTextColor(this.getResources().getColor(R.color.appBlackDark));
            ((EditText) findViewById(R.id.tv_title_left)).setBackgroundColor(this.getResources().getColor(R.color.white));
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_black));
            ((ImageView) findViewById(R.id.iv_cross)).setImageDrawable(getResources().getDrawable(R.drawable.cross));
            ((ImageView) findViewById(R.id.iv_search)).setImageDrawable(getResources().getDrawable(R.drawable.large_search));
        }
    }


}