package com.exploreca.imdb;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by ALOKNATH on 1/20/2015.x
 */
public class MovieTrailer extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_trailer);
        VideoView videoView =(VideoView)findViewById(R.id.movie_trailer);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.mickey;
        videoView.setVideoURI(Uri.parse(path));
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();
    }
}
