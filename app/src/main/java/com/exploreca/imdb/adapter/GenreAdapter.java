package com.exploreca.imdb.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.exploreca.imdb.R;
import com.exploreca.imdb.model.Genre;

import java.util.List;

/**
 * Created by ALOKNATH on 1/29/2015.
 */
public class GenreAdapter extends ArrayAdapter<Genre> {

    private Context context;
    private List<Genre> genreList;

    public GenreAdapter(Context context, int resource, List<Genre> objects){
        super(context, resource, objects);
        this.context = context;
        this.genreList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_genre, parent, false);
        Genre genre = genreList.get(position);
        TextView textView = (TextView)view.findViewById(R.id.textView1);
        textView.setText(genre.getGenreType());
        return view;
    }
}
