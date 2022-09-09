package com.emobx.news;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubeLiveVideoActivity extends YouTubeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Dialog);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_live_video);

//        PictureInPictureParams pictureInPictureParams = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            pictureInPictureParams = new PictureInPictureParams.Builder().build();
//            enterPictureInPictureMode(pictureInPictureParams);
//        }


    }

//    @Override
//    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
//        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
//
//        if (!isInPictureInPictureMode) {
//            sendBroadcast(new Intent("updateLiveVideoStatus"));
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent().hasExtra("videoID")) {

            YouTubePlayerView ytPlayer = (YouTubePlayerView) findViewById(R.id.youTubePlayerView);

            ytPlayer.initialize(null
                    /*getResources().getString(R.string.google_api_key)*/,
                    new YouTubePlayer.OnInitializedListener() {
                        // Implement two methods by clicking on red
                        // error bulb inside onInitializationSuccess
                        // method add the video link or the playlist
                        // link that you want to play In here we
                        // also handle the play and pause
                        // functionality
                        @Override
                        public void onInitializationSuccess(
                                YouTubePlayer.Provider provider,
                                YouTubePlayer youTubePlayer, boolean b) {
                            youTubePlayer.loadVideo(getIntent().getStringExtra("videoID"));
                            youTubePlayer.play();

                            youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);

                        }

                        // Inside onInitializationFailure
                        // implement the failure functionality
                        // Here we will show toast
                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult
                                                                    youTubeInitializationResult) {
                            Toast.makeText(getApplicationContext(), "Video player Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}