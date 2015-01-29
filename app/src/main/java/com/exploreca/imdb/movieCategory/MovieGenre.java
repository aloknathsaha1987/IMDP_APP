package com.exploreca.imdb.movieCategory;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.exploreca.imdb.R;
import com.exploreca.imdb.adapter.GenreAdapter;
import com.exploreca.imdb.httpManager.HttpManager;
import com.exploreca.imdb.model.Genre;
import com.exploreca.imdb.parser.MovieJSONParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALOKNATH on 1/29/2015.
 */
public class MovieGenre extends ListActivity {

    ProgressBar pb;
    List<MyTask> tasks;
    List<Genre> genreList = new ArrayList<>();
    public static final String API_VERSION = "3";
    public static final String CONTEXT_PATH = "genre/list";
    public static final String API_KEY = "f47dd4de64c6ef630c2b0d50a087cc33";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.genre);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);
        tasks = new ArrayList<>();

        if(genreList.isEmpty()){
            if (isOnline()) {
                requestData("http://api.themoviedb.org/");
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }else{
            updateDisplay();
        }
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    public void updateDisplay(){
        GenreAdapter adapter = new GenreAdapter(this, R.layout.item_movie, genreList);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Genre genre = genreList.get(position);
        Intent intent = new Intent(this, GenreSelected.class);
        intent.putExtra("genreID", genre.getId());
        startActivity(intent);
    }

    private class MyTask extends AsyncTask<String, String, List<Genre>>{
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Genre> doInBackground(String... params) {
            String content = HttpManager.getData(params[0], API_VERSION, CONTEXT_PATH, API_KEY);
            genreList = MovieJSONParser.parseGenreMovie(content);
            return genreList;
        }

        @Override
        protected void onPostExecute(List<Genre> result) {
            tasks.remove(this);
            if(tasks.size() == 0){
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MovieGenre.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            genreList = result;
            updateDisplay();
        }
    }
}
