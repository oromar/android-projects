package br.com.oromar.dev.android.choosemovies;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    public static final int DEFAULT_NUM_COLUMNS = 2;
    private Context context;
    private List<Uri> urls;


    public ImageAdapter(Context context, List<Uri> urls){
        this.context = context;
        this.urls = urls;
    }

    public void add(Uri s){
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
                .load(urls.get(position))
                .noFade()
                .noPlaceholder()
                .resize(Math.round(niw), Math.round(nih))
                .into(imageView);

        return imageView;
    }
}
