package com.emobx.news.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.Model.WebSeriesDetailModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import io.realm.Realm;

public class PlayerActivity extends BaseActivity implements View.OnClickListener {

    private String newsId;
    private Realm realm;
    private NewsListModelDatum newsDetail;
    private String videoUrl = "";
    private StyledPlayerView videoPlayer;
    private SimpleExoPlayer simpleExoPlayer;
    private ImageView iv_back;
    private ApiInterface apiInterface;
    private NewsPreferences preferences;
    private String episodeId = "", seriesId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        init();
        listener();
        getIntentData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);
    }

    private void init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferences = new NewsPreferences(this);
        realm = Utils.getRealmInstance(this);

        videoPlayer = findViewById(R.id.videoPlayer);
        iv_back = findViewById(R.id.iv_back);
    }

    private void listener() {
        iv_back.setOnClickListener(this);
    }

    private void getIntentData() {
        /*if (getIntent().hasExtra("newsId")) {
            newsId = getIntent().getStringExtra("newsId");

            RealmResults<CategoryWiseNewsListModel> data = realm.where(CategoryWiseNewsListModel.class).equalTo("id", 1).findAll();

            if (data != null)
                if (data.size() != 0)
                    if (data.get(0).getData() != null)
                        for (int i = 0; i < data.get(0).getData().size(); i++) {
                            if (data.get(0).getData().get(i).getId().toString()
                                    .equalsIgnoreCase(newsId)) {
                                newsDetail = data.get(0).getData().get(i);

                                if (newsDetail.getType() != null && newsDetail.getType().equalsIgnoreCase("video"))
                                    if (newsDetail.getContentFile() != null) {
                                        videoUrl = newsDetail.getContentFile();

                                        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
                                        videoPlayer.setPlayer(simpleExoPlayer);
                                        videoPlayer.setControllerVisibilityListener(new StyledPlayerControlView.VisibilityListener() {
                                            @Override
                                            public void onVisibilityChange(int visibility) {
                                                if (visibility == 0) {
                                                    iv_back.setVisibility(View.VISIBLE);
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                                } else {
                                                    iv_back.setVisibility(View.GONE);
                                                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                                }
                                            }
                                        });
                                        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
                                        simpleExoPlayer.addMediaItem(mediaItem);
                                        simpleExoPlayer.prepare();
                                        simpleExoPlayer.setPlayWhenReady(true);
                                    }
                            }
                        }

        }*/

        if (getIntent().hasExtra("videoUrl")) {
            videoUrl = getIntent().getStringExtra("videoUrl");

            if (!videoUrl.startsWith("http")) {
                videoUrl = Constants.BASE_URl + "/" + videoUrl;
            }

            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
            videoPlayer.setPlayer(simpleExoPlayer);
            videoPlayer.setControllerVisibilityListener(new StyledPlayerControlView.VisibilityListener() {
                @Override
                public void onVisibilityChange(int visibility) {
                    if (visibility == 0) {
                        iv_back.setVisibility(View.VISIBLE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    } else {
                        iv_back.setVisibility(View.GONE);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    }
                }
            });
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            simpleExoPlayer.addMediaItem(mediaItem);
            simpleExoPlayer.prepare();
            simpleExoPlayer.setPlayWhenReady(true);

        } else if (getIntent().hasExtra("webSeriesEpisodeDetail") && getIntent().hasExtra("episodeId") && getIntent().hasExtra("seriesId")) {
            episodeId = getIntent().getStringExtra("episodeId");
            seriesId = getIntent().getStringExtra("seriesId");

            WebSeriesDetailModel.Data.LastWatch episodeData = (WebSeriesDetailModel.Data.LastWatch) getIntent().getSerializableExtra("webSeriesEpisodeDetail");
            if (episodeData != null && episodeData.getVideoUrl() != null
                    && !episodeData.getVideoUrl().isEmpty()) {

                if (!episodeData.getVideoUrl().startsWith("http")) {
                    videoUrl = Constants.BASE_URl + "/" + episodeData.getVideoUrl();
                } else {
                    videoUrl = episodeData.getVideoUrl();
                }

                simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
                videoPlayer.setPlayer(simpleExoPlayer);
                videoPlayer.setControllerVisibilityListener(new StyledPlayerControlView.VisibilityListener() {
                    @Override
                    public void onVisibilityChange(int visibility) {
                        if (visibility == 0) {
                            iv_back.setVisibility(View.VISIBLE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        } else {
                            iv_back.setVisibility(View.GONE);
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        }
                    }
                });
                MediaItem mediaItem = MediaItem.fromUri(videoUrl);
                simpleExoPlayer.addMediaItem(mediaItem);
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(true);

                if (episodeData.getGetUserEpisode() != null
                        && episodeData.getGetUserEpisode().getResumeAt() != null
                        && episodeData.getGetUserEpisode().getResumeAt() < episodeData.getGetUserEpisode().getVideoTotalTime())
                    simpleExoPlayer.seekTo(episodeData.getGetUserEpisode().getResumeAt());
                else
                    simpleExoPlayer.seekTo(0);

            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent("updateWebSeriesDetailScreen")
                .putExtra("resumeTime", simpleExoPlayer.getCurrentPosition() + "")
                .putExtra("totalTime", simpleExoPlayer.getDuration() + "")
                .putExtra("episodeId", episodeId);
        sendBroadcast(intent);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (simpleExoPlayer != null)
            simpleExoPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null)
            simpleExoPlayer.stop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (simpleExoPlayer != null)
                    if (simpleExoPlayer.isPlaying()) {
                        Toast.makeText(this, "Video Paused", Toast.LENGTH_SHORT).show();
                        simpleExoPlayer.pause();
                    } else {
                        Toast.makeText(this, "Video Played", Toast.LENGTH_SHORT).show();
                        simpleExoPlayer.play();
                    }

                return true;

            case KeyEvent.KEYCODE_MEDIA_NEXT:
                if (simpleExoPlayer != null)
                    simpleExoPlayer.seekToNext();
                return true;

            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                if (simpleExoPlayer != null)
                    simpleExoPlayer.seekToPrevious();
                return true;
        }
        return false;
    }
}