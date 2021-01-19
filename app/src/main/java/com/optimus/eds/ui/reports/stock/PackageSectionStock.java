package com.optimus.eds.ui.reports.stock;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.ui.order.PackageSection;
import com.optimus.eds.utils.Util;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class PackageSectionStock extends StatelessSection {


    private final String title;
    private final List<Product> list;


    PackageSectionStock(PackageModel pkg) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.stock_item_view)
                .headerResourceId(R.layout.section_header)
                .build());
        this.title = pkg.getPackageName();
        this.list = pkg.getProducts();

    }

    public List<Product> getList() {
        return list;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new PackageSectionStock.ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PackageSectionStock.ItemViewHolder itemHolder = (PackageSectionStock.ItemViewHolder) holder;

        Product product = list.get(position);

        itemHolder.tvItemName.setText(product.getName());

        itemHolder.whStock.setText(String.valueOf(Util.convertStockToDecimalQuantity(product.getCartonStockInHand(),product.getUnitStockInHand())));

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new PackageSectionStock.HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        PackageSectionStock.HeaderViewHolder headerHolder = (PackageSectionStock.HeaderViewHolder) holder;

        headerHolder.tvTitle.setText(title);
    }




    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        HeaderViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tvTitle);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {


        private final TextView tvItemName;
        private final TextView whStock;

        ItemViewHolder(View view) {
            super(view);

            tvItemName = view.findViewById(R.id.item_name);
            whStock = view.findViewById(R.id.wh_stock);
        }
    }

}
