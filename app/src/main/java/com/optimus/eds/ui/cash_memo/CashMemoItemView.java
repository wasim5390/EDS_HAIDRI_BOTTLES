package com.optimus.eds.ui.cash_memo;

import android.content.Context;
import com.google.android.material.card.MaterialCardView;

import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.utils.PreferenceUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import androidx.core.content.res.ResourcesCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.optimus.eds.Constant.PRIMARY;
import static com.optimus.eds.utils.Util.formatCurrency;

public class CashMemoItemView extends MaterialCardView {

    @BindView(R.id.tvProductName)
    TextView productName;
    @BindView(R.id.tvProductQty)
    TextView productQty;

    @BindView(R.id.tvProductTotal)
    TextView total;

    @BindView(R.id.rate_container)
    FrameLayout rateContainer;

    @BindView(R.id.free_items_container)
    LinearLayout freeItemsContainer;

    private OrderDetail order;

    private CashMemoFreeItemView.FreeItemSelector freeItemSelector;
    private CashMemoAdapter.ItemLevelPriceListener priceListener;

    public CashMemoItemView(Context context) {
        super(context);
    }

    public CashMemoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CashMemoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);

    }



    public void setCartItem(OrderDetail item, CashMemoFreeItemView.FreeItemSelector listener, CashMemoAdapter.ItemLevelPriceListener itemlevelPriceListener) {
        freeItemsContainer.removeAllViews();
        rateContainer.removeAllViews();
        this.order = item;
        this.freeItemSelector = listener;
        this.priceListener = itemlevelPriceListener;
        if (item != null) {


            Integer cartonQty = order.getCartonQuantity()==null?0:order.getCartonQuantity();
            Integer unitQty = order.getUnitQuantity()==null?0:order.getUnitQuantity();

            Double cartonTotalPrice = order.getCartonTotalPrice();
            Double unitTotalPrice  = order.getUnitTotalPrice();

            if(priceListener!=null) {
                boolean priceFound=true;
                if ((cartonQty > 0 && cartonTotalPrice <= 0) || (unitQty > 0 && unitTotalPrice <= 0))
                    priceFound = false;

                itemlevelPriceListener.onPriceAvailable(priceFound);
            }
            Double totalPrice = cartonTotalPrice+unitTotalPrice;

            String free = "";
            Integer freeCarton = item.getCartonFreeGoodQuantity()==null?0:item.getCartonFreeGoodQuantity();
            Integer freeUnits = item.getUnitFreeGoodQuantity()==null?0:item.getUnitFreeGoodQuantity();

            if(item.getCartonFreeQuantityTypeId()==PRIMARY
                    || item.getUnitFreeQuantityTypeId()==PRIMARY )
            {
                free="All";
            }else if(freeCarton>0 || freeUnits>0)
                free = String.valueOf(freeCarton+" / "+freeUnits);
            else
                free = "None";

            productName.setText(item.getProductName());

            if (!PreferenceUtil.getInstance(getContext()).getPunchOrder())
                productQty.setText(order.getQuantity());
            else{
                productQty.setText(order.getWithoutUnitQuantity());
            }
            total.setText(formatCurrency(totalPrice));

            rateContainer.addView(addPricingView(item));

            TextView textView = new TextView(getContext());
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.roboto_bold);
            textView.setTypeface(typeface);
            textView.setText("Free Items: "+free);
            textView.setPadding(10,10,10,0);
            if(item.getCartonFreeGoods().size()>0||item.getUnitFreeGoods().size()>0)
                freeItemsContainer.addView(textView);
            for(OrderDetail freeItems:item.getCartonFreeGoods()){
                freeItemsContainer.addView(addFreeItemsView(freeItems,1));
            }

            for(OrderDetail freeItems:item.getUnitFreeGoods()){
                freeItemsContainer.addView(addFreeItemsView(freeItems,2));
            }
            this.setOnClickListener(v -> {
                if(rateContainer.getVisibility()==VISIBLE)
                    rateContainer.setVisibility(GONE);
                else
                    rateContainer.setVisibility(VISIBLE);

            });
        }
    }

    public void unRegisterPriceListener(){
        priceListener = null;
    }


    private View addPricingView(OrderDetail item){
        LayoutInflater inflater =  LayoutInflater.from(getContext());
        CashMemoRateView rateView = (CashMemoRateView) inflater.inflate(R.layout.memo_rate_child_view,null);

        rateView.setRates(item);
        return rateView;
    }
    private View addFreeItemsView(OrderDetail item,int freeGoodType){
        LayoutInflater inflater =  LayoutInflater.from(getContext());
        CashMemoFreeItemView freeItemsView = (CashMemoFreeItemView) inflater.inflate(R.layout.cashmemo_free_item_view,null);
        freeItemsView.setCartItem(item,freeGoodType==1?order.getCartonFreeQuantityTypeId():order.getUnitFreeQuantityTypeId(),freeItemSelector);
        return freeItemsView;
    }

}
