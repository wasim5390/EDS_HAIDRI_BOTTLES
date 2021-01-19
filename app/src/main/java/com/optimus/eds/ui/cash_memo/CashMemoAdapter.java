package com.optimus.eds.ui.cash_memo;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;

import java.util.ArrayList;
import java.util.List;

public class CashMemoAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private List<OrderDetailAndPriceBreakdown> products;
    private  FreeItemListener mListener;
    private ItemLevelPriceListener itemLevelPriceListener;

    public CashMemoAdapter(Context context) {
        this.mContext = context;
        this.products = new ArrayList<>();
    }

    public void populateCartItems(List<OrderDetailAndPriceBreakdown> products,FreeItemListener listener,ItemLevelPriceListener itemLevelPriceListener) {
        this.mListener = listener;
        this.itemLevelPriceListener = itemLevelPriceListener;
        this.products = products;
        notifyDataSetChanged();
    }

    public void unRegisterPriceListener(){
        itemLevelPriceListener = null;
    }

    public List<OrderDetailAndPriceBreakdown> getCartItems(){
        return products;
    }

    @NonNull
    @Override
    public CartProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.cashmemo_item_view, parent, false);

        return new CartProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OrderDetail product = products.get(position).getOrderDetail();

        ((CashMemoItemView)viewHolder.itemView).setCartItem(product, new CashMemoFreeItemView.FreeItemSelector() {
            @Override
            public void onFreeItemAdd(OrderDetail freeItem) {
                if (freeItem.getCartonQuantity() > 0)
                    addCartonFreeItems(product, freeItem);
                else if (freeItem.getUnitQuantity() > 0)
                    addUnitFreeItems(product, freeItem);
            }

            @Override
            public void onFreeItemRemove(OrderDetail freeItem) {
                if (freeItem.getCartonQuantity() > 0)
                    removeCartonFreeItems(product, freeItem);
                else if (freeItem.getUnitQuantity() > 0)
                    removeUnitFreeItems(product, freeItem);
            }
        }, isAvailable -> {
            if(itemLevelPriceListener!=null)
            itemLevelPriceListener.onPriceAvailable(isAvailable);

        });
    }


    private void removeCartonFreeItems(OrderDetail product,OrderDetail freeItem){

        Integer superSelectedCartonQty = product.getSelectedCartonFreeGoodQuantity()==null?0:product.getSelectedCartonFreeGoodQuantity();
        Integer selectedCartonQty = freeItem.getSelectedCartonFreeGoodQuantity()==null?0:freeItem.getSelectedCartonFreeGoodQuantity();

        if(selectedCartonQty>0){
            selectedCartonQty--;
            freeItem.setSelectedCartonFreeGoodQuantity(selectedCartonQty);
            product.setSelectedCartonFreeGoodQuantity(--superSelectedCartonQty);
            onDataUpdated();
        }
    }
    private void removeUnitFreeItems(OrderDetail product,OrderDetail freeItem){

        Integer superSelectedUnitQty = product.getSelectedUnitFreeGoodQuantity()==null?0:product.getSelectedUnitFreeGoodQuantity();
        Integer selectedUnitQty = freeItem.getSelectedUnitFreeGoodQuantity()==null?0:freeItem.getSelectedUnitFreeGoodQuantity();

        if(selectedUnitQty>0){
            selectedUnitQty--;
            freeItem.setSelectedUnitFreeGoodQuantity(selectedUnitQty);
            product.setSelectedUnitFreeGoodQuantity(--superSelectedUnitQty);
            onDataUpdated();

        }
    }
    private void addCartonFreeItems(OrderDetail product,OrderDetail freeItem){

        Integer selectedCartonQty = product.getSelectedCartonFreeGoodQuantity()==null?0:product.getSelectedCartonFreeGoodQuantity();

        if(product.getCartonFreeGoodQuantity()>selectedCartonQty){
            selectedCartonQty++;
            freeItem.setSelectedCartonFreeGoodQuantity(selectedCartonQty);
            product.setSelectedCartonFreeGoodQuantity(selectedCartonQty);
            onDataUpdated();
        }
    }

    private void addUnitFreeItems(OrderDetail product,OrderDetail freeItem){


        Integer selectedUnitQty = product.getSelectedUnitFreeGoodQuantity()==null?0:product.getSelectedUnitFreeGoodQuantity();

        if(product.getUnitFreeGoodQuantity()>selectedUnitQty){
            selectedUnitQty++;
            freeItem.setSelectedUnitFreeGoodQuantity(selectedUnitQty);
            product.setSelectedUnitFreeGoodQuantity(selectedUnitQty);
            onDataUpdated();
        }
    }

    private void onDataUpdated(){
        mListener.onFreeItemsSelected(products);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class CartProductHolder extends RecyclerView.ViewHolder {

        View view;
        public CartProductHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }

    }

    public interface FreeItemListener{
        void onFreeItemsSelected(List<OrderDetailAndPriceBreakdown> products);
    }

    public interface ItemLevelPriceListener{
        void onPriceAvailable(boolean isAvailable);
    }
}
