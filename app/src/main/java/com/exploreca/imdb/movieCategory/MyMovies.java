package com.exploreca.imdb.movieCategory;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.exploreca.imdb.adapter.MovieAdapter;
import com.exploreca.imdb.MovieDetailActivity;
import com.exploreca.imdb.R;
import com.exploreca.imdb.db.MovieDBOpenHelper;
import com.exploreca.imdb.db.MoviesDataSource;
import com.exploreca.imdb.model.Movie;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALOKNATH on 1/17/2015.
 */
public class MyMovies extends ListActivity {

    private List<Movie> movies;
    private List<Movie> newMovieList = new ArrayList<>();
    private boolean flag;
    MoviesDataSource dataSource;
    Context context;
    private final int DELETE_ID = R.id.delete;
    private UiLifecycleHelper uiLifecycleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        this.registerForContextMenu(getListView());
        uiLifecycleHelper = new UiLifecycleHelper(this, null);
        uiLifecycleHelper.onCreate(savedInstanceState);
        dataSource = new MoviesDataSource(this);
        dataSource.open();

        display();
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,DELETE_ID,0,R.string.delete);
        menu.add(0,2,1,"Share");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        Movie movie = movies.get(index);
        if (item.getItemId() == R.id.delete){

            Log.i("Movie_Name", movie.getTitle());
            if(dataSource.deleteMovie(movie.getTitle())){
                Toast.makeText(this, "Movie Deleted from My Favourites", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Movie Not Deleted", Toast.LENGTH_LONG).show();
            }
            display();
        }else if (item.getItemId() == 2){
            // Toast.makeText(GenreSelected.this,"Share Selected", Toast.LENGTH_LONG).show();
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

                                        Toast.makeText(MyMovies.this, user.getName() + " Logged In...", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).executeAsync();
                        }
                    }
                });
            } else {
                //Toast.makeText(GenreSelected.this,"coming to publish", Toast.LENGTH_LONG).show();
                publishFeedDialog(movie);
            }

        }
        return true;
    }

    public void publishFeedDialog(Movie movie) {
        Bundle params = new Bundle();
        params.putString("name", movie.getTitle());
        params.putString("caption", "This movie is Awesome !!!");
        params.putString("description", GenreSelected.movieDescription);
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
                                Toast.makeText(MyMovies.this,
                                        "Posted story, id: "+postId,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // User clicked the Cancel button
                                Toast.makeText(MyMovies.this.getApplicationContext(),
                                        "Publish cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            // User clicked the "x" button
                            Toast.makeText(MyMovies.this.getApplicationContext(),
                                    "Publish cancelled",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Generic, ex: network error
                            Toast.makeText(MyMovies.this.getApplicationContext(),
                                    "Error posting story",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .build();
        feedDialog.show();
    }


    private void display() {

        movies = dataSource.getMyMovies();
        MovieAdapter adapter = new MovieAdapter(this, R.layout.item_movie, movies);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Movie movie;
        if(flag){
            movie = newMovieList.get(position);

        }else{
            movie = movies.get(position);
        }

        //String value = String.valueOf(position);
        Log.i("PositionNo", movie.getPoster_path());
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(".model.Movie", movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        MenuItem item = menu.findItem(R.id.myFavourite);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        SubMenu subGenre= menu.addSubMenu("Genre");
        subGenre.add(1,1,0,"Action");
        subGenre.add(1,2,0,"Adventure");
        subGenre.add(1,3,0,"Animation");
        subGenre.add(1,4,0,"Comedy");
        subGenre.add(1,5,0,"Crime");
        subGenre.add(1,6,0,"Documentary");
        subGenre.add(1,7,0,"Drama");
        subGenre.add(1,8,0,"Family");
        subGenre.add(1,9,0,"Fantasy");
        subGenre.add(1,10,0,"Foreign");
        subGenre.add(1,11,0,"History");
        subGenre.add(1,12,0,"Horror");
        subGenre.add(1,13,0,"Music");
        subGenre.add(1,14,0,"Mystery");
        subGenre.add(1,15,0,"Romance");
        subGenre.add(1,16,0,"Science Fiction");
        subGenre.add(1,17,0,"TV Movie");
        subGenre.add(1,18,0,"Thriller");
        subGenre.add(1,19,0,"War");
        subGenre.add(1,20,0,"Western");


        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                loadSearchData(search, searchView);
                flag = true;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                loadSearchData(search, searchView);
                flag = true;
                return false;
            }

        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                flag = false;
               // Toast.makeText(MyMovies.this, "Flag = false", Toast.LENGTH_LONG).show();
                display();
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void loadSearchData(String query, SearchView search){

        MoviesDataSource db = new MoviesDataSource(this);
       // Toast.makeText(this, query, Toast.LENGTH_LONG).show();
        Cursor cursor = db.getWordMatches(query, MoviesDataSource.allColumns, MovieDBOpenHelper.TABLE_MY_MOVIES);
        List<Movie> tempMovieList = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Movie movie = new Movie();
                movie.setId(cursor.getInt(cursor.getColumnIndex(MovieDBOpenHelper.COLUMN_ID)));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieDBOpenHelper.COLUMN_TITLE)));
                movie.setPoster_path(cursor.getString(cursor.getColumnIndex(MovieDBOpenHelper.COLUMN_POSTER_PATH)));
                movie.setRelease_date(cursor.getString(cursor.getColumnIndex(MovieDBOpenHelper.COLUMN_RELEASE_DATE)));
                movie.setVote_count(cursor.getInt(cursor.getColumnIndex(MovieDBOpenHelper.COLUMN_VOTE_CNT)));
                movie.setVote_average(cursor.getDouble(cursor.getColumnIndex(MovieDBOpenHelper.COLUMN_VOTE_AVG)));
                movie.setBackdrop_path(cursor.getString(cursor.getColumnIndex(MovieDBOpenHelper.COLUMN_BACKDROP)));
                movie.setOriginal_title(cursor.getString(cursor.getColumnIndex(MovieDBOpenHelper.COLUMN_ORIGINAL_TITLE)));
                movie.setPopularity(cursor.getDouble(cursor.getColumnIndex(MovieDBOpenHelper.COLUMN_POPULARITY)));
                movie.setAdult(cursor.getInt(cursor.getColumnIndex(MovieDBOpenHelper.COLUMN_ADULT)) == 1 ? true : false);
                movie.setVideo(cursor.getInt(cursor.getColumnIndex(MovieDBOpenHelper.COLUMN_VIDEO)) == 1 ? true : false);
                tempMovieList.add(movie);
            }
        }else{
            Toast.makeText(this, "No Search Results Found", Toast.LENGTH_SHORT).show();
        }
        MovieAdapter adapter = new MovieAdapter(this, R.layout.item_movie, tempMovieList);
        setListAdapter(adapter);
        newMovieList = tempMovieList;
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
                intent.putExtra("genreMenu", "Action");
                startActivity(intent);
                break;

            case 2:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Adventure"));
                intent.putExtra("genreMenu", "Adventure");
                startActivity(intent);
                break;

            case 3:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Animation"));
                intent.putExtra("genreMenu", "Animation");
                startActivity(intent);
                break;

            case 4:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Comedy"));
                intent.putExtra("genreMenu", "Comedy");
                startActivity(intent);
                break;


            case 5:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Crime"));
                intent.putExtra("genreMenu", "Crime");
                startActivity(intent);
                break;


            case 6:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Documentary"));
                intent.putExtra("genreMenu", "Documentary");
                startActivity(intent);
                break;


            case 7:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Drama"));
                intent.putExtra("genreMenu", "Drama");
                startActivity(intent);
                break;

            case 8:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Family"));
                intent.putExtra("genreMenu", "Family");
                startActivity(intent);
                break;


            case 9:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Fantasy"));
                intent.putExtra("genreMenu", "Fantasy");
                startActivity(intent);
                break;


            case 10:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Foreign"));
                intent.putExtra("genreMenu", "Foreign");
                startActivity(intent);
                break;


            case 11:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("History"));
                intent.putExtra("genreMenu", "History");
                startActivity(intent);
                break;


            case 12:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Horror"));
                intent.putExtra("genreMenu", "Horror");
                startActivity(intent);
                break;


            case 13:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Music"));
                intent.putExtra("genreMenu", "Music");
                startActivity(intent);
                break;


            case 14:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Mystery"));
                intent.putExtra("genreMenu", "Mystery");
                startActivity(intent);
                break;


            case 15:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Romance"));
                intent.putExtra("genreMenu", "Romance");
                startActivity(intent);
                break;


            case 16:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Science Fiction"));
                intent.putExtra("genreMenu", "Science Fiction");
                startActivity(intent);
                break;


            case 17:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("TV Movie"));
                intent.putExtra("genreMenu", "TV Movie");
                startActivity(intent);
                break;


            case 18:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Thriller"));
                intent.putExtra("genreMenu", "Thriller");
                startActivity(intent);
                break;


            case 19:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("War"));
                intent.putExtra("genreMenu", "War");
                startActivity(intent);
                break;


            case 20:
                intent = new Intent(this, GenreSelected.class);
                intent.putExtra("genreID", NowPlayingMovies.genreMap.get("Western"));
                intent.putExtra("genreMenu", "Western");
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

}
