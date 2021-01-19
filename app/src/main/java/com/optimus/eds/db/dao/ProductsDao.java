package com.optimus.eds.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;

import java.util.List;

import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ProductsDao {

    @Query("SELECT * FROM Package ORDER BY packageName ASC")
    List<Package> findAllPackages();

    @Query("SELECT * FROM ProductGroup ORDER BY productGroupId ASC")
    List<ProductGroup> findAllProductGroups();


    @Query("SELECT * FROM Product ORDER BY name ASC")
    LiveData<List<Product>> findAllProduct();

    @Query("SELECT * FROM Product WHERE pkgId=:pkgId")
    Single<List<Product>> findAllProductsByPkg(Long pkgId);

    @Query("SELECT * FROM Product WHERE productGroupId=:groupId")
    Single<List<Product>> findAllProductsByGroupId(Long groupId);

    @Query("SELECT * FROM Product WHERE pk_pid=:id")
    Single<Product> findProductById(Long id);

    @Query("SELECT * FROM ProductGroup WHERE productGroupId=:id")
    LiveData<ProductGroup> findGroupById(Long id);

    @Query("SELECT * FROM Package WHERE packageId=:id")
    LiveData<Package> findPackageById(Long id);


    @Insert(onConflict = REPLACE)
    void insertProduct(Product product);

    @Insert(onConflict = REPLACE)
    void insertProducts(List<Product> products);

    @Insert(onConflict = REPLACE)
    void insertPackages(List<Package> packages);

    @Insert(onConflict = REPLACE)
    void insertProductGroups(List<ProductGroup> productGroups);

    @Update
    int updateProduct(Product product);


    @Update
    void updateProducts(List<Product> products);

    @Update
    void updatePackage(Package pkg);

    @Update
    void updatePackages(List<Package> packages);

    @Delete
    void deleteRoute(Product product);

    @Query("DELETE FROM Product")
    void deleteAllProducts();

    @Query("DELETE FROM Package")
    void deleteAllPackages();

    @Query("DELETE FROM ProductGroup")
    void deleteAllProductGroups();

}
