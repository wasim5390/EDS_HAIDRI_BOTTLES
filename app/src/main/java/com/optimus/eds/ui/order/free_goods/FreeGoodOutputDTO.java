package com.optimus.eds.ui.order.free_goods;
import com.optimus.eds.ui.order.pricing.Message;

import java.util.List;

public class FreeGoodOutputDTO {

    public int FreeGoodGroupId ;
    public Integer FreeGoodDetailId ;
    public Integer FreeGoodExclusiveId;
    public int ProductId;
    public String ProductName;
    public String ProductCode;
    public int ProductDefinitionId;
    public String ProductSize;
    public boolean IsDefault;
    public String DefinitionCode;
    public int StockInHand;
    public int MaximumFreeGoodQuantity;
    public int FreeGoodQuantity;
    public int FreeGoodTypeId;  //Inclusive=1/Exclusive=2
    public int FinalFreeGoodsQuantity;  //FreeGoodsQuantity If StockInHand > FreeGoodsQuantity ELSE StockInHand OR MaxQuantity
    public int QualifiedFreeGoodQuantity; //FreeGood quantity which the order deserve.
    //list of message
    public List<Message> Messages;
    public int ParentId ;
    public int ForEachQuantity;
    public boolean IsBundle;
    public int FreeQuantityTypeId; //Primary, Optional

}
