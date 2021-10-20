package com.optimus.eds.ui.reports.stock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.model.PackageModel;

import java.util.List;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class StockActivity extends BaseActivity {

    @BindView(R.id.rvProducts)
    RecyclerView rvProducts;

    @BindView(R.id.group_spinner)
    AppCompatSpinner spinner;

    private SectionedRecyclerViewAdapter sectionAdapter;
    private StockViewModel viewModel;

    public static void start(Context context) {
        Intent starter = new Intent(context, StockActivity.class);
        (context).startActivity(starter);
    }
    @Override
    public int getID() {
        return R.layout.activity_stock;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.stock));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewModel = ViewModelProviders.of(this).get(StockViewModel.class);
        setObservers();
    }


    private void setObservers(){

        viewModel.isLoading().observe(this,loaded -> {
            if (!loaded) showProgress();
            else hideProgress();
        });

//        viewModel.stockLoaded().observe(this, responseModel -> {
//          if(responseModel.isSuccess()){
////              onProductGroupsLoaded(responseModel.getProductGroups());
//          }
//        });

        viewModel.getPackages().observe(this, packages -> {

            hideProgress();
            onPackagesLoaded(packages);
        });

//        viewModel.getProductGroupList().observe(this, this::onProductGroupsLoaded);

        viewModel.getProductList().observe(this, this::setSectionedAdapter);

        viewModel.showMessage().observe(this,s -> Toast.makeText(this, s, Toast.LENGTH_SHORT).show());

    }

    @Override
    public void showProgress() {
        showProgressD(this,true);
    }

    @Override
    public void hideProgress() {
        hideProgressD();
    }


    @OnClick(R.id.btnClose)
    public void onFinishClick(){
        finish();
    }


    private void setSectionedAdapter(List<PackageModel> packages){
        if(packages==null)
            return;
        sectionAdapter = new SectionedRecyclerViewAdapter();
        for(PackageModel pkg:packages){
            sectionAdapter.addSection(pkg.getPackageName(),new PackageSectionStock(pkg));
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvProducts.setLayoutManager(linearLayoutManager);
        rvProducts.setAdapter(sectionAdapter);
    }

//    private void onProductGroupsLoaded(List<ProductGroup> groups) {
//
//        ArrayAdapter userAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_dropdown_item, groups);
//        spinner.setAdapter(userAdapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                viewModel.filterProductsByGroup(((ProductGroup)(parent.getSelectedItem())).getProductGroupId());
//                viewModel.findAllProductsByPackageId();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//    }

    private void onPackagesLoaded(List<Package> groups) {

        ArrayAdapter userAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_dropdown_item, groups);
        spinner.setAdapter(userAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                viewModel.filterProductsByGroup(((ProductGroup)(parent.getSelectedItem())).getProductGroupId());
                viewModel.findAllProductsByPackageId(((Package)(parent.getSelectedItem())).getPackageId());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}
