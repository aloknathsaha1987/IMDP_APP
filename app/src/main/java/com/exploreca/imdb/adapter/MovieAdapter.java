package com.exploreca.imdb.adapter;

/**
 * Created by ALOKNATH on 1/16/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;


/**
 * Created by ALOKNATH on 1/15/2015.
 */

import com.exploreca.imdb.MainActivity;
import com.exploreca.imdb.R;
import com.exploreca.imdb.model.Movie;

/**
 * Created by ALOKNATH on 1/14/2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie> implements View.OnCreateContextMenuListener{

    private Context context;
    private List<Movie> movieList;
    private LruCache<Integer, Bitmap> imageCache;
    public static final String DEFAULT_IMAGE = "http://rahatelectronics.com.pk/wp-content/themes/rahatelectronics/images/image_not_found.jpg";

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo menuInfo) {

    }

    public MovieAdapter(Context context, int resource, List<Movie> objects) {
        super(context, resource, objects);
        this.context = context;
        this.movieList = objects;
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() /1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_movie, parent, false);

        Movie movie = movieList.get(position);

        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(movie.getTitle());

        RatingBar ratingBar = (RatingBar)view.findViewById(R.id.ratingBar);
        //ratingBar.setStepSize((float)0.1);
        ratingBar.setRating((float)movie.getVote_average());

        Bitmap bitmap = imageCache.get(movie.getId());

        if (bitmap != null) {
            ImageView image = (ImageView) view.findViewById(R.id.imageView1);
            image.setImageBitmap(bitmap);
        } else {

            MovieAndView container = new MovieAndView();
            container.movie = movie;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        return view;
    }

    private class MovieAndView{
        public Movie movie;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<MovieAndView, Void, MovieAndView> {

        @Override
        protected MovieAndView doInBackground(MovieAndView... params){

            MovieAndView container = params[0];
            Movie movie = container.movie;
            // String tester = null;
            //hello
            String imageUrl;

            try {
                String imageU = movie.getPoster_path();
                if((imageU.isEmpty() && imageU == null) || imageU.equals("null"))
                {
                    imageUrl = DEFAULT_IMAGE;
                }else{
                    imageUrl = MainActivity.PHOTOS_BASE_URL + movie.getPoster_path();
                }
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                //movie.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.imageView1);
            image.setImageBitmap(result.bitmap);
            //result.movie.setBitmap(result.bitmap);
            imageCache.put(result.movie.getId(), result.bitmap);
        }
    }

}
