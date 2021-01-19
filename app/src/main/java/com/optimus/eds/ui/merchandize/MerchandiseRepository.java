package com.optimus.eds.ui.merchandize;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.MerchandiseDao;
import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.db.entities.Merchandise;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created By apple on 4/23/19
 */
public class MerchandiseRepository {

    private MerchandiseDao merchandiseDao;
    private MutableLiveData<List<Asset>> list;


    public MerchandiseRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        merchandiseDao = appDatabase.merchandiseDao();

    }

    public void insertIntoDb(Merchandise merchandise) {
            merchandiseDao.insertMerchandise(merchandise);
    }

    public Maybe<Merchandise> findMerchandise(Long outletId) {
      return  merchandiseDao.findMerchandiseByOutletId(outletId);
    }

    public Single<List<Asset>> loadAssets(Long outletId){
        return merchandiseDao.findAllAssetsForOutlet(outletId);

    }

    public void update(Merchandise merchandise){
        AsyncTask.execute(() -> merchandiseDao.updateMerchandise(merchandise));
    }

    public void updateAsset(Asset asset){
        AsyncTask.execute(() -> merchandiseDao.updateAsset(asset));

    }

    public void updateAssets(List<Asset> assets){
        AsyncTask.execute(() -> merchandiseDao.updateAssets(assets));

    }


}
