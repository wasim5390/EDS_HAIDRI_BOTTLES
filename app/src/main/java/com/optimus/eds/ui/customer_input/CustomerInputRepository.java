package com.optimus.eds.ui.customer_input;

import android.app.Application;
import androidx.lifecycle.LiveData;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Maybe;
import io.reactivex.Single;

import android.os.AsyncTask;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.CustomerDao;
import com.optimus.eds.db.entities.CustomerInput;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;


public class CustomerInputRepository {

    private OutletDetailRepository outletDetailRepository;
    private CustomerDao customerDao;
    public CustomerInputRepository(Application application) {
        customerDao = AppDatabase.getDatabase(application).customerDao();
        outletDetailRepository = new OutletDetailRepository(application);
    }

    public LiveData<Outlet> getOutletById(Long outletId) {
        return outletDetailRepository.getOutletById(outletId);
    }

    public Completable saveCustomerInput(CustomerInput customerInput){
       return Completable.fromAction(() -> customerDao.insertCustomerInput(customerInput));

    }

    public Maybe<CustomerInput> getCustomerInput(Long outletId){
       return customerDao.findCustomerInput(outletId);
    }

}
