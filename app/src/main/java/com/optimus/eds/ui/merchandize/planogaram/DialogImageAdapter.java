package com.optimus.eds.ui.merchandize.planogaram;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.optimus.eds.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class DialogImageAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> urls;
    private LayoutInflater inflater;

    public DialogImageAdapter(Context mContext, List<String> urls) {
        this.mContext = mContext;
        this.urls = urls;
        inflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        View imageLayout = inflater.inflate(R.layout.slider_image_view, collection, false);
        assert imageLayout != null;
        ImageView imageView = (ImageView) imageLayout.findViewById(R.id.imageView);
        File imgFile = new File(urls.get(position));
        Picasso.get().load(imgFile).placeholder(R.drawable.cooler).into(imageView);
        collection.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return urls.get(position);
    }
}
