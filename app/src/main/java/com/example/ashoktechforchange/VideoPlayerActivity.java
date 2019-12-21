package com.example.ashoktechforchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.example.ashoktechforchange.R;

public class VideoPlayerActivity extends AppCompatActivity {

    ProgressBar loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        loader = findViewById(R.id.loader_buffer);

        Intent intent = getIntent();
        String videoURL = intent.getStringExtra("videoURL");

        VideoView videoView =(VideoView)findViewById(R.id.vpa_videoview);
        //Set MediaController  to enable play, pause, forward, etc options.
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);
        //Location of Media File
        Uri uri = Uri.parse(videoURL);
        //Starting VideView By Setting MediaController and URI
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        loader.setVisibility(View.VISIBLE);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                loader.setVisibility(View.INVISIBLE);
            }
        });
    }
}
