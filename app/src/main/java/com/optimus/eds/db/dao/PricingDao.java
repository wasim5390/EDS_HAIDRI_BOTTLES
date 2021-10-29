package com.optimus.eds.db.dao;


import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceBundle;
import com.optimus.eds.db.entities.pricing.PriceCondition;
import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionEntities;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;
import com.optimus.eds.db.entities.pricing.PriceConditionType;
import com.optimus.eds.db.entities.pricing.PricingArea;
import com.optimus.eds.db.entities.pricing_models.PcClassWithPcType;
import com.optimus.eds.ui.order.pricing.PriceConditionWithAccessSequence;
import com.optimus.eds.ui.order.pricing.ProductQuantity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PricingDao {
    @Query("SELECT * FROM PriceConditionClass Where PriceConditionClass.pricingLevelId=1 Order By `order`")
    @Transaction
    Single<List<PcClassWithPcType>> findPriceConditionClassWithTypes();

    @Query("SELECT * FROM PriceConditionClass Where PriceConditionClass.pricingLevelId=:pricingLevel Order By `order`")
    Single<List<PriceConditionClass>> findPriceConditionClasses(int pricingLevel);


    @Query("SELECT * FROM PricingArea Order By `order`")
    Single<List<PricingArea>> findPricingArea();

    @Query("SELECT * FROM PriceConditionType Where priceConditionClassId=:priceConditionClassId")
    Single<List<PriceConditionType>> findPriceConditionTypes(int priceConditionClassId);

    @Query("SELECT * from PriceAccessSequence pc order by pc.`order` " )
    Single<List<PriceAccessSequence>> getAccessSequence();


    @Query("SELECT * from PriceCondition " +
            "INNER JOIN PriceAccessSequence pas on PriceCondition.accessSequenceId=pas.priceAccessSequenceId\n" +
            "Where PriceCondition.priceConditionTypeId=:priceConditionTypeId order by pas.`order`")
    Single<List<PriceConditionWithAccessSequence>> getPriceConditionAndAccessSequenceByTypeId(int priceConditionTypeId);

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
    void insertPriceCondition(List<PriceCondition> priceConditionType);

    @Query("DELETE FROM PriceConditionClass")
    void deleteAllPriceConditionClasses();

    @Query("DELETE FROM Bundle")
    void deleteAllPriceBundles();

    @Query("DELETE FROM PricingArea")
    void deleteAllPricingAreas();

    @Query("DELETE FROM ProductQuantity")
    void deleteAllTempQty();


//    Select	@OutletId AS OutletId,
//    @PriceConditionId AS PriceConditionId,
//    @PriceConditionDetailId AS PriceConditionDetailId,
//    @ProductDefinitionId AS ProductDefinitionId,
//    OAP.productId AS ProductId,
//    OAP.packageId AS PackageId,
//    Sum (OAP.Amount) AS Amount ,
//    Sum(OAP.Quantity) AS Quantity
//
//
//    From	OutletAvailedPromotion OAP
//    INNER JOIN PriceCondition PC ON PC.PriceConditionId = OAP.PriceConditionId
//    INNER JOIN PriceConditionType PCT ON PCT.PriceConditionTypeId = PC.priceConditionTypeId
//    LEFT JOIN Product P ON P.pk_pid = OAP.productId
//    Where	OAP.OutletId =  @OutletId AND
//    OAP.PriceConditionId = @PriceConditionId AND
//            (
//				(PCT.PCDefinitionLevelId = 2 AND (OAP.packageId = P.pkgId OR PC.CombinedLimitBy IS NOT NULL)) OR
//            (OAP.PriceConditionDetailId = @PriceConditionDetailId)
//			)
}
