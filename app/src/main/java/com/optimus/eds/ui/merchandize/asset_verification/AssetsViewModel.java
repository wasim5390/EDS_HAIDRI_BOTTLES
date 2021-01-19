package com.optimus.eds.ui.merchandize.asset_verification;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.ui.merchandize.MerchandiseRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created By apple on 4/30/19
 */
public class AssetsViewModel extends AndroidViewModel {


    private MutableLiveData<List<Asset>> mAssets;
    private MerchandiseRepository repository;


    public AssetsViewModel(@NonNull Application application) {
        super(application);
        mAssets = new MutableLiveData<>();
        repository = new MerchandiseRepository(application);


    }

    public void loadAssets(Long outletId){
        repository.loadAssets(outletId).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(assets -> {
                    mAssets.postValue(assets);
        });

    }

    public void updateAsset(Asset asset){
         repository.updateAsset(asset);
    }

    public void verifyAsset(String barcode){
        List<Asset> assets = mAssets.getValue();
        if(assets!=null){
            for(Asset asset:assets){
                if(asset.getSerialNumber().equals(barcode)){
                    asset.setVerified(true);

                }
            }
            mAssets.postValue(assets);
            repository.updateAssets(assets);
        }
    }

    public LiveData<List<Asset>> getAssets() {
        return mAssets;
    }

}
