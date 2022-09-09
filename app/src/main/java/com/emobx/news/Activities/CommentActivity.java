package com.emobx.news.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Adapters.CommentAdapter;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.AddComment;
import com.emobx.news.Model.CommentModel;
import com.emobx.news.Model.LogoutModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CommentActivity extends BaseActivity implements View.OnClickListener, AdapterItemClickListner {
    private RecyclerView rv_comments;
    private CommentAdapter commentAdapter;
    private NewsPreferences preferences;
    private EditText et_comment;
    private ApiInterface apiInterface;
    private ArrayList<CommentModel.Datum> commentList = new ArrayList<>();
    private String newId;
    private int commentPos = -1;
    private ImageView iv_send;
    private SwipeRefreshLayout swipeRefresh;
    private boolean isFirstTime = true;
    private boolean hasMore = false;
    private String commentListView = "insert";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        init();
        listener();
        getIntentData();
    }

    private void getIntentData() {
        if (getIntent().hasExtra("newsId")) {
            newId = getIntent().getStringExtra("newsId");
            callCommentApi();
        }
    }

    private void init() {
        preferences = new NewsPreferences(this);

        findViewById(R.id.tv_title_left).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title_left)).setText("Comments");
        findViewById(R.id.iv_back).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_refresh).setVisibility(View.VISIBLE);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        et_comment = findViewById(R.id.et_comment);

        rv_comments = findViewById(R.id.rv_comments);
        iv_send = findViewById(R.id.iv_send);
    }

    private void listener() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_refresh).setOnClickListener(this);
        iv_send.setOnClickListener(this);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                if (hasMore) {
                    hasMore = false;
                    commentListView = "loadMore";
                    callCommentApi();
                }
            }
        });

    }

    private void setAdapter() {
        if (commentAdapter == null) {
            commentAdapter = new CommentAdapter(this, commentList, this);
            rv_comments.setLayoutManager(new LinearLayoutManager(this));
            rv_comments.setAdapter(commentAdapter);
        } else
            commentAdapter.notifyDataSetChanged();

        switch (commentListView) {
            case "loadMore":
                if (commentList.size() > 0)
                    ((LinearLayoutManager) rv_comments.getLayoutManager()).scrollToPositionWithOffset((commentList.size() % 20) - 1, 0);
                break;
            case "insert":
                if (commentList.size() > 0)
                    rv_comments.scrollToPosition(commentList.size() - 1);
                break;
            case "delete":
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_send:
                if (!et_comment.getText().toString().isEmpty())
                    addComment();
                break;
            case R.id.iv_refresh:
                commentListView = "insert";
                callCommentApi();
                break;
        }
    }

    private void addComment() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        if (apiInterface == null)
            apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        iv_send.setVisibility(View.GONE);
        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("news_id", newId);
        map.put("comment", et_comment.getText().toString());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<AddComment> couponsObservable
                            = s.addComment(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessCommentAdded, this::handleError);

    }

    private void OnSuccessCommentAdded(AddComment response) {
        iv_send.setVisibility(View.VISIBLE);
        if (response.getError_code().equalsIgnoreCase("200")) {
            commentList.add(response.getData());
            rv_comments.smoothScrollToPosition(commentList.size() - 1);
            et_comment.setText("");
            commentListView = "insert";
            setAdapter();
            sendBroadcast(new Intent("updateComment"));
        }
    }

    @Override
    public void OnItemClick(String type, int i) {
        switch (type) {
            case "commentClick":
                if (commentList.get(i).getGetUser() != null)
                    if (commentList.get(i).getGetUser().getId() != null)
                        if (commentList.get(i).getGetUser().getId().toString()
                                .equalsIgnoreCase(preferences.getUserData().getData().getId().toString())) {
                            commentPos = i;
                            setCommentPopup(commentList.get(i).getId() + "", "");
                        } else {
                            commentPos = i;
                            setCommentPopup(commentList.get(i).getId() + "", "otherComment");
                        }
                break;
        }
    }

    public void setCommentPopup(String commentId, String commentType) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.comment_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        dialog.show();

        if (commentType.isEmpty())
            dialog.findViewById(R.id.rl_delete).setVisibility(View.VISIBLE);
        else
            dialog.findViewById(R.id.rl_delete).setVisibility(View.GONE);


        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            dialog.findViewById(R.id.rl_popup).setBackgroundColor(getResources().getColor(R.color.black));
            ((TextView) dialog.findViewById(R.id.tv_flag)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) dialog.findViewById(R.id.tv_report)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) dialog.findViewById(R.id.tv_delete)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((ImageView) dialog.findViewById(R.id.iv_flag)).setImageDrawable(getResources().getDrawable(R.drawable.flag_white));
            ((ImageView) dialog.findViewById(R.id.iv_report)).setImageDrawable(getResources().getDrawable(R.drawable.report_white));
            ((ImageView) dialog.findViewById(R.id.iv_delete)).setImageDrawable(getResources().getDrawable(R.drawable.delete_white));

        } else {
            dialog.findViewById(R.id.rl_popup).setBackgroundColor(getResources().getColor(R.color.white));
            ((TextView) dialog.findViewById(R.id.tv_flag)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) dialog.findViewById(R.id.tv_report)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) dialog.findViewById(R.id.tv_delete)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((ImageView) dialog.findViewById(R.id.iv_flag)).setImageDrawable(getResources().getDrawable(R.drawable.flag));
            ((ImageView) dialog.findViewById(R.id.iv_report)).setImageDrawable(getResources().getDrawable(R.drawable.report));
            ((ImageView) dialog.findViewById(R.id.iv_delete)).setImageDrawable(getResources().getDrawable(R.drawable.delete));
        }

        dialog.findViewById(R.id.rl_flag).setOnClickListener(v -> {
            dialog.dismiss();
            callFlagApi(commentId);
        });

        dialog.findViewById(R.id.rl_report).setOnClickListener(v -> {
            dialog.dismiss();
            callReportApi(commentId);
        });

        dialog.findViewById(R.id.rl_delete).setOnClickListener(v -> {
            dialog.dismiss();
            callDeleteApi(commentId);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            Utils.clearLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(getResources().getColor(R.color.black));
            findViewById(R.id.rl_top_view).setBackgroundColor(getResources().getColor(R.color.black));
            ((TextView) findViewById(R.id.tv_title_left)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            findViewById(R.id.rl_bottom_view).setBackgroundColor(getResources().getColor(R.color.black));
            et_comment.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.appBlackDark));
            et_comment.setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_white));
            ((ImageView) findViewById(R.id.iv_refresh)).setImageDrawable(getResources().getDrawable(R.drawable.refresh_white));
            ((ImageView) findViewById(R.id.iv_send)).setImageDrawable(getResources().getDrawable(R.drawable.send_white));
        } else {
            Utils.setLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(getResources().getColor(R.color.white));
            findViewById(R.id.rl_top_view).setBackgroundColor(getResources().getColor(R.color.white));
            ((TextView) findViewById(R.id.tv_title_left)).setTextColor(getResources().getColor(R.color.appBlackDark));
            findViewById(R.id.rl_bottom_view).setBackgroundColor(getResources().getColor(R.color.white));
            et_comment.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.grey_bg_color));
            et_comment.setTextColor(getResources().getColor(R.color.appBlackDark));
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_black));
            ((ImageView) findViewById(R.id.iv_refresh)).setImageDrawable(getResources().getDrawable(R.drawable.refresh));
            ((ImageView) findViewById(R.id.iv_send)).setImageDrawable(getResources().getDrawable(R.drawable.send));
        }
    }

    private void callCommentApi() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("news_id", newId);
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());
        map.put("post_value", commentList.size() + "");

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<CommentModel> couponsObservable
                            = s.getComment(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessComment, this::handleError);

    }

    private void OnSuccessComment(CommentModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                hasMore = modelResult.isHasMore();
                setCommentData(modelResult);
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            }
        }
    }

    private void setCommentData(CommentModel modelResult) {
        if (isFirstTime) {
            commentList.addAll(commentList.size(), modelResult.getData());
            Collections.reverse(commentList);
        } else {
            Collections.reverse(modelResult.getData());
            commentList.addAll(0, modelResult.getData());

        }
        setAdapter();
        if (isFirstTime && commentList.size() > 0) {
            isFirstTime = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rv_comments.smoothScrollToPosition(commentList.size());
                }
            }, 1000);
        }
    }

    private void callFlagApi(String commentId) {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("id", commentId);
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<LogoutModel> couponsObservable
                            = s.callFlagComment(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessFlag, this::handleError);

    }

    private void OnSuccessFlag(LogoutModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                Toast.makeText(this, "Comment flag added successfully.", Toast.LENGTH_SHORT).show();
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            }/* else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
       */
        } /*else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    */
    }

    private void callReportApi(String commentId) {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("id", commentId);
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<LogoutModel> couponsObservable
                            = s.callReportComment(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessReport, this::handleError);

    }

    private void OnSuccessReport(LogoutModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                Toast.makeText(this, "Comment report added successfully.", Toast.LENGTH_SHORT).show();
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            }/* else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
       */
        } /*else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    */
    }

    private void callDeleteApi(String commentId) {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("id", commentId);
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<LogoutModel> couponsObservable
                            = s.callDeleteComment(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessDelete, this::handleError);

    }

    private void OnSuccessDelete(LogoutModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                commentList.remove(commentPos);
                commentListView = "delete";
                setAdapter();
                sendBroadcast(new Intent("updateComment"));
                Toast.makeText(this, "Comment successfully deleted.", Toast.LENGTH_SHORT).show();
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            }/* else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
       */
        } /*else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    */
    }

    private void handleError(Throwable throwable) {
        iv_send.setVisibility(View.VISIBLE);
        Utils.hideProgressDialog();
        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

}