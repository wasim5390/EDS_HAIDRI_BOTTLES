package com.optimus.eds.ui.cash_memo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
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
        List<CartonPriceBreakDown> cartonPriceBreakDowns = orderDetail.getCartonPriceBreakDown();
        List<UnitPriceBreakDown> unitPriceBreakDowns = orderDetail.getUnitPriceBreakDown();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        HashMap<BreakDownKey, List<Object>> listHashMap = calculate(cartonPriceBreakDowns, unitPriceBreakDowns);

        // Sort method needs a List, so let's first convert HashList to List in Java
        List<Map.Entry<BreakDownKey,List<Object>>> listOfEntries = new ArrayList<>(listHashMap.entrySet());

        Collections.sort(listOfEntries, (o1, o2) -> o1.getKey().getConditionOrder().compareTo(o2.getKey().conditionOrder));

        for (Map.Entry<BreakDownKey, List<Object>> entry : listOfEntries) {

            LinearLayout rateView = (LinearLayout) inflater.inflate(R.layout.rate_child_layout, null);
            TextView title = rateView.findViewById(R.id.productRate);
            TextView rate = rateView.findViewById(R.id.tvProductRate);
            Double unitPrice = 0.0, cartonPrice = 0.0;
            String type = "";


            for (Object breakDown : entry.getValue()) {

                if (breakDown instanceof CartonPriceBreakDown) {
                    cartonPrice = ((CartonPriceBreakDown) breakDown).getBlockPrice();
                    type = ((CartonPriceBreakDown) breakDown).getPriceConditionType();
                } else {
                    unitPrice = ((UnitPriceBreakDown) breakDown).getBlockPrice();
                    type = ((UnitPriceBreakDown) breakDown).getPriceConditionType();
                }
            }
            rate.setText(formatCurrency(cartonPrice.doubleValue()) + " / " + formatCurrency(unitPrice.doubleValue()));
            title.setText(type);
            this.addView(rateView);
        }

    }


    public HashMap<BreakDownKey,List<Object>> calculate(List<CartonPriceBreakDown> cartonPriceBreakDownList,List<UnitPriceBreakDown> unitPriceBreakDownList){

        List<Object> breakDowns = new ArrayList<>();
        breakDowns.addAll(cartonPriceBreakDownList==null?new ArrayList<>():cartonPriceBreakDownList);
        breakDowns.addAll(unitPriceBreakDownList==null?new ArrayList<>():unitPriceBreakDownList);

        HashMap<BreakDownKey, List<Object>> hashMap = new HashMap<>();
        for (Object breakDown:breakDowns){
            BreakDownKey key;
            if(breakDown instanceof CartonPriceBreakDown) {
                key = new BreakDownKey(((CartonPriceBreakDown) breakDown).getPriceConditionId(),((CartonPriceBreakDown) breakDown).getPriceConditionClassOrder());
            }
            else {
                key = new BreakDownKey(((UnitPriceBreakDown) breakDown).getPriceConditionId(),((UnitPriceBreakDown) breakDown).getPriceConditionClassOrder());
            }

            if (!hashMap.containsKey(key)) {
                List<Object> list = new ArrayList<>();
                list.add(breakDown);
                hashMap.put(key, list);
            } else {
                hashMap.get(key).add(breakDown);
            }
        }
        return hashMap;
    }

    public class BreakDownKey{

        int priceConditionId;
        int conditionOrder;
        public BreakDownKey(int priceConditionId, int conditionOrder) {
            this.priceConditionId = priceConditionId;
            this.conditionOrder = conditionOrder;
            if(conditionOrder<1)
                this.conditionOrder = priceConditionId;
        }

        public Integer getConditionOrder() {
            return conditionOrder;
        }

        //Depends only on account number
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + priceConditionId;
            return result;
        }

        //Compare only account numbers
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            BreakDownKey other = (BreakDownKey) obj;
            if (priceConditionId != other.priceConditionId)
                return false;
            return true;
        }
    }

}
