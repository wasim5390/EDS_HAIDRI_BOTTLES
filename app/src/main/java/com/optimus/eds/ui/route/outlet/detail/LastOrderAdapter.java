package com.optimus.eds.ui.route.outlet.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.Promotion;
import com.optimus.eds.model.LastOrder;
import com.optimus.eds.model.OrderDetail;
import com.optimus.eds.ui.route.PromotionAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LastOrderAdapter extends RecyclerView.Adapter<LastOrderAdapter.LastOrderViewHolder> {

    List<OrderDetail> lastOrders ;


    public LastOrderAdapter(List<OrderDetail> lastOrders) {
        this.lastOrders = lastOrders;
    }

    @NonNull
    @Override
    public LastOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.last_order_item, parent, false);

        return new LastOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LastOrderViewHolder holder, int position) {

        int pos = holder.getAdapterPosition();

       holder.name.setText(lastOrders.get(pos).getProductName());
       holder.quantity.setText(String.valueOf(lastOrders.get(pos).getQuantity()));
       holder.total.setText(String.valueOf(lastOrders.get(pos).getProductTotal()));
    }

    @Override
    public int getItemCount() {
        return lastOrders.size();
    }

    public static class LastOrderViewHolder extends RecyclerView.ViewHolder{

        TextView name , quantity , total ;
        public LastOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameText);
            quantity = itemView.findViewById(R.id.quantityText);
            total = itemView.findViewById(R.id.totalText);
        }
    }
}

