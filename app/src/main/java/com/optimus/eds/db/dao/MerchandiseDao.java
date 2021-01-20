package com.optimus.eds.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.db.entities.Merchandise;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import retrofit2.http.DELETE;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Created By apple on 4/23/19
 */

@Dao
public interface MerchandiseDao {


    @Query("SELECT * FROM Merchandise WHERE outletId=:outletId")
    Maybe<Merchandise> findMerchandiseByOutletId(Long outletId);


    @Insert(onConflict = REPLACE)
    void insertMerchandise(Merchandise merchandise);


    @Query("SELECT * FROM Asset WHERE outletId=:outletId")
    Single<List<Asset>> findAllAssetsForOutlet(Long outletId);

    @Update
    int updateMerchandise(Merchandise merchandise);

    @Update
    void updateMerchandise(List<Merchandise> merchandises);

    @Update
    void updateAsset(Asset asset);

    @Update
    void updateAssets(List<Asset> assets);


    @Query("DELETE FROM Asset")
    void deleteAllAssets();

    @Query("DELETE FROM Promotion")
    void deleteAllPromotion();

    @Query("DELETE FROM LookUp")
    void deleteAllLookUp();

    @Query("DELETE FROM Merchandise")
    void deleteAllMerchandise();

    @Query("DELETE FROM Merchandise where outletId=:merchandiseId")
    void deleteMerchandise(Long merchandiseId);

}
