package com.exploreca.imdb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exploreca.imdb.movieCategory.MyMovies;
import com.exploreca.imdb.movieCategory.NowPlayingMovies;
import com.exploreca.imdb.movieCategory.PopularMovies;
import com.exploreca.imdb.movieCategory.TopRatedMovies;
import com.exploreca.imdb.movieCategory.UpcomingMovies;
import com.exploreca.imdb.adapter.MovieAdapter;
import com.exploreca.imdb.db.MoviesDataSource;
import com.exploreca.imdb.httpManager.HttpManager;
import com.exploreca.imdb.model.Movie;
import com.exploreca.imdb.parser.MovieJSONParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by ALOKNATH on 1/15/2015.
 */
public class MovieDetailActivity extends Activity {

    private Movie movie;
    private MoviesDataSource dataSource;
    private Button trailer_button;
    private String imageUrl;
    private Bitmap bitmapMovie;
    private String movieDescription;
   // private List<Movie> movies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_detail);

        Bundle bundle = getIntent().getExtras();

        movie = (Movie)bundle.getParcelable(".model.Movie");
        //Intent intent = getIntent();
        //movie.setBitmap((Bitmap) intent.getParcelableExtra("BitmapImage"));
        //Log.i("Movie_Poster", movie.getPoster_path());

        String imageU = movie.getPoster_path();

        if((imageU.isEmpty() && imageU == null) || imageU.equals("null"))
        {
            imageUrl = MovieAdapter.DEFAULT_IMAGE;
        }else{
            imageUrl = MainActivity.PHOTOS_BASE_URL + movie.getPoster_path();
        }
        String ContentUrl = "http://api.themoviedb.org/3/movie/"+ movie.getId() + "?api_key=f47dd4de64c6ef630c2b0d50a087cc33";
        ImageLoad imageLoad = new ImageLoad();
        imageLoad.execute(imageUrl, ContentUrl);

        TextView tv = (TextView) findViewById(R.id.movie_title);
        //Log.i("Movie Object", movie.getOriginal_title());
        tv.setText(movie.getTitle());

        tv = (TextView)findViewById(R.id.release_date);
        tv.setText(movie.getRelease_date());

        /*
        {"adult":false,"backdrop_path":"/bPG6DTTcvNVVD8tZif8GhvO67ab.jpg","id":253331,"original_title":"Black or White","release_date":"2015-01-30","poster_path":"/zAm9tSR92QsQFbghMaoj1lsCAAr.jpg","popularity":0.502964881512297,"title":"Black or White","video":false,"vote_average":0.0,"vote_count":0}
         */
        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        ratingBar.setStepSize((float)0.25);
        ratingBar.setRating((float)movie.getPopularity());

        trailer_button = (Button)findViewById(R.id.button_trailer);
        trailer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieDetailActivity.this, MovieTrailer.class);
                intent.putExtra("Movie_Name", movie);
                startActivity(intent);
            }
        });

        dataSource = new MoviesDataSource(this);
        dataSource.open();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favourites, menu);
        MenuItem item = menu.findItem(R.id.addFavourite);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()){

            case R.id.share:
                Uri imageUri = getImageUri(getApplicationContext(), bitmapMovie);
                String movieToShare = movie.getOriginal_title() + ": This Movie is Awesome !! It's description: " + movieDescription ;
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.putExtra(Intent.EXTRA_TITLE, movieToShare);
                startActivity(Intent.createChooser(shareIntent, "Share Via"));
                break;

            case R.id.myFavourite:
                intent = new Intent(this, MyMovies.class);
                startActivity(intent);
                break;

            case R.id.addFavourite:
                if(dataSource.addToMyMovies(movie)) {
                    Toast.makeText(this, "Movie Added to My Favourites", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(this, "Movie Not Added to the DataBase", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
	protected void onResume() {
		super.onResume();
		dataSource.open();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dataSource.close();
	}

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private class MovieDetailContainer{
        public Bitmap bitmapMovie;
        public String movieDetailContainer;

        public Bitmap getBitmapMovie() {
            return bitmapMovie;
        }

        public void setBitmapMovie(Bitmap bitmapMovie) {
            this.bitmapMovie = bitmapMovie;
        }

        public String getMovieDetailContainer() {
            return movieDetailContainer;
        }

        public void setMovieDetailContainer(String movieDetailContainer) {
            this.movieDetailContainer = movieDetailContainer;
        }
    }

    private class ImageLoad extends AsyncTask<String, Void, MovieDetailContainer>{
        @Override
        protected MovieDetailContainer doInBackground(String... urls) {
            InputStream in = null;
            try {
                in = (InputStream) new URL(urls[0]).getContent();
                String content = HttpManager.getData(urls[1]);
                String movieDetail = MovieJSONParser.parseMovieDetail(content);
                MovieDetailContainer container = new MovieDetailContainer();
                container.setBitmapMovie( BitmapFactory.decodeStream(in));
                container.setMovieDetailContainer(movieDetail);
                in.close();
                return container;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieDetailContainer container) {

            ImageView image = (ImageView)findViewById(R.id.movie_poster);
            image.setImageBitmap(container.getBitmapMovie());
            bitmapMovie = container.getBitmapMovie();
            TextView textView = (TextView)findViewById(R.id.movie_description);
            textView.setText(container.getMovieDetailContainer());
            movieDescription = container.getMovieDetailContainer();
        }
    }

}
