package com.optimus.eds.db.dao;

import com.optimus.eds.db.entities.pricing.PriceConditionEntities;

import androidx.room.Dao;
import androidx.room.Query;
import io.reactivex.Maybe;

@Dao
public interface PriceConditionEntitiesDao extends PriceConditionDetailsDao {

    @Query("SELECT * FROM PriceConditionEntities Where priceConditionId=:conditionId AND outletId=:outletId AND ((bundleId NOT Null AND bundleId =:bundleId )or  bundleId is NUll OR bundleId=0)")
    Maybe<PriceConditionEntities> findPriceConditionEntityOutlet(int conditionId,int outletId, Integer bundleId);

    @Query("SELECT * FROM PriceConditionEntities Where priceConditionId=:conditionId AND outletId=:outletId ")
    Maybe<PriceConditionEntities> findPriceConditionEntityOutlet(int conditionId,int outletId);

    @Query("SELECT * FROM PriceConditionEntities Where priceConditionId=:conditionId AND routeId=:routeId ")
    Maybe<PriceConditionEntities> findPriceConditionEntityRoute(int conditionId,int routeId);


    @Query("SELECT * FROM PriceConditionEntities Where priceConditionId=:conditionId AND routeId=:routeId AND ((bundleId NOT Null AND bundleId =:bundleId )or  bundleId is NUll OR bundleId=0)")
    Maybe<PriceConditionEntities> findPriceConditionEntityRoute(int conditionId,int routeId , Integer bundleId);

    @Query("SELECT * FROM PriceConditionEntities Where priceConditionId=:conditionId AND distributionId=:distribution")
    Maybe<PriceConditionEntities> findPriceConditionEntityDistribution(int conditionId,int distribution);

    @Query("SELECT * FROM PriceConditionEntities Where priceConditionId=:conditionId AND distributionId=:distribution AND ((bundleId NOT Null AND bundleId =:bundleId )or  bundleId is NUll OR bundleId=0)")
    Maybe<PriceConditionEntities> findPriceConditionEntityDistribution(int conditionId,int distribution ,  Integer bundleId);

    @Query("DELETE FROM PriceConditionEntities")
    void deleteAllPriceConditionEntities();



}
