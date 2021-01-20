package com.optimus.eds.ui.merchandize;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.optimus.eds.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By apple on 4/24/19
 */
public class MerchandiseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<MerchandiseImage> merchandiseImages;

    public MerchandiseAdapter(Context context) {
        this.mContext = context;
        this.merchandiseImages = new ArrayList<>();
    }

    public void populateMerchandise(List<MerchandiseImage> merchandiseImages) {
        this.merchandiseImages = merchandiseImages;
        notifyDataSetChanged();
    }

    @Override
    public MerchandiseListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.merchandise_list_item, parent, false);

        return new MerchandiseListHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MerchandiseListHolder merchandiseListHolder = (MerchandiseListHolder) holder;
        MerchandiseImage merchandiseImage = merchandiseImages.get(position);
        File imgFile = new File(merchandiseImage.getPath());
        Picasso.get().load(imgFile).fit().into(merchandiseListHolder.imageView);
        merchandiseListHolder.deleteImageView.setOnClickListener(view -> ((OutletMerchandiseActivity) mContext).removeImage(merchandiseImage));
    }

    @Override
    public int getItemCount() {
        return merchandiseImages.size();
    }

    static class MerchandiseListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image)
        ImageView imageView;
        @BindView(R.id.imageView_delete)
        ImageView deleteImageView;

        MerchandiseListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
