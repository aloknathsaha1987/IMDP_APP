package com.exploreca.imdb.movieCategory;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.exploreca.imdb.MovieDetailActivity;
import com.exploreca.imdb.R;
import com.exploreca.imdb.adapter.MovieAdapter;
import com.exploreca.imdb.httpManager.HttpManager;
import com.exploreca.imdb.model.Movie;
import com.exploreca.imdb.parser.MovieJSONParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALOKNATH on 1/29/2015.
 */
public class GenreSelected extends ListActivity {

    ProgressBar pb;
    List<MyTask> tasks;
    List<Movie> movieList = new ArrayList<>();
    public static final String API_VERSION = "3";
    private String context_path;
    public static final String API_KEY = "f47dd4de64c6ef630c2b0d50a087cc33";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);
        tasks = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();

        context_path = "genre/" + bundle.getInt("genreID") + "/movies";

        if(movieList.isEmpty()){
            if (isOnline()) {
                requestData("http://api.themoviedb.org/");
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }else{
            updateDisplay();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        MenuItem item = menu.findItem(R.id.myFavourite);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        SubMenu subGenre = menu.addSubMenu("Genre");
        subGenre.add(1, 1, 0, "Action");
        subGenre.add(1, 2, 0, "Adventure");
        subGenre.add(1, 3, 0, "Animation");
        subGenre.add(1, 4, 0, "Comedy");
        subGenre.add(1, 5, 0, "Crime");
        subGenre.add(1, 6, 0, "Documentary");
        subGenre.add(1, 7, 0, "Drama");
        subGenre.add(1, 8, 0, "Family");
        subGenre.add(1, 9, 0, "Fantasy");
        subGenre.add(1, 10, 0, "Foreign");
        subGenre.add(1, 11, 0, "History");
        subGenre.add(1, 12, 0, "Horror");
        subGenre.add(1, 13, 0, "Music");
        subGenre.add(1, 14, 0, "Mystery");
        subGenre.add(1, 15, 0, "Romance");
        subGenre.add(1, 16, 0, "Science Fiction");
        subGenre.add(1, 17, 0, "TV Movie");
        subGenre.add(1, 18, 0, "Thriller");
        subGenre.add(1, 19, 0, "War");
        subGenre.add(1, 20, 0, "Western");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){

            case R.id.menu_upcoming:
                intent = new Intent(this, UpcomingMovies.class);
                startActivity(intent);
                break;
            case R.id.menu_now_playing:
                intent = new Intent(this, NowPlayingMovies.class);
                startActivity(intent);
                break;
            case R.id.menu_top_rated:
                intent = new Intent(this, TopRatedMovies.class);
                startActivity(intent);
                break;

            case R.id.menu_popular:
                intent = new Intent(this, PopularMovies.class);
                startActivity(intent);
                break;

            case R.id.myFavourite:
                intent = new Intent(this, MyMovies.class);
                startActivity(intent);
                break;

            case 1:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Action"));
                startActivity(intent);
                break;

            case 2:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Adventure"));
                startActivity(intent);
                break;

            case 3:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Animation"));
                startActivity(intent);
                break;

            case 4:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Comedy"));
                startActivity(intent);
                break;


            case 5:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Crime"));
                startActivity(intent);
                break;


            case 6:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Documentary"));
                startActivity(intent);
                break;


            case 7:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Drama"));
                startActivity(intent);
                break;

            case 8:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Family"));
                startActivity(intent);
                break;


            case 9:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Fantasy"));
                startActivity(intent);
                break;


            case 10:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Foreign"));
                startActivity(intent);
                break;


            case 11:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("History"));
                startActivity(intent);
                break;


            case 12:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Horror"));
                startActivity(intent);
                break;


            case 13:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Music"));
                startActivity(intent);
                break;


            case 14:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Mystery"));
                startActivity(intent);
                break;


            case 15:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Romance"));
                startActivity(intent);
                break;


            case 16:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Science Fiction"));
                startActivity(intent);
                break;


            case 17:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("TV Movie"));
                startActivity(intent);
                break;


            case 18:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Thriller"));
                startActivity(intent);
                break;


            case 19:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("War"));
                startActivity(intent);
                break;


            case 20:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Western"));
                startActivity(intent);
                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
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
        MovieAdapter adapter = new MovieAdapter(this, R.layout.item_movie, movieList);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Movie movie;
        movie = movieList.get(position);
        // String value = String.valueOf(position);
        Log.i("PositionNo", movie.getPoster_path());
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(".model.Movie", movie);
        startActivity(intent);
    }

    private class MyTask extends AsyncTask<String, String, List<Movie>> {
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            String content = HttpManager.getData(params[0], API_VERSION, context_path, API_KEY);
            movieList = MovieJSONParser.parseFeedMovie(content);
            return movieList;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            tasks.remove(this);
            if(tasks.size() == 0){
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(GenreSelected.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            movieList = result;
            updateDisplay();
        }
    }
}



