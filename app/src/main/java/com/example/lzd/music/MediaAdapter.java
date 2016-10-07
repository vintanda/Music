package com.example.lzd.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by LZD on 2016/10/6.
 */
public class MediaAdapter extends ArrayAdapter {
    private int resourceId;
    public MediaAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MusicData musicData = (MusicData) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.List_image);
        TextView textView = (TextView) view.findViewById(R.id.List_text);
        imageView.setImageResource(R.drawable.pic_cd);
        textView.setText(musicData.getName() + " " + musicData.getArtist());
        return view;
    }
}
