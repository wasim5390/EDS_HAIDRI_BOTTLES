package com.optimus.eds.source;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.optimus.eds.Constant;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.entities.Merchandise;
import com.optimus.eds.model.BaseResponse;
import com.optimus.eds.model.MerchandiseModel;
import com.optimus.eds.ui.merchandize.MerchandiseImage;
import com.optimus.eds.utils.Util;

import java.io.File;
import java.io.IOException;

import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class MerchandiseUploadService extends JobService implements Constant {

    private final String iTAG = MerchandiseUploadService.class.getSimpleName();
    String token;
    public MerchandiseUploadService() {

    }

    @Override
    public boolean onStartJob(JobParameters params) {
        if (params != null) {
            PersistableBundle bundle = params.getExtras();

            final Long outletId = bundle.getLong(EXTRA_PARAM_OUTLET_ID);
            token = bundle.getString(TOKEN);
            AppDatabase.getDatabase(getApplication())
                    .merchandiseDao().findMerchandiseByOutletId(outletId)
                    .map(merchandise -> {
                        for(MerchandiseImage merchandiseImage: merchandise.getMerchandiseImages()){
                            merchandiseImage.setBase64Image(Util.imageFileToBase64(new File(merchandiseImage.getPath())));
                        }
                        return merchandise;
                    })
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::uploadMerchandise,this::error);

        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }



    private void error(Throwable throwable) throws IOException {

        throwable.printStackTrace();
        String errorBody = throwable.getMessage();
        if (throwable instanceof HttpException){
            HttpException error = (HttpException)throwable;
            errorBody = error.response().errorBody().string();
        }
        MerchandiseUploadService.this.stopSelf();
    }


    /**
     * Handle action in the provided background thread with the provided
     * parameters.
     */
    private void uploadMerchandise(Merchandise merchandise) {

        MerchandiseModel merchandiseModel  = new MerchandiseModel(merchandise);

        RetrofitHelper.getInstance().getApi().postMerchandise(merchandiseModel,token)
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(baseResponse -> {
            onUpload(baseResponse,merchandise);

        },this::error);
    }

    private void onUpload(BaseResponse baseResponse,Merchandise merchandise) {
        if(baseResponse.isSuccess()) {
            Log.i(iTAG, "File Uploaded");
            removeRecord(merchandise);
        }
        else
            Log.i(iTAG,baseResponse.getResponseMsg());

    }

    private void removeRecord(Merchandise merchandise){
        AppDatabase.getDatabase(getApplication()).merchandiseDao().deleteMerchandise(merchandise.getOutletId());
    }


}

