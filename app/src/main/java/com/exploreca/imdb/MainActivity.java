package com.exploreca.imdb;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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

import static com.exploreca.imdb.R.raw.startup;

public class MainActivity extends Activity {

    public static final String PHOTOS_BASE_URL = "http://d3gtl9l2a4fn1j.cloudfront.net/t/p/w500";
    Intent intent;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_page);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.raw.startup);
        ImageView image = (ImageView)findViewById(R.id.imageView);
        image.setImageBitmap(bitmap);
        intent = new Intent(this, NowPlayingMovies.class);
        startActivity(intent);
	}
}
