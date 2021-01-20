package com.optimus.eds.ui.order;

import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.UnitPriceBreakDown;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class OrderManager {

    private static OrderManager instance;

    public static OrderManager instance(){
        if(instance ==null)
            instance = new OrderManager();
        return instance;
    }

    /**
     * Converts units to cartons and remaining items as units
     * @param productUnitsPerCarton
     * @param orderedUnits
     * @param orderedCartons
     * @return
     */
    public OrderQuantity calculateOrderQty(Integer productUnitsPerCarton, Integer orderedUnits, Integer orderedCartons){

        OrderQuantity orderQuantity = new OrderQuantity(orderedUnits,orderedCartons);
        if(orderedUnits!=null && productUnitsPerCarton!=null && orderedUnits>=productUnitsPerCarton  && productUnitsPerCarton>0) {
            if(orderedCartons==null) orderedCartons = 0;
            Integer quotient = orderedUnits / productUnitsPerCarton;
            Integer remainder = orderedUnits % productUnitsPerCarton;
            orderQuantity.setCarton(quotient+orderedCartons);
            orderQuantity.setUnits(remainder);
        }
        return orderQuantity;
    }

    /**
     * This calculates units in float carton size
     * @param productUnitsPerCarton
     * @param orderedUnits
     * @param orderedCartons
     * @return
     */
    public Float calculateQtyInCartons(Integer productUnitsPerCarton, Integer orderedUnits, Integer orderedCartons){
        Float qtyInCarton=0f+orderedCartons;

        if(orderedUnits!=null && productUnitsPerCarton!=null &&  productUnitsPerCarton>0) {
            if(orderedCartons==null) orderedCartons = 0;
            float quotient =(float) orderedUnits / productUnitsPerCarton;
            qtyInCarton =quotient+orderedCartons;
        }
        return qtyInCarton;
    }


    public static class OrderQuantity{

        public OrderQuantity(Integer units, Integer carton) {
            this.units = units;
            this.carton = carton;
        }

        public Integer getUnits() {
            return units;
        }

        public Integer getCarton() {
            return carton;
        }



        public void setUnits(Integer units) {
            this.units = units;
        }


        public void setCarton(Integer carton) {
            this.carton = carton;
        }

       private Integer units;
       private Integer carton;
    }



}
