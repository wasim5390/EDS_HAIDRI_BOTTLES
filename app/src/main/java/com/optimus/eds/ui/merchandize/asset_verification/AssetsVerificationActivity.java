package com.optimus.eds.ui.merchandize.asset_verification;

import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.db.entities.LookUp;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.location_services.GpsUtils;
import com.optimus.eds.model.AssetStatus;
import com.optimus.eds.model.Configuration;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailActivity;
import com.optimus.eds.ui.scanner.ScannerActivity;
import com.optimus.eds.utils.PreferenceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created By apple on 4/30/19
 */
public class AssetsVerificationActivity extends BaseActivity implements AssetVerificationStatusListener {

    @BindView(R.id.rv_assets)
    RecyclerView recyclerView;
    @BindView(R.id.btnScanBarcode)
    Button btnScanBarcode;
    private Long outletId;
    private AssetsVerificationAdapter assetsVerificationAdapter;
    private final int SCANNER_REQUEST_CODE = 0x0001;

    AssetsViewModel viewModel;

    List<Asset> assetList ;

    String barcode ="";

    LatLng currentLatLng , outletLatLng ;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient locationProviderClient;
    private LocationCallback locationCallback ;
    Outlet outlet ;

    public static void start(Context context,Long outletId) {
        Intent starter = new Intent(context, AssetsVerificationActivity.class);
        starter.putExtra("OutletId",outletId);
        context.startActivity(starter);
    }

    @Override
    public int getID() {
        return R.layout.activity_asset_verification;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.asset_verification));

        outletId =  getIntent().getLongExtra("OutletId",0);
        viewModel = ViewModelProviders.of(this).get(AssetsViewModel.class);

        viewModel.findOutlet(outletId).observe(this , outlet -> {
            this.outlet = outlet ;
        });

        viewModel.getLookUpData().observe(this , lookUp -> {
            if (lookUp != null)
                initAssetsAdapter(lookUp.getAssetStatus());
        });

        viewModel.loadAssets(outletId);
        viewModel.getAssets().observe(this, assets -> {

            assetList = new ArrayList<>(assets);
            updateAssets(assetList);
        });


    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    private void updateAssets(List<Asset> assets) {
        assetsVerificationAdapter.populateAssets(assets);
    }

    private void initAssetsAdapter(List<AssetStatus> assetStatuses) {
        assetStatuses.add(0 , new AssetStatus("" , -1));

        assetList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this , RecyclerView.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
        assetsVerificationAdapter = new AssetsVerificationAdapter(this, assetStatuses,this);
        recyclerView.setAdapter(assetsVerificationAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @SuppressLint("MissingPermission")
    public void enableLocationServices() {
//        new GpsUtils(this, LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
//                .turnGPSOn(isGPSEnable -> {
//                    // turn on GPS
//                    if(isGPSEnable) {
//                        createLocationRequest();
//                        setLocationCallback();
//                        locationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
//                    }
//                });

        createLocationRequest();
        setLocationCallback();
        locationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());

    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    public void setLocationCallback(){
        locationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        if (location.isFromMockProvider()){
                            Toast.makeText(AssetsVerificationActivity.this, "You are using Fake GPS", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        currentLatLng = new LatLng(location.getLatitude() , location.getLongitude());

                        if (outlet != null){

                            outletLatLng = new LatLng(outlet.getLatitude() , outlet.getLongitude());

                            Double metre = checkMetre(currentLatLng , outletLatLng);
                            Configuration config = PreferenceUtil.getInstance(AssetsVerificationActivity.this).getConfig();

                            if (config.getGeoFenceMinRadius() != null){
                                if (metre > config.getGeoFenceMinRadius() ){
                                    showOutsideBoundaryDialog(0);
                                }
                            }else{
                                if (metre > 100 ){
                                    showOutsideBoundaryDialog(0);
                                }
                            }

                            if (barcode != null)
                                if (!barcode.isEmpty())
                                    viewModel.verifyAsset(barcode);

                        }

                        locationProviderClient.removeLocationUpdates(locationCallback);

                    }
                }
            }
        };
    }
//    private LocationCallback locationCallback = new LocationCallback(){
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            if (locationResult == null) {
//                return;
//            }
//            for (Location location : locationResult.getLocations()) {
//                if (location != null) {
//
//                   if (location.isFromMockProvider()){
//                       Toast.makeText(AssetsVerificationActivity.this, "You are using Fake GPS", Toast.LENGTH_SHORT).show();
//                       return;
//                   }
//
//                    currentLatLng = new LatLng(location.getLatitude() , location.getLongitude());
//
//                   if (outlet != null){
//
//                       outletLatLng = new LatLng(outlet.getLatitude() , outlet.getLongitude());
//                       if (checkMetre(currentLatLng, outletLatLng) > 100) {
//                           showOutsideBoundaryDialog(0);
//                       }
//                       if (barcode != null)
//                           if (!barcode.isEmpty())
//                               viewModel.verifyAsset(barcode);
//                   }
//
//                }
//            }
//
//        }
//    };

    public void showOutsideBoundaryDialog( int repeat){

        if (repeat != 5){

            final int repeatLocal = ++repeat ;
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
            builderSingle.setTitle(R.string.warning);
            builderSingle.setMessage(R.string.retailersBoundary);
            builderSingle.setCancelable(false);
            builderSingle.setPositiveButton(getString(R.string.ok), (dialog1, which1) ->{
                dialog1.dismiss();
                showOutsideBoundaryDialog(repeatLocal);
            });
            builderSingle.show();

        }
    }
    public Double checkMetre(LatLng from , LatLng to){
        return Double.parseDouble(new DecimalFormat("##.##").format(SphericalUtil.computeDistanceBetween(from , to )));
    }

    @OnClick(R.id.btnScanBarcode)
    public void BarCodeClick(){
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivityForResult(intent,SCANNER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            switch (requestCode){
                case SCANNER_REQUEST_CODE:
                    barcode = data.getStringExtra(KEY_SCANNER_RESULT);
                   permissionCheck();
                    break;
            }
        }
    }

    @Override
    public void onStatusChange(Asset asset) {
        viewModel.updateAsset(asset);
    }

    public void permissionCheck(){

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){
                           enableLocationServices();
                        }
                        else{
                            if(report.isAnyPermissionPermanentlyDenied())
                                openLocationSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void onBackPressed() {

        int verified = 0 , notVerified = 0 , statusWithOutVerified = 0 ;
        if (assetsVerificationAdapter.getAssetList().size() > 0){

            for (Asset asset : assetsVerificationAdapter.getAssetList()){

                if (asset.getStatusid() != null && asset.getVerified()){
                    verified++;
                }else if (asset.getStatusid() != null){
                    notVerified++;
                }else{
                    statusWithOutVerified++;
                }
            }
        }

        if (statusWithOutVerified > 0){
            PreferenceUtil.getInstance(this).setAssetsScannedWithoutVerified(true);
        }else if (notVerified > 0){
            PreferenceUtil.getInstance(this).setAssetsScannedInLastMonth(false);
            PreferenceUtil.getInstance(this).setAssetsScannedWithoutVerified(false);
        }else if (verified == assetsVerificationAdapter.getAssetList().size()){
            PreferenceUtil.getInstance(this).setAssetsScannedInLastMonth(true);
            PreferenceUtil.getInstance(this).setAssetsScannedWithoutVerified(true);
        }
        finish();
    }
}
