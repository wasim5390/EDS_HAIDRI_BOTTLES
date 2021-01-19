package com.optimus.eds.ui.route;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.Promotion;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder> {

    List<Promotion> promotionList ;

    final String FREE_GOOD = "FreeGood" , PROMO = "Promo" , UNIT = "Unit" , CARTON = "Carton" , PERCENTAGE = "%" , RUPEES = "Rs";

    public PromotionAdapter(List<Promotion> promotionList) {
        this.promotionList = promotionList;
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.promotion_custom_layout, parent, false);

        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {

        int pos = holder.getAdapterPosition();

        if (promotionList.get(pos).getPromoOrFreeGoodType().equals(PROMO)){

            holder.promotionName.setText(promotionList.get(pos).getName());

            holder.promotionSize.setText(String.valueOf(promotionList.get(pos).getAmount() + " " + promotionList.get(pos).getCalculationType()));

//            if (promotionList.get(pos).getSize().equals(CARTON))
//                holder.promotionSize.setText(String.valueOf(promotionList.get(pos).getAmount()));
//            else if (promotionList.get(pos).getSize().equals(UNIT))
//                holder.promotionSize.setText(String.valueOf(promotionList.get(pos).getAmount()));
        }else if(promotionList.get(pos).getPromoOrFreeGoodType().equals(FREE_GOOD)){

            holder.promotionName.setText(promotionList.get(pos).getFreeGoodName());

            holder.promotionSize.setText(promotionList.get(pos).getFreeGoodSize().concat(" " + promotionList.get(pos).getCalculationType()));
        }

    }

    @Override
    public int getItemCount() {
        return promotionList.size();
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder{

        TextView promotionName , promotionSize ;
        public PromotionViewHolder(@NonNull View itemView) {
            super(itemView);

            promotionName = itemView.findViewById(R.id.promotionNameText);
            promotionSize = itemView.findViewById(R.id.promotionSizeText);
        }
    }
}
