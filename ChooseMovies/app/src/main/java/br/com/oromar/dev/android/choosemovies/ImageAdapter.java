package br.com.oromar.dev.android.choosemovies;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.internal.VersionUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageAdapter extends BaseAdapter {

    public static final int DEFAULT_NUM_COLUMNS = 2;
    private Context context;
    private List<String> urls;


    public ImageAdapter(Context context, List<String> urls){
        this.context = context;
        this.urls = urls;
    }

    public void add(String s){
        urls.add(s);
    }

    public void remove(int position) {
        urls.remove(position);
    }

    public void clear(){
        urls.clear();
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
        } else {
            imageView = (ImageView) convertView;
        }
        float ih = imageView.getHeight();
        float iw = imageView.getWidth();
        float niw;
        niw = parent.getWidth() / DEFAULT_NUM_COLUMNS;
        float nih = ih / iw * niw;

        Picasso.with(context)
                .load(Uri.parse(urls.get(position)))
                .resize(Math.round(niw), Math.round(nih))
                .into(imageView);
        return imageView;
    }
}
