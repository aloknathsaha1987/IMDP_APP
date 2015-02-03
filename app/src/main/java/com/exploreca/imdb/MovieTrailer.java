package com.exploreca.imdb;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.exploreca.imdb.model.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import org.apache.http.HttpRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ALOKNATH on 1/20/2015.x
 */
public class MovieTrailer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {


private YouTubePlayerView ytpv;
    private YouTubePlayer ytp;
    private Movie movie;
    static private final String VIDEO = "4SK0cUNMnMM";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_trailer);
        String developerKey = getResources().getString(R.string.api_key);
        ytpv = (YouTubePlayerView) findViewById(R.id.youtubeplayer);
        ytpv.initialize(developerKey, this);
        Bundle bundle = getIntent().getExtras();
        movie = (Movie)bundle.getParcelable("Movie_Name");
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        ytp = youTubePlayer;
        if(ytp !=null){

            for (String videoId: MovieDetailActivity.videoIds){
                ytp.loadVideo(videoId);
                break;
            }
//            if(videoIds.isEmpty()){
//                Toast.makeText(this,"VideoIds is Empty", Toast.LENGTH_LONG).show();
//            }else{
//                for (String videoId: videoIds){
//                    ytp.loadVideo(videoId);
//                    break;
//                }
//            }
        }
        Toast.makeText(this, movie.getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Initialization Fail", Toast.LENGTH_LONG).show();
    }

    private String extractYoutubeId(String url) {

        String video_id = "";
        if (url != null && url.trim().length() > 0 && url.startsWith("http")) {

            String expression = "^.*((youtu.be" + "\\/)"
                    + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";
            CharSequence input = url;
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

}
