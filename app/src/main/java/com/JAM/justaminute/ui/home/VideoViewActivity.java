package com.JAM.justaminute.ui.home;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.JAM.justaminute.R;

public class VideoViewActivity extends AppCompatActivity {
    MediaController mediaController;
    VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        video=findViewById(R.id.video);
        Bundle bundle=getIntent().getExtras();
        String src=bundle.getString("name");
        if(bundle!=null)
        {
            video.setVideoPath(src);
            mediaController=new MediaController(this);
            video.setMediaController(mediaController);
            mediaController.setAnchorView(video);
            video.start();
        }


    }
}
