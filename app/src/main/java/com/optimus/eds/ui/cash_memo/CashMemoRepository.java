package com.optimus.eds.ui.cash_memo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.ui.order.OrderBookingRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public class CashMemoRepository extends OrderBookingRepository {
    private ProductsDao productDao;
    private OrderDao orderDao;
    private RouteDao routeDao;



    public LiveData<Boolean> isSaving() {
        return isSaving;
    }

    private MutableLiveData<Boolean> isSaving;

    public CashMemoRepository(Application application) {
        super(application);
        productDao = appDatabase.productsDao();
        routeDao= appDatabase.routeDao();
        orderDao = appDatabase.orderDao();
        isSaving = new MutableLiveData<>();

    }
    public void updateOrderItem(OrderDetail orderDetail){
        AsyncTask.execute(() -> orderDao.updateOrderItem(orderDetail));
    }

}
