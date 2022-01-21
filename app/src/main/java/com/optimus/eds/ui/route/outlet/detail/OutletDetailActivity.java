package com.optimus.eds.ui.route.outlet.detail;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.maps.android.SphericalUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.LocationLogs;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.OutletVisit;
import com.optimus.eds.location_services.GpsUtils;
import com.optimus.eds.model.Configuration;
import com.optimus.eds.model.CustomObject;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.source.UploadOrdersService;
import com.optimus.eds.ui.AlertDialogManager;
import com.optimus.eds.ui.merchandize.OutletMerchandiseActivity;
import com.optimus.eds.ui.order.OrderBookingActivity;
import com.optimus.eds.ui.route.outlet.tasks.OutletTasksDialogFragment;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.optimus.eds.location_services.GpsUtils.GPS_REQUEST;

public class OutletDetailActivity extends BaseActivity implements
        AdapterView.OnItemSelectedListener, OnMapReadyCallback {


    private static final String TAG = OutletDetailActivity.class.getName();
    private static final int REQUEST_CODE = 0x1001;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient locationProviderClient;
    private Long outletId;
    @BindView(R.id.tvName)
    TextView outletName;
    @BindView(R.id.tvAddress)
    TextView outletAddress;
    @BindView(R.id.tvChannel)
    TextView outletChannel;

    @BindView(R.id.digitalAccountTv)
    TextView digitalAccountTv;

    @BindView(R.id.disburseAmountTv)
    TextView disburseAmountTv;


    @BindView(R.id.remarksTv)
    TextView remarksTv;

    @BindView(R.id.tvTotalVisit)
    TextView outletVisits;
    @BindView(R.id.monthlySalesText)
    TextView monthlySalesText;
    @BindView(R.id.pop_spinner)
    AppCompatSpinner popSpinner;
    @BindView(R.id.btnOk)
    Button btnOk;
    @BindView(R.id.btnNotFlow)
    Button btnNotFlow;
    @BindView(R.id.btnTasks)
    Button btnTasks;


    @BindView(R.id.lastOrderPrice)
    TextView lastOrderPrice;
    @BindView(R.id.lastOrderTakenDate)
    TextView lastOrderTakenDate;
    @BindView(R.id.lastOrderQuantityText)
    TextView lastOrderQuantity;
    @BindView(R.id.lastOrder)
    MaterialCardView lastOrderCard;

    Location accurateLatLng ;

    Outlet outlet;

    OutletDetailViewModel viewModel;
    private String reasonForNoSale = "";

    private boolean isAssets = false ;

    private int alertDialogCount = 0 ;

    private Long startLocationTime  = 0L , endLocationTime = 0L;

    private List<LocationLogs> locationsListForLog = new ArrayList<>();

    private GoogleMap mMap;
    private Long outletVisitStartTime = Calendar.getInstance().getTimeInMillis();
    private Location currentLocation = new Location("CurrentLocation");
    private LatLng outletLatLng, currentLatLng;
    private boolean isFakeLocation = false;
    private LocationCallback locationCallback ;
    private boolean withoutVerification ;

    public static void start(Context context, Long outletId, Long routeId, int code) {
        Intent starter = new Intent(context, OutletDetailActivity.class);
        starter.putExtra("OutletId", outletId);
        starter.putExtra("RouteId", routeId);
        ((Activity) context).startActivityForResult(starter, code);
    }

    @Override
    public int getID() {
        return R.layout.activity_outlet;
    }

    @Override
    public void created(Bundle savedInstanceState) {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (locationRequest != null && locationCallback != null)
                                locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                        } else {
                            if (report.isAnyPermissionPermanentlyDenied()){
                                Toast.makeText(OutletDetailActivity.this, "Please access location permission", Toast.LENGTH_SHORT).show();
                                openLocationSettings();
                            }
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        ButterKnife.bind(this);
        outletId = getIntent().getLongExtra("OutletId", 0);
        viewModel = ViewModelProviders.of(this).get(OutletDetailViewModel.class);
        showProgress();

        PreferenceUtil.getInstance(this).setVisitTime(outletVisitStartTime);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment == null) return;
        mapFragment.getMapAsync(this);


        setToolbar(getString(R.string.outlet_summary));
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item);
//        adapter.addAll(getResources().getStringArray(R.array.pop_array));
//        popSpinner.setAdapter(adapter);
//        popSpinner.setOnItemSelectedListener(this);
        viewModel.findOutlet(outletId).observe(this, outlet -> {
            this.outlet = outlet;
            updateUI(outlet);
        });
        viewModel.getOutletNearbyPos().observe(this, outletLocation -> {
            AlertDialogManager.getInstance().showLocationMissMatchAlertDialog(OutletDetailActivity.this, currentLocation, outletLocation);
        });
        viewModel.getUploadStatus().observe(this, aBoolean -> {
            if (aBoolean) {
                viewModel.uploadStatus(outletId, currentLocation, outletVisitStartTime, Calendar.getInstance().getTimeInMillis(), reasonForNoSale);
            } else {
                if (enableMerchandise)
                    OutletMerchandiseActivity.start(this, outletId, REQUEST_CODE);
                else
                    OrderBookingActivity.start(this, outletId, REQUEST_CODE);

//                finish();
            }
        });

        viewModel.startUploadService().observe(this, aBoolean -> {
            if (aBoolean) {
                UploadOrdersService.startUploadService(getApplication(), outletId);
//                finish(); by husnain
            }else{
                finish();
            }
        });

        viewModel.loadAssets(outletId);
        viewModel.getAssets().observe(this, assets -> {
            createLocationRequest();
            setLocationCallback();
            enableLocationServices();
            outletVisits.setText(String.valueOf(assets.size()));

            isAssets = assets.size() > 0;
        });

       /* viewModel.stockLoaded().observe(this,responseModel -> {

        });*/

        viewModel.isLoading().observe(this, loaded -> {
            if (!loaded) showProgress();
            else hideProgress();
        });

        viewModel.viewTasks().observe(this, exist -> {
            btnTasks.setVisibility(exist ? View.VISIBLE : View.GONE);
        });


        updateBtn(false);

        LocalBroadcastManager.getInstance(this).registerReceiver(orderUploadSuccessReceiver,new IntentFilter(Constant.ACTION_ORDER_UPLOAD));


    }



    @SuppressLint("MissingPermission")
    public void enableLocationServices() {
        new GpsUtils(this, LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
                .turnGPSOn(isGPSEnable -> {
                    // turn on GPS
                    if (isGPSEnable) {
                        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                    }
                });

    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setSmallestDisplacement(0F);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    private void setLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.d("hasAccuracy" , location.hasAccuracy()+ "  accuracy " + location.getAccuracy() +" latitude" + location.getLatitude() + "longitude" + location.getLongitude() + " Provider " + location.getProvider());
//                        Toast.makeText(OutletDetailActivity.this, "hasAccuracy"+ location.hasAccuracy()+ "  accuracy " + location.getAccuracy() +" latitude " + location.getLatitude() + "longitude " + location.getLongitude() , Toast.LENGTH_SHORT).show();
                        isFakeLocation = location.isFromMockProvider();
                        if (isFakeLocation){
                            locationProviderClient.removeLocationUpdates(locationCallback);
                            Toast.makeText(OutletDetailActivity.this, "You are using fake GPS", Toast.LENGTH_SHORT).show();
                            return ;
                        }

                        if (accurateLatLng == null){
                            currentLocation = location;
                            accurateLatLng = location;
                            currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        }else{

                            if (accurateLatLng.getAccuracy() < location.getAccuracy())
                                currentLatLng = new LatLng(accurateLatLng.getLatitude(), accurateLatLng.getLongitude());
                            else{
                                accurateLatLng = location;
                                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            }
                        }

                        locationsListForLog.add(new LocationLogs(location.getLatitude() , location.getLongitude() , location.getAccuracy()));
                        if (outletLatLng != null) {

                            Double metre = checkMetre(currentLatLng, outletLatLng);
                            Configuration config = PreferenceUtil.getInstance(OutletDetailActivity.this).getConfig();

                            startLocationTime = System.currentTimeMillis();
                            if (config.getGeoFenceMinRadius() != null) {
                                if (metre > config.getGeoFenceMinRadius() && !isAssets && startLocationTime > endLocationTime) {
                                    hideProgress();
                                    locationProviderClient.removeLocationUpdates(locationCallback);
                                    Log.d("hasAccuracy selected" , accurateLatLng.getAccuracy() + "");
                                    showOutsideBoundaryDialog(alertDialogCount, String.valueOf(metre));
                                } else if (metre > config.getGeoFenceMinRadius() && isAssets && startLocationTime > endLocationTime) {
                                   hideProgress();
                                    locationProviderClient.removeLocationUpdates(locationCallback);
//                                    updateBtn(true);
                                    showOutsideBoundaryDialog(alertDialogCount, String.valueOf(metre)); // showOutsideBoundaryDialogWhenAssets(String.valueOf(metre));
                                } else if (metre <= config.getGeoFenceMinRadius()){
                                    hideProgress();
                                    locationProviderClient.removeLocationUpdates(locationCallback);
                                    updateBtn(true);
                                }
                            }else {
                                hideProgress();
                                locationProviderClient.removeLocationUpdates(locationCallback);
                                Toast.makeText(OutletDetailActivity.this, "GeoFence radius is not defined", Toast.LENGTH_SHORT).show();
                            }
                        }
//                        locationProviderClient.removeLocationUpdates(locationCallback);
//                        break;
                    }
                }

            }
        };
    }

//    private LocationCallback locationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            if (locationResult == null) {
//                return;
//            }
//            for (Location location : locationResult.getLocations()) {
//                if (location != null) {
//
//
//                    isFakeLocation = location.isFromMockProvider();
//
//                    currentLocation = location;
//                    currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//
//                    // Toast.makeText(OutletDetailActivity.this, "Location Received!", Toast.LENGTH_SHORT).show();
//                    if (locationProviderClient != null) {
//                        locationProviderClient.removeLocationUpdates(locationCallback);
//                    }
//
//                    if (outletLatLng != null) {
//
//                        Double metre = checkMetre(currentLatLng, outletLatLng);
//                        Configuration config = PreferenceUtil.getInstance(OutletDetailActivity.this).getConfig();
//
//                        if (config.getGeoFenceMinRadius() != null) {
//                            if (metre > config.getGeoFenceMinRadius() && !isAssets) {
//                                showOutsideBoundaryDialog(alertDialogCount, String.valueOf(metre));
//                            }else if (metre > config.getGeoFenceMinRadius() && isAssets){
//                                showOutsideBoundaryDialogWhenAssets(String.valueOf(metre));
//                            }else{
//                                updateBtn(true);
//                            }
//                        }
//                    }
//
//                }
//            }
//
//        }
//    };



    public void showOutsideBoundaryDialogWhenAssets(String metres) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(R.string.warning);
        builderSingle.setMessage("You are " + metres + " meters away from the retailer\'s defined boundary.\nPress Ok to continue" +
                "\nCurrent LatLng :: " + currentLatLng.latitude + "," + currentLatLng.longitude);
        builderSingle.setCancelable(false);
        builderSingle.setPositiveButton("Retry Location", (dialog1, which1) -> {
            dialog1.dismiss();
            startLocationTime = System.currentTimeMillis();
            endLocationTime = startLocationTime + 4000;
            showProgress();
            locationProviderClient.requestLocationUpdates(locationRequest , locationCallback , Looper.getMainLooper());
        });
        builderSingle.setNegativeButton("Back to PJP", (dialog1, which1) -> {
            dialog1.dismiss();

            sendLogs();

            viewModel.postEmptyCheckoutWithoutSurvey(true, outletId, outletVisitStartTime, Calendar.getInstance().getTimeInMillis());
        });
        if (!OutletDetailActivity.this.isFinishing()) {
            builderSingle.show();
        }

    }

    private void sendLogs(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("outletId" , outletId.toString());
            jsonObject.put("locationLogs" ,new Gson().toJson(locationsListForLog) );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseCrashlytics.getInstance().setCustomKey(outletId.toString() , jsonObject.toString());
        FirebaseCrashlytics.getInstance().log(jsonObject.toString());

        try {
            throw new Exception();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showOutsideBoundaryDialog(int repeat, String metres) {
        if (repeat < 5) {

            final int repeatLocal = ++repeat;
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
            builderSingle.setTitle(R.string.warning);
            builderSingle.setMessage("You are " + metres + " meters away from the retailer\'s defined boundary.\nPress Ok to continue" +
                    "\nCurrent LatLng :: " + currentLatLng.latitude + "," + currentLatLng.longitude +
                    "\nAlert Count :: " + repeat);
            builderSingle.setCancelable(false);
            builderSingle.setPositiveButton(getString(R.string.ok), (dialog1, which1) -> {
                dialog1.dismiss();
                alertDialogCount++;
                startLocationTime = System.currentTimeMillis();
                endLocationTime = startLocationTime + 4000;
                showProgress();
                locationProviderClient.requestLocationUpdates(locationRequest , locationCallback , Looper.getMainLooper());
//                showOutsideBoundaryDialog(repeatLocal, metres);
            });
            if (!OutletDetailActivity.this.isFinishing()) {
                builderSingle.show();
            }
        }else {
            sendLogs();

            OutletVisit outletVisit = new OutletVisit();
            outletVisit.setOutletId(outletId);
            outletVisit.setVisitTime(outletVisitStartTime);
            outletVisit.setLatitude(currentLatLng.latitude);
            outletVisit.setLongitude(currentLatLng.longitude);
            outlet.getOutletVisits().add(outletVisit);
            viewModel.updateOutlet(outlet);
            updateBtn(true);
        }
    }


    public Double checkMetre(LatLng from, LatLng to) {
        return Double.parseDouble(new DecimalFormat("##.##").format(SphericalUtil.computeDistanceBetween(from, to)));
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int code = getOutletPopCode(popSpinner.getSelectedItem().toString());
        viewModel.updateOutletStatusCode(code);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    private void updateUI(Outlet outlet) {
        if (outlet != null) {
            setTitle(outlet.getOutletName());
            outletAddress.setText(!outlet.getAddress().isEmpty() ? outlet.getAddress() : "");
            outletChannel.setText(String.valueOf(outlet.getChannelName()));
            outletName.setText(outlet.getOutletName().concat(" - " + outlet.getLocation()));
            digitalAccountTv.setText(outlet.getDigitalAccount());
            disburseAmountTv.setText(outlet.getDisburseAmount() + "");
            remarksTv.setText(outlet.getRemarks());
            viewModel.setOutlet(outlet);

            if (outlet.getLastOrder() != null) {
                lastOrderPrice.setText("RS. " + outlet.getLastOrder().getOrderTotal());
                lastOrderQuantity.setText(String.valueOf(outlet.getLastOrder().getOrderQuantity()));
                lastOrderTakenDate.setText(Util.formatDate(Util.DATE_FORMAT, outlet.getLastOrder().getLastSaleDate()));
            }

            if (outlet.getMtdSale() != null) {
                monthlySalesText.setText(String.valueOf(outlet.getMtdSale()));
            }

            outletLatLng = new LatLng(outlet.getLatitude(), outlet.getLongitude());
            if (mMap != null ) {
                mMap.addMarker(new MarkerOptions().position(outletLatLng).title(outlet.getOutletName() != null ? outlet.getOutletName() : ""));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(outletLatLng, 19));
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                mMap.getUiSettings().setMapToolbarEnabled(false);
            }
        }
    }


    private void updateBtn(boolean enable) {
        btnOk.setEnabled(enable);
        btnOk.setAlpha(enable ? 1.0f : 0.5f);

        btnNotFlow.setEnabled(enable);
        btnNotFlow.setAlpha(enable ? 1.0f : 0.5f);



    }

    @OnClick(R.id.btnPromotions)
    public void onPromotionsClick() {
        List<CustomObject> objects = new ArrayList<>();
//        objects.add(new CustomObject(125L,"16 Free-345 ml with jumbo case"));
//        AlertDialogManager.getInstance().showListAlertDialog(this,getString(R.string.promotions),
//                object -> {
//                    Toast.makeText(this, object.getText(), Toast.LENGTH_SHORT).show();
//                },objects);

        viewModel.getPromos(outletId).observe(this, promotions -> {

            if (promotions.size() > 0) {
                AlertDialogManager.getInstance().showPromotionsDialog(this, promotions);
            } else {
                Toast.makeText(this, "No Promotions", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick(R.id.navigation)
    public void onNavigationClick() {

        try {
            if (outletLatLng != null) {
                Uri navigation = Uri.parse("google.navigation:q=" + outletLatLng.latitude + "," + outletLatLng.longitude + "");
                Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                navigationIntent.setPackage("com.google.android.apps.maps");
                startActivity(navigationIntent);
            }
        }catch (Exception e){
            Toast.makeText(this, "Please install google map", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnTasks)
    public void onViewTasksClick() {
        OutletTasksDialogFragment.newInstance(outletId).show(getSupportFragmentManager(), "TaskFragmentDialog");

    }

    @OnClick(R.id.lastOrder)
    public void onLastOrderCLick() {
        if (outlet.getLastOrder() != null)
            AlertDialogManager.getInstance().showLastOrderDialog(this, outlet.getLastOrder());
    }

    @SuppressLint("MissingPermission")
    @OnClick(R.id.btnOk)
    public void onOkClick() {

        viewModel.updateOutletStatusCode(1);
        if (currentLatLng != null)
            viewModel.onNextClick(currentLocation , currentLatLng, outletVisitStartTime);
        else{
            locationProviderClient.requestLocationUpdates(locationRequest , locationCallback , Looper.getMainLooper());
        }
//        if (!isFakeLocation) {
//            viewModel.updateOutletStatusCode(1);
//            if (currentLatLng != null)
//                viewModel.onNextClick(currentLocation, outletVisitStartTime);
//            else{
//                locationProviderClient.requestLocationUpdates(locationRequest , locationCallback , Looper.getMainLooper());
//            }
//        } else {
//            Toast.makeText(this, "You are using fake GPS", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onDestroy() {
        if (orderUploadSuccessReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(orderUploadSuccessReceiver);
        }
        if (locationProviderClient != null)
        locationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    @OnClick(R.id.btnNotFlow)
    public void notFlowClick(View v) {

        if (!isFakeLocation) {
            PopupMenu menu = new PopupMenu(this, v);

            HashMap<String, Integer> hashMap = new HashMap<>();
            hashMap.put("Outlet Closed", 2);
            hashMap.put("No Time", 3);

            menu.getMenu().add("Outlet Closed");
            menu.getMenu().add("No Time");
            menu.show();

            menu.setOnMenuItemClickListener(item -> {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
                builderSingle.setTitle(R.string.warning);
                builderSingle.setMessage("Are you sure you want to take an action?");
                builderSingle.setCancelable(true);
                builderSingle.setPositiveButton(getString(R.string.ok), (dialog1, which1) -> {
                    dialog1.dismiss();
                    int code = hashMap.get(item.getTitle().toString());
                    viewModel.updateOutletStatusCode(code);
                    if (currentLatLng != null)
                        viewModel.onNextClick(currentLocation , currentLatLng, outletVisitStartTime);
                    else{
                        locationProviderClient.requestLocationUpdates(locationRequest , locationCallback , Looper.getMainLooper());
                    }
                });

                builderSingle.show();

                return true;
            });
        } else {
            Toast.makeText(this, "You are using fake GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GPS_REQUEST:
                    locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                    break;
                case REQUEST_CODE:
                    if (data != null && data.getExtras() != null && data.hasExtra(EXTRA_PARAM_NO_ORDER_FROM_BOOKING)) {
                        boolean noOrderFromOrderBooking = data.getBooleanExtra(EXTRA_PARAM_NO_ORDER_FROM_BOOKING, false);
                        withoutVerification = data.getBooleanExtra(WITHOUT_VERIFICATION, false);
                        reasonForNoSale = String.valueOf(data.getLongExtra(EXTRA_PARAM_OUTLET_REASON_N_ORDER, 1L));
                        if (!withoutVerification){
                            showProgress();
                            viewModel.postEmptyCheckout(noOrderFromOrderBooking, outletId, outletVisitStartTime, Calendar.getInstance().getTimeInMillis());
//                            viewModel.scheduleMerchandiseJob(getApplication(), outletId, PreferenceUtil.getInstance(getApplication()).getToken());
                        } else
                            viewModel.postEmptyCheckoutWithoutAssetVerification(noOrderFromOrderBooking, outletId, outletVisitStartTime, Calendar.getInstance().getTimeInMillis());
                    } else {
                        setResult(RESULT_OK);
                        finish();
                    }
                    break;
            }

        }
    }


    private BroadcastReceiver orderUploadSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constant.ACTION_ORDER_UPLOAD)){
                MasterModel response = (MasterModel) intent.getSerializableExtra("Response");
                hideProgress();
                if(response!=null && response.isSuccess()){
                    Toast.makeText(context, response.isSuccess()?"Order Uploaded Successfully!":response.getResponseMsg()+"", Toast.LENGTH_SHORT).show();
                    if (!withoutVerification)
                        viewModel.scheduleMerchandiseJob(getApplication(), outletId, PreferenceUtil.getInstance(getApplication()).getToken());
                    finish();
                }else{
                    Toast.makeText(context, response.getErrorMessage()+"", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void showProgress() {
        showProgressD(this, true);
    }

    @Override
    public void hideProgress() {
        hideProgressD();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
    }
}
