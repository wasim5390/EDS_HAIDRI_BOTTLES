package com.optimus.eds.ui.order;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.optimus.eds.EdsApplication;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class PackageSection extends StatelessSection {


    private final String title;
    private final List<Product> list;
    private final QtySelectionCallback mCallback;
    private Context context;

    PackageSection(Context context  , PackageModel pkg, QtySelectionCallback callback) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.order_booking_item_view)
                .headerResourceId(R.layout.section_header)
                .build());
        this.context = context;
        this.title = pkg.getPackageName();
        this.list = pkg.getProducts();
        this.mCallback = callback;

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
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;

        Product product = list.get(position);

        itemHolder.tvItemName.setText(product.getName());

        if (position == 0){
            itemHolder.etOrderQty.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }


//        itemHolder.whStock.setText(String.valueOf(Util.convertStockToDecimalQuantity(product.getCartonStockInHand(),product.getUnitStockInHand())));
        itemHolder.whStock.setText(String.valueOf(product.getCartonStockInHand()));

        itemHolder.etAvlStock.setText(String.valueOf(Util.convertToNullableDecimalQuantity(product.getAvlStockCarton(),product.getAvlStockUnit())));

//        itemHolder.etOrderQty.setText(String.valueOf(Util.convertToNullableDecimalQuantity(product.getQtyCarton(),product.getQtyUnit())));
        if (product.getQtyCarton() != null)
        itemHolder.etOrderQty.setText(String.valueOf(product.getQtyCarton()));

        itemHolder.etAvlStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()<1 || (s.length()==1 && s.toString().equals("."))) {
                    product.setAvlStock(null,null);
                    return;
                }
                double qty = Double.parseDouble(s.toString());
                if(qty>0){
                    Integer[] cu = Util.convertToLongQuantity(s.toString());
                    product.setAvlStock(cu[0],cu[1]);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (PreferenceUtil.getInstance(EdsApplication.getContext()).getPunchOrder()){
          itemHolder.etOrderQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        }else{
            itemHolder.etOrderQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        itemHolder.etOrderQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()<1 || (s.length()==1 && s.toString().equals("."))) {
                    product.setQty(null,null);
                    return;
                }

                double qty = Double.parseDouble(s.toString());
                Integer unitStock = product.getUnitStockInHand();
                if(qty>0){
                    Integer[] cu = Util.convertToLongQuantity(s.toString());
                    Integer enteredQty = Util.convertToUnits(cu[0],product.getCartonQuantity(),cu[1]);
                    if(enteredQty>unitStock)
                    {
                        s = s.toString().substring(0,start);
                        itemHolder.etOrderQty.setText(s.toString());
                        itemHolder.etOrderQty.setSelection(start);
                        mCallback.onInvalidQtyEntered();
                    }
                    else
                    {
                        product.setQty(cu[0],cu[1]);

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

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
        private final  EditText etAvlStock;
        private final EditText etOrderQty;

        ItemViewHolder(View view) {
            super(view);

            tvItemName = view.findViewById(R.id.item_name);
            whStock = view.findViewById(R.id.wh_stock);
            etAvlStock = view.findViewById(R.id.avl_stock);
            etOrderQty = view.findViewById(R.id.order_unit);

        }
    }
    interface QtySelectionCallback{
        void onInvalidQtyEntered();
    }

}