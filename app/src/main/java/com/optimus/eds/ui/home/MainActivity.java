package com.optimus.eds.ui.home;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bugfender.sdk.Bugfender;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.BuildConfig;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.model.TargetVsAchievement;
import com.optimus.eds.model.WorkStatus;
import com.optimus.eds.source.ApkDownloader;
import com.optimus.eds.ui.AlertDialogManager;
import com.optimus.eds.ui.AppUpdater;
import com.optimus.eds.ui.login.LoginActivity;
import com.optimus.eds.ui.reports.ReportsActivity;
import com.optimus.eds.ui.reports.stock.StockActivity;
import com.optimus.eds.ui.route.outlet.routes.RoutesActivity;
import com.optimus.eds.utils.NetworkManagerKotlin;
import com.optimus.eds.utils.PermissionUtil;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.schedulers.Schedulers;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;


public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav)
    NavigationView nav;

    @BindView(R.id.tvRunningDay)
    TextView tvRunningDay;

    @BindView(R.id.keyOne)
    TextView keyOne;
    @BindView(R.id.keyOneValue)
    TextView keyOneValue;
    @BindView(R.id.keyTwo)
    TextView keyTwo;
    @BindView(R.id.keyTwoValue)
    TextView keyTwoValue;
    @BindView(R.id.keyThree)
    TextView keyThree;
    @BindView(R.id.keyThreeValue)
    TextView keyThreeValue;
    @BindView(R.id.keyFour)
    TextView keyFour;
    @BindView(R.id.keyFourValue)
    TextView keyFourValue;

    private ActionBarDrawerToggle drawerToggle;
    HomeViewModel viewModel;

    private AppUpdateManager appUpdateManager;

    private NetworkManagerKotlin networkManager;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    public int getID() {
        return R.layout.activity_home;
    }

    private void checkUpdate() {
        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
        Log.d(TAG, "Checking for updates");

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            1122);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Update available");
            } else {
                Log.d(TAG, "No Update available");

                init();
            }
        }).addOnFailureListener(e -> {

            Toast.makeText(this, e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
            init();
        });

    }

    @Override
    public void created(Bundle savedInstanceState) {

        init();
//        appUpdateManager = AppUpdateManagerFactory.create(this);
//
//        checkUpdate();

        Bugfender.d("Test", "Hello world!");


    }


    private void init() {
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        setObservers();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        networkManager = new NetworkManagerKotlin(this);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        TextView navProfileName = nav.getHeaderView(0).getRootView().findViewById(R.id.profileName);

        if (PreferenceUtil.getInstance(this).getUsername().contains("@")){
            String[] usernameString = PreferenceUtil.getInstance(this).getUsername().split("@");
            navProfileName.setText(usernameString[0]);
        }else{
            navProfileName.setText(PreferenceUtil.getInstance(this).getUsername());
        }

        MenuItem appVersion = nav.getMenu().getItem(3);
        appVersion.setTitle("App version " + BuildConfig.VERSION_CODE);

        MenuItem lastUpdate = nav.getMenu().getItem(4);
        lastUpdate.setTitle("Updated on 1/22/2022");

        nav.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.account:
                    Toast.makeText(MainActivity.this, getString(R.string.profile_will_avl_soon), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.update:
                    PermissionUtil.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionUtil.PermissionCallback() {
                        @Override
                        public void onPermissionsGranted(String permission) {

                        }

                        @Override
                        public void onPermissionsGranted() {
                            //  showProgress(true,getString(R.string.downloading));
                            showProgress();
                            viewModel.checkAppUpdate();
                        }

                        @Override
                        public void onPermissionDenied() {
                            Toast.makeText(MainActivity.this, getString(R.string.access_to_update), Toast.LENGTH_SHORT).show();
                        }
                    });


                    break;
                case R.id.exit:
                    AlertDialogManager.getInstance().showVerificationAlertDialog(this,
                            getString(R.string.logout), getString(R.string.are_you_sure_to_logout), verified -> {
                                if (verified) {
                                    PreferenceUtil.getInstance(this).clearAllPreferences();
                                    LoginActivity.start(this);
                                    finish();

                                }
                            });
                    break;
                default:
                    return true;
            }

            return false;
        });

        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        viewModel.checkDayEnd(); //TODO enable this in production

//        setBarChart();

        if (PreferenceUtil.getInstance(this).getTargetAchievement() != null)
            setTargetVsAchievement(new Gson().fromJson(PreferenceUtil.getInstance(this).getTargetAchievement(), TargetVsAchievement.class));

    }

    private void setTargetVsAchievement(TargetVsAchievement targetVsAchievement) {

        if (targetVsAchievement != null) {
            keyOne.setText(R.string.targetQuantity);
            keyOneValue.setText(String.valueOf(targetVsAchievement.getTargetQuantity() != null ? targetVsAchievement.getTargetQuantity() : "0"));

            keyTwo.setText(R.string.achieved_quantity);
            keyTwoValue.setText(String.valueOf(targetVsAchievement.getAchievedQuantityPercentage() != null ? targetVsAchievement.getAchievedQuantityPercentage() : "0"));

            keyThree.setText(R.string.perDayQuantity);
            keyThreeValue.setText(String.valueOf(targetVsAchievement.getPerDayRequiredSaleQuantity() != null ? targetVsAchievement.getPerDayRequiredSaleQuantity() : "0"));

            keyFour.setText(R.string.mtdSales);
            keyFourValue.setText(String.valueOf(targetVsAchievement.getMtdSales() != null ? targetVsAchievement.getMtdSales() : "0"));

        }

//        if (targetVsAchievement.getTargetAmount() != null){
//
//            keyOne.setText(R.string.targetAmount);
//            keyOneValue.setText(String.valueOf(targetVsAchievement.getTargetAmount() != null ? targetVsAchievement.getTargetAmount() : "0"));
//
//            keyTwo.setText(R.string.achieved_amount);
//            keyTwoValue.setText(String.valueOf(targetVsAchievement.getAchievedAmountPercentage() != null ? targetVsAchievement.getAchievedAmountPercentage() : "0"));
//
//            keyThree.setText(R.string.perDayAmount);
//            keyThreeValue.setText(String.valueOf(targetVsAchievement.getPerDayRequiredSaleAmount() != null ? targetVsAchievement.getPerDayRequiredSaleAmount() : "0"));
//
//        }else if (targetVsAchievement.getTargetQuantity() != null){
//
//            keyOne.setText(R.string.targetQuantity);
//            keyOneValue.setText(String.valueOf(targetVsAchievement.getTargetQuantity() != null ? targetVsAchievement.getTargetQuantity() : "0" ));
//
//            keyTwo.setText(R.string.achieved_quantity);
//            keyTwoValue.setText(String.valueOf(targetVsAchievement.getAchievedQuantityPercentage() != null ? targetVsAchievement.getAchievedQuantityPercentage() : "0"));
//
//            keyThree.setText(R.string.perDayQuantity);
//            keyThreeValue.setText(String.valueOf(targetVsAchievement.getPerDayRequiredSaleQuantity()  != null ? targetVsAchievement.getPerDayRequiredSaleQuantity() : "0"));
//        }

    }

//    public void setBarChart(){
//        List<BarEntry> barEntryArrayList = new ArrayList<>();
//        List<String> label = new ArrayList<>(Arrays.asList("Jan" , "Feb" , "Mar" , "Apr" , "May" , "June" , "July" , "Aug" , "Sep" , "Oct", "Nov" , "Dec"));
//
//        for (int i=0; i<label.size(); i++){
//            barEntryArrayList.add(new BarEntry(i , i*4));
//        }
//
//        BarDataSet barDataSet = new BarDataSet(barEntryArrayList , "Monthly Sales");
//        barDataSet.setColors(getResources().getColor(R.color.colorPrimary) , getResources().getColor(R.color.colorAccent));
//        barDataSet.setValueTextSize(10f);
//        Description description = new Description();
//        description.setText("Months");
//        barChart.setDescription(description);
//        BarData barData = new BarData(barDataSet);
//        barChart.setData(barData);
//
//        XAxis xAxis = barChart.getXAxis();
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(label));
//
//        xAxis.setPosition(XAxis.XAxisPosition.TOP);
//        xAxis.setDrawGridLines(false);
//        xAxis.setDrawAxisLine(false);
//        xAxis.setGranularity(1f);
//        xAxis.setTextSize(10f);
//        xAxis.setLabelCount(label.size());
//        barChart.animateY(2000);
//        barChart.setTouchEnabled(false);
//        barChart.getXAxis().setSpaceMin(0.5f);
//        barChart.invalidate();
//    }

    @OnClick({R.id.btnStartDay, R.id.btnDownload, R.id.btnPlannedCall, R.id.btnReports, R.id.btnUpload, R.id.btnEndDay})
    public void onMainMenuClick(View view) {
        switch (view.getId()) {
            case R.id.btnStartDay:
                if (PreferenceUtil.getInstance(this).getWorkSyncData().isDayStarted())
                    showMessage(getString(R.string.already_started_day));
                else {
                    showProgress();
                    viewModel.startDay();
                }
                break;
            case R.id.btnDownload:
                if (!PreferenceUtil.getInstance(this).getWorkSyncData().isDayStarted()) {
                    showMessage(Constant.ERROR_DAY_NO_STARTED);
                    return;
                }
                AlertDialogManager.getInstance().showVerificationAlertDialog(this, getString(R.string.update_routes_title),
                        getString(R.string.update_routes_msg)
                        , verified -> {
                            if (verified) {
//                                showProgress();
                                PreferenceUtil.getInstance(this).saveConfig(null);
                                viewModel.download();
                            }
                        });

                break;
            case R.id.btnPlannedCall:
                if (!PreferenceUtil.getInstance(this).getWorkSyncData().isDayStarted()) {
                    showMessage(Constant.ERROR_START_DAY_FIRST);
                    return;
                }

                Integer priceConditionClass = viewModel.priceConditionClassValidation();
                Integer priceCondition = viewModel.priceConditionValidation();
                Integer priceConditionType = viewModel.priceConditionTypeValidation();

                if (priceConditionClass != 0 && priceCondition != 0 && priceConditionType != 0)
                    RoutesActivity.start(this);
                else{
                    Toast.makeText(this, "Please download data", Toast.LENGTH_SHORT).show();
                }
//                OutletListActivity.start(this);
                break;
            case R.id.btnReports:

                AlertDialogManager.getInstance().showReportsSelectionDialog(this, "Select Report",
                        object -> {
                            if (object.getId() == 0)
                                ReportsActivity.start(this);
                            else
                                StockActivity.start(this);
                        });

                break;
            case R.id.btnUpload:
                findViewById(R.id.btnUpload).setClickable(false);
                if (!PreferenceUtil.getInstance(this).getWorkSyncData().isDayStarted()) {
                    showMessage(Constant.ERROR_DAY_NO_STARTED);
                    return;
                }

                if (networkManager.isWorking()){
                    viewModel.handleMultipleSyncOrder();
                }else{
                    findViewById(R.id.btnUpload).setClickable(true);
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
//
                break;
            case R.id.btnEndDay:
                if (!PreferenceUtil.getInstance(this).getWorkSyncData().isDayStarted()) {
                    showMessage(Constant.ERROR_DAY_NO_STARTED);
                    return;
                }


//                viewModel.findAllOutlets().flatMap(outlets -> {
//                    if (outlets.size() > 0){
//                        return viewModel.findOutletsWithPendingTasks();
//                    }
//                    return null;
//                }).subscribe(outlets -> {
//
//                    if (outlets == null && outlets.size() > 0 && PreferenceUtil.getInstance(this).getConfig() != null && PreferenceUtil.getInstance(this).getConfig().getEndDayOnPjpCompletion()) {
//                        viewModel.getErrorMsg().postValue("Please complete your tasks");
//                    } else {
//                        viewModel.getEndDayLiveData().postValue(true);
//                    }
//                });
                viewModel.findAllOutlets().subscribe( outletsSize -> {

                    if(outletsSize.size() > 0){
                        viewModel.findOutletsWithPendingTasks().subscribe(outlets -> {
                            if (outlets.size() > 0 && PreferenceUtil.getInstance(this).getConfig() != null && PreferenceUtil.getInstance(this).getConfig().getEndDayOnPjpCompletion()) {
                                viewModel.getErrorMsg().postValue("Please complete your tasks");
                            } else if (PreferenceUtil.getInstance(this).getConfig() != null){
                                viewModel.getEndDayLiveData().postValue(true);
                            }
                        });
                    }
                });

                break;
        }
    }

    public void setObservers() {
        viewModel.isLoading().observe(this, this::setProgress);
        viewModel.getErrorMsg().observe(this, this::showMessage);
        viewModel.onStartDay().observe(this, aBoolean -> {
            if (aBoolean) {
                findViewById(R.id.btnStartDay).setClickable(false);
                findViewById(R.id.btnStartDay).setAlpha(0.5f);

                if(PreferenceUtil.getInstance(this).getWorkSyncData().getSyncDate() != 0){
                    String date = Util.formatDate(Util.DATE_FORMAT_3, PreferenceUtil.getInstance(this).getWorkSyncData().getSyncDate());
                    AlertDialogManager.getInstance().showAlertDialog(this, "Day Started! ( " + date + " )", "Your day has been started");
                    tvRunningDay.setText("( " + date + " )");
                    tvRunningDay.setVisibility(View.VISIBLE);
                }

            } else {
                findViewById(R.id.btnStartDay).setClickable(true);
                findViewById(R.id.btnStartDay).setAlpha(1.0f);
                WorkStatus status = new WorkStatus(0);
                PreferenceUtil.getInstance(this).saveWorkSyncData(status);
                tvRunningDay.setVisibility(View.GONE);
            }
        });


        viewModel.getTargetVsAchievement().observe(this, aBoolean -> {
            hideProgress();
            if (aBoolean)
                setTargetVsAchievement(new Gson().fromJson(PreferenceUtil.getInstance(this).getTargetAchievement(), TargetVsAchievement.class));
        });

      /*  viewModel.dayStarted().observe(this, aBoolean -> {
            if(aBoolean){
                findViewById(R.id.btnStartDay).setClickable(false);
                findViewById(R.id.btnStartDay).setAlpha(0.5f);
                String date = Util.formatDate(Util.DATE_FORMAT_3,PreferenceUtil.getInstance(this).getWorkSyncData().getSyncDate());
                tvRunningDay.setText("( "+date+" )");
                tvRunningDay.setVisibility(View.VISIBLE);
            }
        });*/

        viewModel.dayEnded().observe(this, aBoolean -> {
            if (aBoolean) {
                findViewById(R.id.btnEndDay).setClickable(false);
                findViewById(R.id.btnEndDay).setAlpha(0.5f);
                tvRunningDay.setVisibility(View.GONE);
            }
        });

        viewModel.getEndDayLiveData().observe(this, aBoolean -> {
            if (aBoolean){
                String endDate = Util.formatDate(Util.DATE_FORMAT_2, PreferenceUtil.getInstance(this).getWorkSyncData().getSyncDate());
                AlertDialogManager.getInstance().showVerificationAlertDialog(this, getString(R.string.day_closing_title).concat(" ( " + endDate + " )"),
                        getString(R.string.end_day_msg)
                        , verified -> {
                            if (verified)
                                viewModel.updateDayEndStatus();
                        });
            }
        });
        viewModel.appUpdateLiveData().observe(this, appUpdateModel -> {
            boolean appNeedToUpdate = AppUpdater.getInstance().apkChanged(appUpdateModel);
            if (appNeedToUpdate)
                ApkDownloader.getInstance().downloadApk(this, appUpdateModel);
            else
                showMessage(getString(R.string.already_updated));
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress() {
        showProgressD(this, true);
    }

    @Override
    public void hideProgress() {
        hideProgressD();
    }

    private void setProgress(boolean isLoading) {
        if (isLoading) {
            findViewById(R.id.btnUpload).setClickable(false);
            showProgress();
        } else {
            findViewById(R.id.btnUpload).setClickable(true);
            findViewById(R.id.btnEndDay).setClickable(true);
            hideProgress();
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message+"", Toast.LENGTH_LONG).show();
    }


    BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (downloadId == -1)
                return;

            // query download status
            Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {

                    // download is successful
                    String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    File file = new File(Uri.parse(uri).getPath());
                    AppUpdater.getInstance().installApk(ctxt, file);
                    hideProgress();

                } else {
                    hideProgress();
                    // download is assumed cancelled
                    Toast.makeText(ctxt, getString(R.string.download_cancel), Toast.LENGTH_SHORT).show();
                }
            } else {
                hideProgress();
                // download is assumed cancelled
                Toast.makeText(ctxt, getString(R.string.download_cancel), Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1122) {

            switch (requestCode){

                case RESULT_OK:

                    Toast.makeText(this, "Downloading latest app", Toast.LENGTH_SHORT).show();
                    break;
                case ActivityResult.RESULT_IN_APP_UPDATE_FAILED :
                case RESULT_CANCELED:
                    checkUpdate();
                    break;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability()
//                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
//            ) {
//                // If an in-app update is already running, resume the update.
//
//                try {
//                    appUpdateManager.startUpdateFlowForResult(
//                            appUpdateInfo,
//                            IMMEDIATE,
//                            this,
//                            1122
//                    );
//                } catch (IntentSender.SendIntentException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadReceiver != null) {
            unregisterReceiver(downloadReceiver);
        }
    }
}
