package com.optimus.eds.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.optimus.eds.db.entities.CustomerInput;

import io.reactivex.Completable;
import io.reactivex.Maybe;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CustomerDao {

    @Query("SELECT * FROM CustomerInput WHERE outletId=:outletId")
    Maybe<CustomerInput> findCustomerInput(Long outletId);

    @Insert(onConflict = REPLACE)
    void insertCustomerInput(CustomerInput customerInput);

    @Update
    int updateCustomerInput(CustomerInput customerInput);

    @Query("Delete FROM CustomerInput WHERE outletId=:outletId")
    void deleteCustomerInput(Long outletId);

    @Query("DELETE FROM CustomerInput")
    void deleteAllCustomerInput();
}
