package com.exploreca.imdb.parser;

/**
 * Created by ALOKNATH on 1/15/2015.
 */

import com.exploreca.imdb.model.Genre;
import com.exploreca.imdb.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieJSONParser {

    public static String parseMovieDetail(String content){
        String movieDetail;
        try {
            JSONObject jsonObject = new JSONObject(content);
            movieDetail = jsonObject.getString("overview");

        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return movieDetail;
    }

    public static List<Genre> parseGenreMovie(String content){
        List<Genre> genreList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONArray jsonArray = jsonObject.getJSONArray("genres");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                Genre genre = new Genre();
                genre.setId(obj.getInt("id"));
                genre.setGenreType(obj.getString("name"));
                genreList.add(genre);
            }

        }catch (JSONException jexp){
            jexp.printStackTrace();
            return null;
        }

        return genreList;
    }

    public static List<Movie> parseFeedMovie(String content) {

        try {

            JSONObject jsonObject = new JSONObject(content);
            List<Movie> movieList = new ArrayList<>();
            JSONArray objs =  jsonObject.getJSONArray("results");

            for (int i = 0; i < objs.length(); i++) {

                JSONObject obj = objs.getJSONObject(i);
                Movie movie = new Movie();
                movie.setAdult(obj.getBoolean("adult"));
                movie.setBackdrop_path(obj.getString("backdrop_path"));
                movie.setId(obj.getInt("id"));
                movie.setOriginal_title(obj.getString("original_title"));
                movie.setRelease_date(obj.getString("release_date"));
                movie.setPoster_path(obj.getString("poster_path"));
                movie.setPopularity(obj.getDouble("popularity"));
                movie.setTitle(obj.getString("title"));
                movie.setVideo(obj.getBoolean("video"));
                movie.setVote_average(obj.getDouble("vote_average"));
                movie.setVote_count(obj.getInt("vote_count"));

                movieList.add(movie);

            }

            return movieList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}

