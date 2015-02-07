package com.exploreca.imdb;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
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
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by ALOKNATH on 1/15/2015.
 */
public class MovieDetailActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    private Movie movie;
    private MoviesDataSource dataSource;
    private Button trailer_button;
    private String imageUrl;
    private Bitmap bitmapMovie;
    private static String movieDescription;
    private UiLifecycleHelper uiLifecycleHelper;
    public static List<String> videoIds;
    boolean mShowMap;

    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    private static final float DEFAULTZOOM = 15;

    LocationClient mLocationClient;
    GoogleMap mMap;
    LatLng latLng;
    Marker marker;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiLifecycleHelper = new UiLifecycleHelper(this, null);
        uiLifecycleHelper.onCreate(savedInstanceState);

        if (servicesOK()) {
            setContentView(R.layout.movie_detail);

            if (initMap()) {

                LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    mLocationClient = new LocationClient(MovieDetailActivity.this, MovieDetailActivity.this, MovieDetailActivity.this);
                    mLocationClient.connect();
                    mShowMap = true;
                }else{
                    Toast.makeText(this, "Location Manager Not Available", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Map not available!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            setContentView(R.layout.fragment_movie_detail);
        }


        Bundle bundle = getIntent().getExtras();

        movie = (Movie)bundle.getParcelable(".model.Movie");

        dataSource = new MoviesDataSource(this);
        dataSource.open();

    }

    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        }
        else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean initMap() {
        if(mMap == null){
            MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
            mMap = mapFragment.getMap();
        }
        return (mMap != null);
    }

    private void displayMovieDetail() throws IOException {

        new Thread(){
            public void run(){
                YoutubeConnector yc = new YoutubeConnector(MovieDetailActivity.this);
                videoIds = yc.search(movie.getTitle());
            }
        }.start();

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
        //ratingBar.setStepSize((float)0.1);
        ratingBar.setRating((float)movie.getVote_average());

        trailer_button = (Button)findViewById(R.id.button_trailer);
        trailer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieDetailActivity.this, MovieTrailer.class);
                intent.putExtra("Movie_Name", movie);
                startActivity(intent);
            }
        });

        locationClientConnectionStatus();
    }

    private void locationClientConnectionStatus() {
        if(mShowMap){
            if(mLocationClient.isConnected()) {
                Location mLocation = mLocationClient.getLastLocation();
                if (mLocation == null) {
                    Toast.makeText(this, "My Location is not available", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        gotoCurrentLocation();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(this, "I'm Here", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "LocationClient is Not Connected", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiLifecycleHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favourites, menu);
        MenuItem item = menu.findItem(R.id.addFavourite);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    public void publishFeedDialog(Movie movie) {
        Bundle params = new Bundle();
        params.putString("name", movie.getTitle());
        params.putString("caption", "This movie is Awesome !!!");
        params.putString("description", movieDescription);
        //params.putString("link", "https://developers.facebook.com/android");
        params.putString("picture", "http://d3gtl9l2a4fn1j.cloudfront.net/t/p/w500" + movie.getPoster_path());

        WebDialog feedDialog = (

                new WebDialog.FeedDialogBuilder(this,
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error == null) {
                            // When the story is posted, echo the success
                            // and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                Toast.makeText(MovieDetailActivity.this,
                                        "Posted story, id: "+postId,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // User clicked the Cancel button
                                Toast.makeText(MovieDetailActivity.this.getApplicationContext(),
                                        "Publish cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            // User clicked the "x" button
                            Toast.makeText(MovieDetailActivity.this.getApplicationContext(),
                                    "Publish cancelled",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Generic, ex: network error
                            Toast.makeText(MovieDetailActivity.this.getApplicationContext(),
                                    "Error posting story",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .build();
        feedDialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()){

            case R.id.share:

                //start Facebook Login

                if (Session.getActiveSession() == null || !Session.getActiveSession().isOpened()) {
                    Session.openActiveSession(this, true, new Session.StatusCallback() {

                        // callback when session changes state
                        @Override
                        public void call(Session session, SessionState state, Exception exception) {
                            if (state.isOpened()) {

                                // make request to the /me API
                                Request.newMeRequest(session, new Request.GraphUserCallback() {

                                    // callback after Graph API response with user object
                                    @Override
                                    public void onCompleted(GraphUser user, Response response) {
                                        if (user != null) {

                                            Toast.makeText(MovieDetailActivity.this, user.getName() + " Logged In...", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }).executeAsync();
                            }
                        }
                    });
                } else {
                    publishFeedDialog(movie);
                }


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

            case R.id.menu_google_maps:
                sendActionToIntent();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void gotoCurrentLocation() throws IOException {

        Location mLocation = mLocationClient.getLastLocation();
        if(mLocation == null){
            Toast.makeText(this, "My Location is not available", Toast.LENGTH_SHORT).show();
        }else{
            LatLng latLng = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,DEFAULTZOOM);
            mMap.animateCamera(cameraUpdate);
            Geocoder gc = new Geocoder(this);
            List<Address> list = gc.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(),1);
            Address add = list.get(0);
            String locality = add.getLocality();
            String country = add.getCountryName();
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
            setMarker(country, locality, mLocation.getLatitude(),mLocation.getLongitude());
        }
    }

    public void setMarker(String country, String locality, double lat, double lng) {
        if(marker != null){
            marker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .title(locality)
                .position(new LatLng(lat, lng))
                .anchor(.5f, .5f)
                .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_CYAN
                ))
                .draggable(true);
        if (country.length() > 0) {
            markerOptions.snippet(country);
        }
        marker = mMap.addMarker(markerOptions);
    }

    @SuppressWarnings("deprecation")
    private void sendActionToIntent() {
        StringBuilder uri = new StringBuilder("geo:");
        uri.append(latitude);
        uri.append(",");
        uri.append(longitude);
        uri.append("?z=10");
        uri.append("&q=" + URLEncoder.encode("Movie Theatre"));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
        startActivity(intent);

    }

    @Override
	protected void onResume() {
		super.onResume();
		dataSource.open();
        uiLifecycleHelper.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dataSource.close();
        uiLifecycleHelper.onPause();
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiLifecycleHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiLifecycleHelper.onSaveInstanceState(outState);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onConnected(Bundle bundle) {

        try {
            displayMovieDetail();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Toast.makeText(this,"Connected to the location services", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this,"Disconnected from the location services", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"Connection the location services Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Location" + location.getLatitude() + "," + location.getLongitude();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
