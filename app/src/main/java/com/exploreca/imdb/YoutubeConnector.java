package com.exploreca.imdb;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALOKNATH on 2/3/2015.
 */
public class YoutubeConnector {
    private YouTube youtube;
    private YouTube.Search.List query;

    // Your developer key goes here
    public static final String KEY
            = "AIzaSyAT3jwS3RTIFrfCpbLG7lo_V1SFcpAv-20";

    public YoutubeConnector(Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest hr) throws IOException {}
        }).setApplicationName(context.getString(R.string.app_name)).build();

        try{
            query = youtube.search().list("id,snippet");
            query.setKey(KEY);
            query.setType("video");
            query.setFields("items(id/videoId)");
        }catch(IOException e){
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    public List<String> search(String keywords){
        query.setQ(keywords);
        try{
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();

            List<String> items = new ArrayList<>();
            for(SearchResult result:results){
                items.add(result.getId().getVideoId());
            }
            return items;
        }catch(IOException e){
            Log.d("YC", "Could not search: " + e);
            return null;
        }
    }
}