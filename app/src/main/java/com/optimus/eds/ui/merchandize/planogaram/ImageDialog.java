package com.optimus.eds.ui.merchandize.planogaram;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.optimus.eds.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created By apple on 4/29/19
 */
public class ImageDialog extends DialogFragment {

    @BindView(R.id.tv_count)
    TextView countTextView;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    DialogImageAdapter mAdapter;
    List<String> urls;
    int index;

    public static ImageDialog newInstance(List<String> urls) {
        ImageDialog f = new ImageDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("list", (ArrayList<String>)urls);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_image, container, false);
        ButterKnife.bind(this, v);

        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        urls = getArguments().getStringArrayList("list");

        mAdapter = new DialogImageAdapter(getContext(), this.urls);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(index);
        countTextView.setText((index + 1) + "/" + urls.size());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                index = position;
                countTextView.setText((index + 1) + "/" + urls.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return v;
    }

    @OnClick(R.id.iv_arrow_left)
    public void left(){
        if(viewPager.getCurrentItem()>0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
        }
    }

    @OnClick(R.id.iv_arrow_right)
    public void right(){
        if(viewPager.getCurrentItem() < viewPager.getAdapter().getCount())
        viewPager.setCurrentItem(viewPager.getCurrentItem()+1, true);
    }

    @Override
    public int getTheme() {
        return R.style.FullScreenDialog;
    }

    @OnClick(R.id.iv_close)
    public void close() {
        dismiss();
    }
}
