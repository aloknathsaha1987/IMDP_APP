package com.exploreca.imdb;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.exploreca.imdb.httpManager.HttpManager;
import com.exploreca.imdb.model.Genre;
import com.exploreca.imdb.movieCategory.MyMovies;
import com.exploreca.imdb.movieCategory.NowPlayingMovies;
import com.exploreca.imdb.movieCategory.PopularMovies;
import com.exploreca.imdb.movieCategory.TopRatedMovies;
import com.exploreca.imdb.movieCategory.UpcomingMovies;
import com.exploreca.imdb.parser.MovieJSONParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

	public static final String USERNAME="pref_username";
	public static final String VIEWIMAGE="pref_viewimages";
    public static final String PHOTOS_BASE_URL = "http://d3gtl9l2a4fn1j.cloudfront.net/t/p/w500";
    private List<String> genreListToPass = new ArrayList<>();
    List<Genre> genreList = new ArrayList<>();

    Intent intent;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, NowPlayingMovies.class);
        //intent.putStringArrayListExtra("GenreValues", (ArrayList<String>) genreListToPass);
        startActivity(intent);

	}

  @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menu_upcoming:
                intent = new Intent(this, UpcomingMovies.class);
                startActivity(intent);
                break;
            case R.id.menu_now_playing:

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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
