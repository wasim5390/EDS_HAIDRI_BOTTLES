package com.optimus.eds.db.dao;


import com.optimus.eds.db.entities.OutletAvailedPromotion;
import com.optimus.eds.db.entities.PriceValidation;
import com.optimus.eds.db.entities.pricing.FreeGoodDetails;
import com.optimus.eds.db.entities.pricing.FreeGoodEntityDetails;
import com.optimus.eds.db.entities.pricing.FreeGoodExclusives;
import com.optimus.eds.db.entities.pricing.FreeGoodGroups;
import com.optimus.eds.db.entities.pricing.FreeGoodMasters;
import com.optimus.eds.db.entities.pricing.FreePriceConditionOutletAttributes;
import com.optimus.eds.db.entities.pricing.OutletAvailedFreeGoods;
import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceBundle;
import com.optimus.eds.db.entities.pricing.PriceCondition;
import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionEntities;
import com.optimus.eds.db.entities.pricing.PriceConditionOutletAttribute;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;
import com.optimus.eds.db.entities.pricing.PriceConditionType;
import com.optimus.eds.db.entities.pricing.PricingArea;
import com.optimus.eds.db.entities.pricing_models.PcClassWithPcType;
import com.optimus.eds.ui.order.free_goods.FreeGoodOutputDTO;
import com.optimus.eds.ui.order.pricing.PriceConditionWithAccessSequence;
import com.optimus.eds.ui.order.pricing.ProductQuantity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import io.reactivex.Maybe;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PricingDao {
    @Query("SELECT * FROM PriceConditionClass Where PriceConditionClass.pricingLevelId=1 Order By `order`")
    @Transaction
    Single<List<PcClassWithPcType>> findPriceConditionClassWithTypes();

    @Query("SELECT * FROM PriceConditionClass Where PriceConditionClass.pricingLevelId=:pricingLevel Order By `order`")
    Single<List<PriceConditionClass>> findPriceConditionClasses(int pricingLevel);

    @Query("Select Count(*) from PriceConditionClass")
    Single<Integer> priceConditionClassValidation();

    @Query("Select Count(*) from PriceCondition")
    Single<Integer> priceConditionValidation();

    @Query("Select Count(*) from PriceConditionType")
    Single<Integer> priceConditionTypeValidation();

    @Query("SELECT * FROM PricingArea Order By `order`")
    Single<List<PricingArea>> findPricingArea();

    @Query("SELECT * FROM PriceConditionType Where priceConditionClassId=:priceConditionClassId")
    Single<List<PriceConditionType>> findPriceConditionTypes(int priceConditionClassId);

    @Query("SELECT * from PriceAccessSequence pc order by pc.`order` " )
    Single<List<PriceAccessSequence>> getAccessSequence();

    @Query("SELECT * from PriceAccessSequence pc where pricingTypeId = 1 order by `order`" )
    Maybe<List<PriceAccessSequence>> getAccessSequenceByTypeId();


    //    @Query("SELECT * from PriceCondition " +
//            "INNER JOIN PriceAccessSequence pas on PriceCondition.accessSequenceId=pas.priceAccessSequenceId\n" +
//            "Where PriceCondition.priceConditionTypeId=:priceConditionTypeId order by pas.`order`")
//    Single<List<PriceConditionWithAccessSequence>> getPriceConditionAndAccessSequenceByTypeId(int priceConditionTypeId);
    @Query("SELECT PC.*,pas.* , ChannelAttribute.ChannelAttributeCount , OutletChannelAttribute.ChannelAttributeCount AS OutletChannelAttribute , " +
            "GroupAttribute.GroupAttributeCount , OutletGroupAttribute.GroupAttributeCount AS OutletGroupAttribute, "+
            "VPOClassificationAttribute.VPOClassificationAttributeCount , OutletVPOClassificationAttribute.VPOClassificationAttributeCount AS OutletVPOClassificationAttributeCount "+
            "FROM PriceCondition PC\n" +
            "INNER JOIN PriceConditionType PCT ON PCT.priceConditionTypeId=PC.priceConditionTypeId " +
            "INNER JOIN PriceConditionClass pcc ON pcc.priceConditionClassId=pct.priceConditionClassId "+
            "INNER JOIN PriceAccessSequence pas ON pas.priceAccessSequenceId=pc.accessSequenceId "+
            "LEFT JOIN (\n" +
            "\t\t\t\t\t\t\tSELECT\tCount(pcoa.ChannelId) AS ChannelAttributeCount,pcoa.PriceConditionId\n" +
            "\t\t\t\t\t\t\tFROM\tPriceConditionOutletAttribute pcoa\n" +
            "\t\t\t\t\t\t\tGroup By pcoa.PriceConditionId\n" +
            "\t\t\t\t\t\t) ChannelAttribute ON ChannelAttribute.PriceConditionId = PC.PriceConditionId \n" +

            "LEFT JOIN (\n" +
            "\t\t\t\t\t\t\tSELECT\tCount(pcoa.ChannelId) AS ChannelAttributeCount,pcoa.PriceConditionId\n" +
            "\t\t\t\t\t\t\tFROM\tPriceConditionOutletAttribute pcoa\n" +
            "\t\t\t\t\t\t\tWhere  pcoa.ChannelId = :ChannelId\n" +
            "\t\t\t\t\t\t\tGroup By pcoa.PriceConditionId\n" +
            "\t\t\t\t\t\t) OutletChannelAttribute ON ChannelAttribute.PriceConditionId = PC.PriceConditionId \n" +

            "LEFT JOIN (" +
            "         SELECT\tCount(pcoa.OutletGroupId) AS GroupAttributeCount, pcoa.PriceConditionId" +
            "         FROM\tPriceConditionOutletAttribute pcoa\n" +
            "         Group By pcoa.PriceConditionId\n" +
            "         ) GroupAttribute ON GroupAttribute.PriceConditionId = PC.PriceConditionId\n" +
            "LEFT JOIN (" +
            "         SELECT Count(pcoa.OutletGroupId) AS GroupAttributeCount, pcoa.PriceConditionId" +
            "         FROM PriceConditionOutletAttribute pcoa\n" +
            "         Where pcoa.OutletGroupId = :PricingGroupId\n" +
            "         Group By pcoa.PriceConditionId\n" +
            "         ) OutletGroupAttribute ON OutletGroupAttribute.PriceConditionId = PC.PriceConditionId\n" +

            "LEFT JOIN (\n" +
            "                                \tSELECT\tCount(pcoa.VPOClassificationId) AS VPOClassificationAttributeCount, pcoa.PriceConditionId\n" +
            "                                \tFROM\tPriceConditionOutletAttribute pcoa\n" +
            "                                \tGroup By pcoa.PriceConditionId\n" +
            "                                \t)\tVPOClassificationAttribute ON VPOClassificationAttribute.PriceConditionId = PC.PriceConditionId\n" +
            "        " +

            "LEFT JOIN (\n" +
            "                                \tSELECT\tCount(pcoa.VPOClassificationId) AS VPOClassificationAttributeCount, pcoa.PriceConditionId\n" +
            "                                \tFROM\tPriceConditionOutletAttribute pcoa\n" +
            "                                \tWhere\tpcoa.VPOClassificationId = :VPOClassificationId\n" +
            "                                \tGroup By pcoa.PriceConditionId\n" +
            "                                 ) OutletVPOClassificationAttribute ON OutletVPOClassificationAttribute.PriceConditionId = PC.PriceConditionId\n" +
            "        " +
            "        " +
            "Where PC.priceConditionTypeId = :priceConditionTypeId  AND\n" +
            "  (PC.IsBundle = 0 OR PC.IsBundle IS NULL)\n" +
            "\t\t\tAND (:OrganizationId = 0 OR :OrganizationId IS NULL OR PC.OrganizationId = :OrganizationId)\n" +
            "\t\t\tAND (\n" +
            "\t\t\t\t\t(PCC.Code IS NULL OR PCC.Code  <> 'Tax') OR\n" +
            "\t\t\t\t\t(pc.CustomerRegistrationTypeId = 3) OR\n" +
            "\t\t\t\t\t(PCC.Code = 'Tax' AND pc.CustomerRegistrationTypeId = :CustomerRegistrationTypeId)\n" +
            "\t\t\t\t)\n" +
            "\t\t\tAND (ChannelAttribute.ChannelAttributeCount > 0  OR (\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tSELECT\t\tCount(pcoa1.ChannelId)\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tFROM\t\tPriceConditionOutletAttribute pcoa1\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tWHERE\t\tpcoa1.PriceConditionId = PC.PriceConditionId\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t) = 0\t\t\n" +
            "\t\t\t\t )\n" +
            "\t\t\tAND (GroupAttribute.GroupAttributeCount > 0  OR\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t(\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t SELECT\t\tCount(pcoa1.OutletGroupId)\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t FROM\t\tPriceConditionOutletAttribute pcoa1\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t WHERE\t\tpcoa1.PriceConditionId = PC.PriceConditionId \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t ) = 0\t\t\n" +
            "\t\t\t\t )\n" +
            "\t\t\tAND  (VPOClassificationAttribute.VPOClassificationAttributeCount > 0  OR\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t(\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t SELECT\t\tCount(pcoa1.VPOClassificationId)\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t FROM\t\tPriceConditionOutletAttribute pcoa1\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t WHERE\t\tpcoa1.PriceConditionId = PC.PriceConditionId \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t ) = 0\n" +
            "\t\t\t\t )\n" +
            "\t\t\tAND (\n" +
            "\t\t\t        (:OutletPromoConfigId IS NULL OR :OutletPromoConfigId=2) \n" +
            "\t\t\t\t\tOR (:OutletPromoConfigId=1 AND pct.IsPromo = 0) " +
            "\t\t\t)\n" +
            "order by pas.`order`")
    //--Outlet Is Promo
    //Or Outlet Is No Promo and Pricing is also no promo
    Single<List<PriceConditionWithAccessSequence>> getPriceConditionAndAccessSequenceByTypeId(int priceConditionTypeId , int VPOClassificationId ,
                                                                                              int PricingGroupId , int ChannelId , int OrganizationId,
                                                                                              int OutletPromoConfigId , int CustomerRegistrationTypeId);

    @Query("SELECT * from PriceCondition pc " +
            "INNER JOIN PriceAccessSequence pas ON pc.accessSequenceId=pas.priceAccessSequenceId\n" +
            "INNER JOIN Bundle b ON b.PriceConditionId = pc.PriceConditionId  \n"+
            "Where pc.priceConditionTypeId=:priceConditionTypeId AND b.bundleId IN (:applyingBundleIds) order by pas.`order` "  )
    Single<List<PriceConditionWithAccessSequence>> getPriceConditionAndAccessSequenceByTypeIdWithBundle(int priceConditionTypeId,List<Integer> applyingBundleIds);

    @Query("SELECT DISTINCT b.bundleId FROM  PriceCondition pc" +
            "   INNER JOIN Bundle b ON pc.priceConditionId = b.priceConditionId " +
            "   INNER JOIN PriceConditionDetail pcd ON b.bundleId = pcd.bundleId " +
            "   AND pcd.productDefinitionId=:productDefinitionId " +
            "  WHERE  pc.priceConditionTypeId =:conditionTypeId " +
            "  AND pc.isBundle = 1 ")
    Single<List<Integer>> getBundleIdsForConditionType(int productDefinitionId, int conditionTypeId);

    @Query("SELECT  bundleMinimumQuantity  FROM Bundle WHERE BundleId =:bundleId")
    Single<Integer> getBundleMinQty(int bundleId);

    @Query(" SELECT COUNT(priceConditionDetailId) FROM PriceConditionDetail" +
            "  WHERE bundleId =:bundleId")
    Single<Integer> getBundleProductCount(int bundleId);

    @Query("SELECT COUNT(pcd.PriceConditionDetailId)" +
            "  FROM PriceConditionDetail pcd" +
            "    INNER JOIN ProductQuantity pl " +
            "    ON pl.ProductDefinitionId = pcd.productDefinitionId" +
            "    AND pl.Quantity >= ifNull(pcd.minimumQuantity,1)" +
            "  WHERE pcd.bundleId =:bundleId")
    Single<Integer> getCalculatedBundleProdCount(int bundleId);

    @Query("SELECT SUM(pl.Quantity)" +
            "  FROM PriceConditionDetail pcd" +
            "    INNER JOIN ProductQuantity pl " +
            "    ON pl.ProductDefinitionId = pcd.productDefinitionId" +
            "    AND pl.Quantity >= ifNull(pcd.minimumQuantity,1)" +
            "  WHERE pcd.bundleId =:bundleId")
    Single<Integer> getBundleProdTotalQty(int bundleId);

    @Insert(onConflict = REPLACE)
    void insertPriceConditionClasses(List<PriceConditionClass> priceConditionClasses);

    @Insert(onConflict = REPLACE)
    void insertPriceConditionType(List<PriceConditionType> priceConditionTypes);

    @Insert(onConflict = REPLACE)
    void insertTempOrderQty(List<ProductQuantity> productQuantityList);

    @Insert(onConflict = REPLACE)
    void insertPriceConditionDetail(List<PriceConditionDetail> priceConditionDetails);

    @Insert(onConflict = REPLACE)
    void insertPriceBundles(List<PriceBundle> priceBundles);

    @Insert(onConflict = REPLACE)
    void insertPriceConditionEntities(List<PriceConditionEntities> priceConditionEntities);

    @Insert(onConflict = REPLACE)
    void insertAvailedAMount(List<PriceConditionEntities> priceConditionEntities);

    @Insert(onConflict = REPLACE)
    void insertPriceAccessSequence(List<PriceAccessSequence> priceAccessSequences);

    @Insert(onConflict = REPLACE)
    void insertPriceConditionScales(List<PriceConditionScale> scales);

    @Insert(onConflict = REPLACE)
    void insertPriceConditionOutletAttributes(List<PriceConditionOutletAttribute> priceConditionOutletAttributes);

    @Insert(onConflict = REPLACE)
    void insertPriceCondition(List<PriceCondition> priceConditionType);

    @Insert(onConflict = REPLACE)
    void insertFreeGoodMasters(List<FreeGoodMasters> freeGoodMasters);

    @Insert(onConflict = REPLACE)
    void insertFreeGoodGroups(List<FreeGoodGroups> freeGoodGroups);

    @Insert(onConflict = REPLACE)
    void insertFreePriceConditionOutletAttributes(List<FreePriceConditionOutletAttributes> priceConditionOutletAttributes);

    @Insert(onConflict = REPLACE)
    void insertFreeGoodDetails(List<FreeGoodDetails> freeGoodDetails);

    @Insert(onConflict = REPLACE)
    void insertFreeGoodExclusives(List<FreeGoodExclusives> freeGoodExclusives);

    @Insert(onConflict = REPLACE)
    void insertFreeGoodEntityDetails(List<FreeGoodEntityDetails> freeGoodEntityDetails);

    @Insert(onConflict = REPLACE)
    void insertOutletAvailedFreeGoods(List<OutletAvailedFreeGoods> outletAvailedFreeGoods);


    @Query("DELETE FROM PriceConditionClass")
    void deleteAllPriceConditionClasses();

    @Query("DELETE FROM Bundle")
    void deleteAllPriceBundles();

    @Query("DELETE FROM PricingArea")
    void deleteAllPricingAreas();

    @Query("DELETE FROM ProductQuantity")
    void deleteAllTempQty();

    @Query("DELETE FROM PriceConditionType")
    void deletePriceConditionTypes();

    @Query("DELETE FROM PriceAccessSequence")
    void deletePriceAccessSequence();

    @Query("DELETE FROM PriceCondition")
    void deletePriceCondition();

    @Query("DELETE FROM PriceConditionDetail")
    void deletePriceConditionDetail();


    @Query("DELETE FROM PriceConditionEntities")
    void deletePriceConditionEntity();


    @Query("DELETE FROM PriceConditionScale")
    void deletePriceConditionScale();


    @Query("DELETE FROM PriceConditionOutletAttribute")
    void deletePriceConditionOutletAttribute();


    @Query("DELETE FROM FreeGoodMasters")
    void deleteFreeGoodMasters();

    @Query("DELETE FROM FreeGoodGroups")
    void deleteFreeGoodGroups();

    @Query("DELETE FROM FreePriceConditionOutletAttributes")
    void deleteFreePriceConditionOutletAttribute();

    @Query("DELETE FROM FreeGoodDetails")
    void deleteFreeGoodDetails();

    @Query("DELETE FROM FreeGoodExclusives")
    void deleteFreeGoodExclusives();

    @Query("DELETE FROM FreeGoodEntityDetails")
    void deleteFreeGoodEntityDetails();

    @Query("DELETE FROM OutletAvailedFreeGoods")
    void deleteOutletAvailedFreeGoods();

    @Query("Select * from FreeGoodDetails where freeGoodGroupId = :freeGoodGroupId")
    Maybe<List<FreeGoodOutputDTO>> getFreeGoodGroupDetails(Integer freeGoodGroupId);

    @Query("Select productDefinitionId from FreeGoodDetails where freeGoodGroupId = :freeGoodGroupId")
    Maybe<List<Integer>> getPromoBaseProduct(Integer freeGoodGroupId);

    @Query("Select *, quantity AS freeGoodQuantity from FreeGoodExclusives where freeGoodGroupId = :freeGoodGroupId")
    Maybe<List<FreeGoodOutputDTO>> getFreeGoodExclusiveDetails(Integer freeGoodGroupId);

    @Query("Select SUM(quantity) from OutletAvailedFreeGoods where freeGoodGroupId = :freeGoodGroupId AND freeGoodDetailId = :freeGoodDetailId AND outletId = :outletId AND freeGoodExclusiveId = :freeGoodExclusiveId")
    Maybe<Integer> getAlreadyAvailedFreeGoods(Integer freeGoodGroupId , Integer freeGoodDetailId , Integer freeGoodExclusiveId , Integer outletId );


    @Query("SELECT    DISTINCT fgg.id , fgg.minimumQuantity, fgg.typeId, --fgm.IsBundle,\n" +
            "                        fgg.freeQuantityTypeId, fgg.forEachQuantity , fgg.freeGoodMasterId,\n" +
            "                        outletChannelAttributeCount.ChannelAttributeCount AS outletChannelAttributeCount ,\n" +
            "                        Count (channnelAttributeCount.channelId) AS channelAttributeCount,\n" +
            "                        outletGroupAttributeCount.GroupAttributeCount as outletGroupAttributeCount ,\n" +
            "                         Count (groupAttributeCount.outletGroupId) AS groupAttributeCount,\n" +
            "                         outletVPOAttributeCount.VPOAttributeCount AS outletVPOAttributeCount,\n" +
            "                         Count (VPOAttributeCount.vpoClassificationId) AS vpoAttributeCount\n" +
            "        FROM      FreeGoodGroups fgg\n" +
            "                 INNER JOIN  FreeGoodMasters fgm ON    fgg.freeGoodMasterId = fgm.freeGoodMasterId AND fgm.isActive = 1 AND fgm.accessSequenceId=:AccessSequenceId\n" +
            "                 INNER JOIN  FreeGoodDetails fgd ON    fgd.freeGoodGroupId = fgg.id AND fgd.isActive = 1\n" +
            "                 LEFT JOIN   FreeGoodEntityDetails fged ON fged.freeGoodMasterId = fgm.freeGoodMasterId\n" +
            "                 INNER JOIN  Outlet O ON O.mOutletId =:OutletIdToCheckAttribute\n" +
            "                 --LEFT JOIN FreePriceConditionOutletAttributes  outletChannelAttributeCount ON outletChannelAttributeCount.channelId=O.channelId\n" +
            "                 LEFT JOIN (SELECT Count(fpcoa.channelId) AS ChannelAttributeCount, fpcoa.freeGoodId  FROM FreePriceConditionOutletAttributes  fpcoa Where fpcoa.channelId=:ChannelId Group By fpcoa.freeGoodId)  outletChannelAttributeCount ON outletChannelAttributeCount.freeGoodId=fgm.freeGoodMasterId                  \n" +
            "                 LEFT JOIN FreePriceConditionOutletAttributes  channnelAttributeCount ON channnelAttributeCount.channelId IS NOT NULL\n" +
            "                 LEFT JOIN (SELECT Count(fpcoa.outletGroupId) AS GroupAttributeCount, fpcoa.freeGoodId  FROM FreePriceConditionOutletAttributes  fpcoa Where fpcoa.outletGroupId=:PricingGroupId Group By fpcoa.freeGoodId)  outletGroupAttributeCount ON outletGroupAttributeCount.freeGoodId=fgm.freeGoodMasterId\n" +
            "                 LEFT JOIN FreePriceConditionOutletAttributes  groupAttributeCount ON groupAttributeCount.outletGroupId IS NOT NULL\n" +
            "                 --LEFT JOIN FreePriceConditionOutletAttributes  outletVPOAttributeCount ON outletVPOAttributeCount.vpoClassificationId=O.vpoClassificationId\n" +
            "                 \n" +
            "                 LEFT JOIN (SELECT Count(fpcoa.vpoClassificationId) AS VPOAttributeCount, fpcoa.freeGoodId  FROM FreePriceConditionOutletAttributes  fpcoa Where fpcoa.vpoClassificationId=:VpoClassificationId Group By fpcoa.freeGoodId)  outletVPOAttributeCount ON outletVPOAttributeCount.freeGoodId=fgm.freeGoodMasterId\n" +
            "               \n" +
            "                 LEFT JOIN FreePriceConditionOutletAttributes  VPOAttributeCount ON VPOAttributeCount.outletGroupId IS NOT NULL\n" +
            "        WHERE     fgd.ProductDefinitionId=:ProductDefinitionId\n" +
            "                 AND (\n" +
            "                       --(@AccessSequenceId = @GlobalAccessSequenc AND fged.FreeGoodEntityDetailId IS NULL)\n" +
            "                       (:AccessSequenceId = 21 AND fged.freeGoodEntityDetailId IS NULL)\n" +
            "                    OR  (\n" +
            "                       fged.freeGoodEntityDetailId IS NOT NULL And    (fged.outletId = :OutletId OR fged.routeId = :RouteId OR fged.channelId = O.channelId OR fged.distributionId = :DistributionId)\n" +
            "                       )\n" +
            "                    )\n" +
            "                 AND    fgg.isActive = 1 AND fgg.isDeleted=0\n" +
            "                 --AND fgm.ValidFrom <= @OrderDate AND  fgm.ValidTo >= @OrderDate;\n" +
            "                 AND O.outletPromoConfigId<>1")
    Maybe<List<FreeGoodGroups>> appliedFreeGoodGroups(Integer OutletId , Integer ChannelId , Integer VpoClassificationId , Integer PricingGroupId , Integer RouteId, Integer DistributionId , Integer ProductDefinitionId , Integer AccessSequenceId , Integer OutletIdToCheckAttribute);


    @Query("Select COUNT(fgd.freeGoodDetailId) from FreeGoodDetails fgd where fgd.freeGoodGroupId = :freeGoodGroupId")
    Maybe<Integer> getFreeGoodDetailCount(Integer freeGoodGroupId);

    @Query("Select * from FreeGoodDetails fgd where fgd.freeGoodGroupId = :freeGoodGroupId")
    Maybe<List<FreeGoodDetails>> getFreeGoodDetail(Integer freeGoodGroupId);


    @Query("Select fgg.maximumQuantity from FreeGoodGroups fgg where fgg.id = :freeGoodGroupId")
    Maybe<Integer> getFreeGoodGroupMaxQuantity(Integer freeGoodGroupId);

    @Query(" Select\t:OutletId AS outletId,\n" +
            "    :PriceConditionId AS priceConditionId,\n" +
            "    :PriceConditionDetailId AS priceConditionDetailId,\n" +
            "    :ProductDefinitionId AS productDefinitionId,\n" +
            "    OAP.productId AS productId,\n" +
            "    OAP.packageId AS packageId,\n" +
            "    Sum (OAP.Amount) AS amount ,\n" +
            "    Sum(OAP.Quantity) AS quantity\n" +
            "\n" +
            "\n" +
            "    From\tOutletAvailedPromotion OAP\n" +
            "    INNER JOIN PriceCondition PC ON PC.PriceConditionId = OAP.PriceConditionId\n" +
            "    INNER JOIN PriceConditionType PCT ON PCT.PriceConditionTypeId = PC.priceConditionTypeId\n" +
            "    LEFT JOIN Product P ON P.pk_pid = OAP.productId\n" +
            "    Where\tOAP.OutletId =  :OutletId AND\n" +
            "    OAP.PriceConditionId = :PriceConditionId AND\n" +
            "            (\n" +
            "\t\t\t\t(PCT.PCDefinitionLevelId = 2 AND (OAP.packageId = P.pkgId OR PC.CombinedLimitBy IS NOT NULL)) OR\n" +
            "            (OAP.PriceConditionDetailId = :PriceConditionDetailId)\n" +
            "\t\t\t)")
    Maybe<OutletAvailedPromotion> getAlreadyAvailedPromo(Integer OutletId , Integer PriceConditionId , Integer PriceConditionDetailId,
                                                         Integer ProductDefinitionId );
}
