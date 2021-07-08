package com.optimus.eds.ui.cash_memo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.PriceBreakDown;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
import com.optimus.eds.model.PricingModel;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import butterknife.ButterKnife;

import static com.optimus.eds.utils.Util.formatCurrency;

public class CashMemoRateView extends LinearLayout {

    public CashMemoRateView(Context context) {
        super(context);
    }

    public CashMemoRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CashMemoRateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);

    }

    public void setRates(OrderDetail orderDetail) {

        if (Util.isListEmpty(orderDetail.getUnitPriceBreakDown()) && Util.isListEmpty(orderDetail.getCartonPriceBreakDown()))
            return;
        Gson gson = new Gson();
        List<CartonPriceBreakDown> cartonPriceBreakDowns = orderDetail.getCartonPriceBreakDown()==null?new ArrayList<>():orderDetail.getCartonPriceBreakDown();
        List<UnitPriceBreakDown> unitPriceBreakDowns = orderDetail.getUnitPriceBreakDown()==null?new ArrayList<>():orderDetail.getUnitPriceBreakDown();


        LayoutInflater inflater = LayoutInflater.from(getContext());

        List<PriceBreakDown> breakDowns =  gson.fromJson(gson.toJson(cartonPriceBreakDowns),new TypeToken<List<PriceBreakDown>>() {}.getType());
        breakDowns.addAll(gson.fromJson(gson.toJson(unitPriceBreakDowns),new TypeToken<List<PriceBreakDown>>() {}.getType()));
        HashMap<PriceBreakDown, List<PriceBreakDown>> listHashMap = calculate(breakDowns);

        // Sort method needs a List, so let's first convert HashList to List in Java
        List<Map.Entry<PriceBreakDown, List<PriceBreakDown>>> listOfEntries = new ArrayList<>(listHashMap.entrySet());

        Collections.sort(listOfEntries, (o1, o2) -> o1.getKey().getPriceConditionClassOrder().compareTo(o2.getKey().getPriceConditionClassOrder()));

        for (Map.Entry<PriceBreakDown, List<PriceBreakDown>> entry : listOfEntries) {

            LinearLayout rateView = (LinearLayout) inflater.inflate(R.layout.rate_child_layout, null);
            TextView title = rateView.findViewById(R.id.productRate);
            TextView rate = rateView.findViewById(R.id.tvProductRate);
            Double unitPrice = 0.0, cartonPrice = 0.0;
            String type = "";

            for (PriceBreakDown breakDown : entry.getValue()) {
                cartonPrice = breakDown.getBlockPrice()==null?0:breakDown.getBlockPrice();
                unitPrice = breakDown.getBlockPrice()==null?0:breakDown.getBlockPrice();
                type =  breakDown.getPriceConditionType();
            }


            if (!PreferenceUtil.getInstance(getContext()).getPunchOrder())
                rate.setText(formatCurrency(cartonPrice.doubleValue()) + " / " + formatCurrency(unitPrice.doubleValue()));
            else
                rate.setText(formatCurrency(cartonPrice.doubleValue()));
            title.setText(type);
            this.addView(rateView);

        }
    }



    public HashMap<PriceBreakDown, List<PriceBreakDown>> calculate(List<PriceBreakDown> priceBreakDownList) {

        PriceBreakDown promos=null, discount=null, tax=null,tradePrice=null;
        HashMap<PriceBreakDown, List<PriceBreakDown>> hashMap = new HashMap<>();

        for (PriceBreakDown breakDown : priceBreakDownList) {


            if(!(breakDown.getmPriceConditionClass().toLowerCase().equalsIgnoreCase("retailer margin")
                    ||breakDown.getmPriceConditionClass().toLowerCase().equalsIgnoreCase("market discount hth")
                    || breakDown.getmPriceConditionClass().toLowerCase().equalsIgnoreCase("rental discount")
                    ||breakDown.getmPriceConditionClass().toLowerCase().equalsIgnoreCase("promotions")
                    ||breakDown.getmPriceConditionClass().toLowerCase().equalsIgnoreCase("consumer rate off")
                    ||breakDown.getmPriceConditionClass().toLowerCase().equalsIgnoreCase("tax")))
                continue;

            // add updated list items here......
            if (breakDown.getmPriceConditionClass().toLowerCase().equals("retailer margin")) {

                breakDown.mBlockPrice = Double.valueOf( breakDown.getTotalPrice());
                breakDown.setPriceConditionType("Trade Price");
                tradePrice = breakDown;
                tradePrice.setPriceConditionClassOrder(1);

            }
            if (breakDown.getmPriceConditionClass().toLowerCase().equals("market discount hth") || breakDown.getmPriceConditionClass().toLowerCase().equals("rental discount")) {
                breakDown.setPriceConditionType("Discounts");
                if(discount!=null)
                    discount.addBlockPrice(breakDown.mBlockPrice);
                else
                    discount = breakDown;
                discount.setPriceConditionClassOrder(2);
            }
            if (breakDown.getmPriceConditionClass().toLowerCase().equals("consumer rate off") || breakDown.getmPriceConditionClass().toLowerCase().equals("promotions")) {
                breakDown.setPriceConditionType("Promos");
                if(promos!=null)
                    promos.addBlockPrice(breakDown.mBlockPrice);
                else
                    promos = breakDown;

                promos.setPriceConditionClassOrder(3);
            }
            if (breakDown.getmPriceConditionClass().toLowerCase().equals("tax")) {
                breakDown.setPriceConditionType("Tax");
                if(tax!=null)
                    tax.addBlockPrice(breakDown.mBlockPrice);
                else
                    tax = breakDown;
                tax.setPriceConditionClassOrder(4);
            }




            //.........................


        /*    if (!hashMap.containsKey(breakDown)) {
                List<PriceBreakDown> list = new ArrayList<>();
                list.add(breakDown);
                hashMap.put(breakDown, list);
            } else {
                hashMap.get(breakDown).add(breakDown);
            }*/
        }
        tradePrice = tradePrice==null?new PriceBreakDown(1,"Trade Price"):tradePrice;
        discount = discount==null?new PriceBreakDown(2,"Discounts"):discount;
        promos = promos==null?new PriceBreakDown(3, "Promos"):promos;
        tax = tax==null?new PriceBreakDown(4,"Tax"):tax;

        hashMap.put(tradePrice, new ArrayList<>(Collections.singleton(tradePrice)));
        hashMap.put(discount, new ArrayList<>(Collections.singleton(discount)));
        hashMap.put(promos, new ArrayList<>(Collections.singleton(promos)));
        hashMap.put(tax, new ArrayList<>(Collections.singleton(tax)));

        return hashMap;
    }


}
