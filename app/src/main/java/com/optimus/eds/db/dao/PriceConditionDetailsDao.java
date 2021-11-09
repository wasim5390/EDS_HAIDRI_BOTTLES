package com.optimus.eds.db.dao;

import com.optimus.eds.db.entities.pricing_models.PriceConditionDetailsWithScale;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;
import io.reactivex.Maybe;

@Dao
public interface PriceConditionDetailsDao extends PricingDao {

    @Query("SELECT * FROM PriceConditionDetail Where priceConditionId=:conditionId AND outletId=:outletId")
    @Transaction
    Maybe<PriceConditionDetailsWithScale> findPriceConditionDetails(int conditionId, int outletId);

    @Query("SELECT * FROM PriceConditionDetail Where priceConditionId=:conditionId AND routeId=:routeId")
    @Transaction
    Maybe<PriceConditionDetailsWithScale> findPriceConditionDetailsRoute(int conditionId, int routeId);

    @Query("SELECT * FROM PriceConditionDetail Where priceConditionId=:conditionId AND distributionId=:distributionId")
    @Transaction
    Maybe<PriceConditionDetailsWithScale> findPriceConditionDetailsDistribution(int conditionId, int distributionId);


    @Query("SELECT * FROM PriceConditionDetail Where priceConditionId=:conditionId AND productDefinitionId=:productDefinitionId")
    @Transaction
    Maybe<PriceConditionDetailsWithScale> findPriceConditionDetail(int conditionId, int productDefinitionId);

    @Query("SELECT * FROM PriceConditionDetail Where priceConditionId=:conditionId AND (productDefinitionId=:productDefinitionId" +
            " OR packageId = :packageId) AND ((:bundleId NOT Null AND (bundleId =:bundleId OR bundleId=0)) OR (:bundleId is Null AND bundleId is Null)) ")
    @Transaction
    Maybe<PriceConditionDetailsWithScale> findPriceConditionDetailWithBundle(int conditionId, int productDefinitionId,Integer bundleId , Long packageId);



}
