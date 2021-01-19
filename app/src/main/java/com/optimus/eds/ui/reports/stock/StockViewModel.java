package com.optimus.eds.ui.reports.stock;

import android.app.Application;

import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.model.PackageProductResponseModel;
import com.optimus.eds.ui.order.OrderBookingRepository;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;
import com.optimus.eds.utils.NetworkManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class StockViewModel extends AndroidViewModel {

    private final CompositeDisposable disposable;
    private final OrderBookingRepository repository;
    private final OutletDetailRepository detailRepo;

    private final MutableLiveData<List<PackageModel>> mutablePkgList;
    private MutableLiveData<List<ProductGroup>> productGroupList;
    private final MutableLiveData<String> msg;
    private LiveData<List<Package>> packages;


    private  final static String TAG= StockViewModel.class.getName();


    public StockViewModel(@NonNull Application application) {
        super(application);
        disposable = new CompositeDisposable();
        repository = OrderBookingRepository.singleInstance(application);
        detailRepo = new OutletDetailRepository(application);
        mutablePkgList = new MutableLiveData<>();
        msg = new MutableLiveData<>();
        productGroupList = repository.findAllGroups();
        packages = repository.findAllPackages();
        loadProducts();
    }


    public void filterProductsByGroup(Long groupId){
        Single<List<Product>> allProductsByGroup = repository.findAllProductsByGroup(groupId);
        disposable.add(allProductsByGroup.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onProductsLoaded));
    }

    private void onProductsLoaded(List<Product> products) {
        mutablePkgList.postValue(repository.packageModel(packages.getValue(),products));
    }




    public void loadProducts(){

        NetworkManager.getInstance().isOnline().subscribe((aBoolean, throwable) -> {
            if (aBoolean) {
                detailRepo.loadProductsFromServer();
            }
        });
    }


    public LiveData<List<PackageModel>> getProductList() {
        return mutablePkgList;
    }

    public LiveData<List<ProductGroup>> getProductGroupList() {
        return productGroupList;
    }

    public LiveData<PackageProductResponseModel> stockLoaded(){
       return detailRepo.stockLoaded();
    }


    public LiveData<Boolean> isLoading() {
        return detailRepo.isLoading();
    }

    public LiveData<String> showMessage(){
        return msg;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();

    }
}
