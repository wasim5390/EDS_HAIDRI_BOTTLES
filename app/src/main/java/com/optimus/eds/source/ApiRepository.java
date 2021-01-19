package com.optimus.eds.source;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRepository implements DataSource, Constant {

    private MutableLiveData<List<Outlet>> mutableOutletList = new MutableLiveData<>();
    private MutableLiveData<List<Route>> mutableRouteList = new MutableLiveData<>();
    private static ApiRepository instance;
    public static ApiRepository getInstance() {
        if (instance == null) {
            instance = new ApiRepository();
        }
        return instance;
    }

    private ApiRepository() {

        }
    @Override
    public LiveData<List<Route>> getRoutes(String userId) {
          RetrofitHelper.getInstance().getApi().getRoutes(userId).enqueue(new Callback<List<Route>>() {
              @Override
              public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                  if(response.isSuccessful())
                      mutableRouteList.setValue(response.body());
              }

              @Override
              public void onFailure(Call<List<Route>> call, Throwable t) {
                mutableRouteList.setValue(null);
              }
          });
          return mutableRouteList;
    }

    @Override
    public LiveData<List<Outlet>> getOutlets(String routeId) {
        RetrofitHelper.getInstance().getApi().getOutlets(routeId).enqueue(new Callback<List<Outlet>>() {
            @Override
            public void onResponse(Call<List<Outlet>> call, Response<List<Outlet>> response) {
                if(response.isSuccessful())
                    mutableOutletList.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Outlet>> call, Throwable t) {
                mutableOutletList.setValue(null);

            }
        });
        return mutableOutletList;
    }


}
