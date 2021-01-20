package com.optimus.eds;

import com.optimus.eds.db.entities.Product;
import com.optimus.eds.source.JobIdManager;
import com.optimus.eds.ui.order.OrderManager;
import com.optimus.eds.utils.Util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void convertToDecimal()  {

        String doubleAsText = "1.0";
        if(doubleAsText.startsWith("."))
            doubleAsText = "0".concat(doubleAsText);
        int decimal;
        int fractional = 0;

        boolean isWhole = Util.isInt(Double.parseDouble(doubleAsText));
        if(isWhole) {
            decimal = Integer.parseInt(doubleAsText.split("\\.")[0]);
        }else {
            decimal = Integer.parseInt(doubleAsText.split("\\.")[0]);
            fractional = Integer.parseInt(doubleAsText.split("\\.")[1]);
        }

        assertEquals(decimal,1);
        assertEquals(fractional,0);

    }
    @Test
    public void calculateQuantity(){
        List<Product> alreadyAdded = new ArrayList<>();
        alreadyAdded.add(new Product(10L,1L,"abc"));
        alreadyAdded.add(new Product(11L,1L,"def"));
        alreadyAdded.add(new Product(12L,1L,"efg"));
        List<Product> newItems = new ArrayList<>();
        newItems.add(new Product(10L,1L,"ghi"));
        newItems.add(new Product(13L,1L,"qxy"));
        newItems.add(new Product(12L,1L,"dee"));
        List<Product> products = mergeProductsInLocalList(newItems,alreadyAdded);
        for(Product product:products)
        System.out.println("Item: "+product.getId() +" "+ product.getName());
    }



    @Test
    public void getJobId() {
        final int JOB_TYPE_SHIFTS = 30;
        @JobIdManager.JobType int jobType =1;
        int objectId = 99999999;

        if ( 0 < objectId && objectId < (1<< JOB_TYPE_SHIFTS) ) {
            int job= (jobType << JOB_TYPE_SHIFTS) + objectId;
            System.out.println(job);

        } else {
            String err = String.format("objectId %s must be between %s and %s",
                    objectId,0,(1<<JOB_TYPE_SHIFTS));
            throw new IllegalArgumentException(err);
        }
    }

    @Test
    public void convertQty(){

        Integer orderedUnits=13;
        Integer orderedCartons=1; Integer productUnitsPerCarton=12;
        OrderManager.OrderQuantity orderQuantity = new OrderManager.OrderQuantity(orderedUnits,orderedCartons);
        if(orderedUnits!=null && productUnitsPerCarton!=null && orderedUnits>=productUnitsPerCarton  && productUnitsPerCarton>0) {
            if(orderedCartons==null) orderedCartons = 0;
            Integer quotient = orderedUnits / productUnitsPerCarton;
            Integer remainder = orderedUnits % productUnitsPerCarton;
            orderQuantity.setCarton(quotient+orderedCartons);
            orderQuantity.setUnits(remainder);
        }
            System.out.println("Calculated QTY: "+orderQuantity.getCarton() +" :"+ orderQuantity.getUnits());
    }

    public List<Product> mergeProductsInLocalList(List<Product> products, List<Product> alreadyAddedProducts){
        List<Product> productsToRemove = new ArrayList<>();
        for(Product product:products){
            for(Product addedProduct: alreadyAddedProducts){
                if(product.getId()==addedProduct.getId()){
                    productsToRemove.add(addedProduct);
                }
            }
        }
        alreadyAddedProducts.removeAll(productsToRemove);
        alreadyAddedProducts.addAll(products);
        return alreadyAddedProducts;
    }



}