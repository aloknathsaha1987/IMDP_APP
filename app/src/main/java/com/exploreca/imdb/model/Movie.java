package com.exploreca.imdb.model;

/**
 * Created by ALOKNATH on 1/16/2015.
 */
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by ALOKNATH on 1/14/2015.
 */

public class Movie implements Parcelable {

    private Boolean adult;
    private String backdrop_path;
    private int id;
    private String original_title;
    private String release_date;
    private String poster_path;
    private double popularity;
    private String title;
    private Boolean video;
    private double vote_average;
    private int vote_count;
   // private Bitmap bitmap;

    public Movie(){

    }


    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }
//    public Bitmap getBitmap() {
//        return bitmap;
//    }
//    public void setBitmap(Bitmap bitmap) {
//        this.bitmap = bitmap;
//    }

    public Movie(Parcel in) {
        //Log.i(MainActivity.LOGTAG, "Parcel constructor");

        backdrop_path = in.readString();
        id = in.readInt();
        original_title = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        popularity = in.readDouble();
        title = in.readString();
        vote_average = in.readDouble();
        vote_count = in.readInt();
      //  bitmap = in.readParcelable(Bitmap.class.getClassLoader());
       // bitmap = null;
        adult = in.readByte() != 0;
        video = in.readByte() != 0;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Log.i(MainActivity.LOGTAG, "writeToParcel");

        dest.writeString(backdrop_path);
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeDouble(popularity);
        dest.writeString(title);
        dest.writeDouble(vote_average);
        dest.writeInt(vote_count);
        //dest.writeValue(bitmap);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeByte((byte) (video ? 1 : 0));
    }

    public static final Parcelable.Creator<Movie> CREATOR =
            new Parcelable.Creator<Movie>() {

                @Override
                public Movie createFromParcel(Parcel source) {
                    //Log.i(MainActivity.LOGTAG, "createFromParcel");
                    return new Movie(source);
                }

                @Override
                public Movie[] newArray(int size) {
                    //Log.i(MainActivity.LOGTAG, "newArray");
                    return new Movie[size];
                }

            };

}
