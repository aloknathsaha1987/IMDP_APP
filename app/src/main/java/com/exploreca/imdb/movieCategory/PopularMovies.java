package com.exploreca.imdb.movieCategory;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.exploreca.imdb.httpManager.HttpManager;
import com.exploreca.imdb.adapter.MovieAdapter;
import com.exploreca.imdb.MovieDetailActivity;
import com.exploreca.imdb.R;
import com.exploreca.imdb.db.MovieDBOpenHelper;
import com.exploreca.imdb.db.MoviesDataSource;
import com.exploreca.imdb.model.Movie;
import com.exploreca.imdb.parser.MovieJSONParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALOKNATH on 1/15/2015.
 */
public class PopularMovies extends ListActivity {

    public static final String LOGGERSTR = "HELLO";
    public static final String API_VERSION = "3";
    public static final String CONTEXT_PATH = "movie/popular";
    public static final String API_KEY = "f47dd4de64c6ef630c2b0d50a087cc33";

    Context context;
    ProgressBar pb;
    List<MyTask> tasks;
    List<Movie> movieList;
    MoviesDataSource dataSource;
    private Boolean flag = false;
    private List<Movie> newMovieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        this.registerForContextMenu(getListView());
        dataSource = new MoviesDataSource(this);
        dataSource.open();

        pb = (ProgressBar) findViewById(R.id.progressBar1);

        pb.setVisibility(View.INVISIBLE);
        tasks = new ArrayList<>();
        movieList = dataSource.getPopularMovies();
        dataSource.close();

        if(movieList.isEmpty()){
            if (isOnline()) {
                requestData("http://api.themoviedb.org/");
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }else{
            //Toast.makeText(this, "Getting Movies from the Database", Toast.LENGTH_LONG).show();
            updateDisplay();
        }
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater m = getMenuInflater();
        m.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.addFavourite){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int index = info.position;
            Movie movie = movieList.get(index);
            MoviesDataSource dataSource = new MoviesDataSource(context);
            dataSource.open();
            dataSource.addToMyMovies(movie);
            dataSource.close();
            Toast.makeText(this, "Movie Added to My Favourites", Toast.LENGTH_LONG).show();
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        MenuItem item = menu.findItem(R.id.menu_popular);
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

        // Associate searchable configuration with the SearchView
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
                updateDisplay();
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void loadSearchData(String query, SearchView search){

        MoviesDataSource db = new MoviesDataSource(this);
        db.open();
        // Toast.makeText(this, query, Toast.LENGTH_LONG).show();
        Cursor cursor = db.getWordMatches(query, MoviesDataSource.allColumns, MovieDBOpenHelper.TABLE_POPULAR);
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
        }
        db.close();
        MovieAdapter adapter = new MovieAdapter(this, R.layout.item_movie, tempMovieList);
        setListAdapter(adapter);
        newMovieList = tempMovieList;
    }
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        menu.findItem(R.id.menu_now_playing).setVisible(false);
//        menu.findItem(R.id.menu_latest).setVisible(false);
//        menu.findItem(R.id.menu_popular).setVisible(true);
//        menu.findItem(R.id.menu_top_rated).setVisible(false);
//        menu.findItem(R.id.menu_upcoming).setVisible(false);
//        return true;
//    }

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
        if(flag){
            movie = newMovieList.get(position);

        }else{
            movie = movieList.get(position);
        }
        String value = String.valueOf(position);
        Log.i("PositionNo", movie.getPoster_path());
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(".model.Movie", movie);
        startActivity(intent);
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
            String content = HttpManager.getData(params[0], API_VERSION, CONTEXT_PATH, API_KEY);
            movieList = MovieJSONParser.parseFeedMovie(content);
            return movieList;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(PopularMovies.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            movieList = result;
            MoviesDataSource dataSource = new MoviesDataSource(context);
            dataSource.open();
            for(Movie movie: movieList){
                dataSource.addToPopularMovies(movie);
            }
            dataSource.close();
            updateDisplay();
        }

    }

}
