package com.optimus.eds.ui.merchandize;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.constant.ImageProvider;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.BuildConfig;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.ui.order.OrderBookingActivity;
import com.optimus.eds.ui.camera.ImageCropperActivity;
import com.optimus.eds.ui.merchandize.asset_verification.AssetsVerificationActivity;
import com.optimus.eds.ui.merchandize.planogaram.ImageDialog;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.watermark.androidwm.WatermarkBuilder;
import com.watermark.androidwm.bean.WatermarkText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class OutletMerchandiseActivity extends BaseActivity {

    @BindView(R.id.rvBeforeMerchandize)
    RecyclerView recyclerViewBefore;
    @BindView(R.id.rvAfterMerchandise)
    RecyclerView recyclerViewAfter;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.btnAfterMerchandise)
    Button btnAfterMerchandise;
    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.tvName)
    TextView tvOutletName;
    @BindView(R.id.etRemarks)
    EditText etRemarks;

    Outlet outlet;
    private MerchandiseAdapter merchandiseAdapterBefore,merchandiseAdapterAfter;
    private Long outletId;
    private static final int REQUEST_CODE_IMAGE = 0x0005;
    private static final int REQUEST_CODE=0x1100;

    MerchandiseViewModel viewModel;
    int type=0;
    ImageDialog dialogFragment;

    boolean isAssets = true , assetsVerified = true;

    private String mImagePath;
    private Uri mImageUri = null;
    //File for capturing camera images
    private File mFileTemp;

    public static void start(Context context,Long outletId, int requestCode) {
        Intent starter = new Intent(context, OutletMerchandiseActivity.class);
        starter.putExtra("OutletId",outletId);
        ((Activity)context).startActivityForResult(starter,requestCode);
    }

    @Override
    public int getID() {
        return R.layout.activity_merchandize;
    }

    @Override
    public void created(Bundle savedInstanceState) {

        ButterKnife.bind(this);
        setToolbar(getString(R.string.merchandizing));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        initMerchandiseAdapter();
        outletId =  getIntent().getLongExtra("OutletId",0);
        viewModel = ViewModelProviders.of(this).get(MerchandiseViewModel.class);
        viewModel.loadOutlet(outletId).observe(this, outlet -> {
            if (outlet != null){
                this.outlet = outlet;
                PreferenceUtil.getInstance(this).setAssetsScannedInLastMonth(outlet.getAssetsScennedInTheLastMonth());
                onOutletLoaded(outlet);
            }
        });
        viewModel.loadAssets(outletId);
        viewModel.loadMerchandise(outletId).observe(this,merchandise -> {
            etRemarks.setText(merchandise.getRemarks());
            updateMerchandiseList(merchandise.getMerchandiseImages());

        });

        viewModel.getAssets().observe(this,assets -> {
            if(assets.isEmpty()){
                findViewById(R.id.btnAssetVerification).setClickable(false);
                findViewById(R.id.btnAssetVerification).setAlpha(0.5f);
                ((AppCompatButton)findViewById(R.id.btnAssetVerification)).setText("No Assets");
                isAssets = false;
            }else{
                isAssets = true;
                int assetVerified = 0;
                for (Asset asset : assets){
                    if (asset.getVerified())
                        assetVerified++;
                }
                if (assetVerified == assets.size())
                    PreferenceUtil.getInstance(this).setAssetsScannedInLastMonth(true);
            }

        });

        viewModel.getMerchandiseImages().observe(this, this::updateMerchandiseList);


        viewModel.isSaved().observe(this, aBoolean -> {
            if(aBoolean && assetsVerified){
                OrderBookingActivity.start(OutletMerchandiseActivity.this,outletId,REQUEST_CODE);
//                finish();
            }else{
                Intent intent = getIntent();
                intent.putExtra(WITHOUT_VERIFICATION, true);
                intent.putExtra(Constant.EXTRA_PARAM_NO_ORDER_FROM_BOOKING, true);
                intent.putExtra(Constant.EXTRA_PARAM_OUTLET_ID, outletId);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        viewModel.isInProgress().observe(this, this::setProgress);

        viewModel.enableAfterMerchandiseButton().observe(this, aBoolean -> {
            btnAfterMerchandise.setEnabled(aBoolean);
            btnAfterMerchandise.setAlpha(aBoolean?1.0f:0.5f);
        });
        viewModel.enableNextButton().observe(this, aBoolean -> {
            btnNext.setEnabled(aBoolean);
            btnNext.setAlpha(aBoolean?1.0f:0.5f);
        });

        viewModel.lessImages().observe(this, aBoolean ->{
            Toast.makeText(OutletMerchandiseActivity.this,"At least 2 images required",Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    private void onOutletLoaded(Outlet outlet) {
        tvOutletName.setText(outlet.getOutletName().concat(" - "+ outlet.getLocation()));


    }
    public void removeImage(MerchandiseImage item){
        viewModel.removeImage(item);

    }

    private void updateMerchandiseList(List<MerchandiseImage> merchandiseImages) {
        List<MerchandiseImage> merchandiseImagesBefore = new ArrayList<>();
        List<MerchandiseImage> merchandiseImagesAfter = new ArrayList<>();
        for(MerchandiseImage image:merchandiseImages){
            if(image.getType()==MerchandiseImgType.BEFORE_MERCHANDISE)
                merchandiseImagesBefore.add(image);
            else
                merchandiseImagesAfter.add(image);
        }
        merchandiseAdapterBefore.populateMerchandise(merchandiseImagesBefore);
        merchandiseAdapterAfter.populateMerchandise(merchandiseImagesAfter);
    }

    private void setProgress(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void initMerchandiseAdapter() {

        recyclerViewBefore.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        recyclerViewBefore.setHasFixedSize(true);
        recyclerViewBefore.setNestedScrollingEnabled(false);
        merchandiseAdapterBefore = new MerchandiseAdapter(this);
        recyclerViewBefore.setAdapter(merchandiseAdapterBefore);

        recyclerViewAfter.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        recyclerViewAfter.setHasFixedSize(true);
        recyclerViewAfter.setNestedScrollingEnabled(false);
        merchandiseAdapterAfter = new MerchandiseAdapter(this);
        recyclerViewAfter.setAdapter(merchandiseAdapterAfter);

    }

    @OnClick(R.id.btnNext)
    public void onNextClick(){

        if (isAssets){
            if (PreferenceUtil.getInstance(this).getAssetScannedInLastMonth()){
                outlet.setAssetsScennedInTheLastMonth(true);
                String remarks = etRemarks.getText().toString();
                viewModel.updateOutlet(outlet);
                viewModel.insertMerchandiseIntoDB(outletId,remarks , outlet.getStatusId());
            } else{

                if (!PreferenceUtil.getInstance(this).getAssetWithOutVerified()){
                    new AlertDialog.Builder(this)
                            .setTitle("Info")
                            .setMessage("Please scan all assets to proceed.")

                            .setPositiveButton("Scan Again", (dialog, which) -> {
                                dialog.dismiss();
                                AssetsVerificationActivity.start(this,outletId);
                            })
                            .setNegativeButton("Back to PJP", (dialog, which) -> {

                                dialog.dismiss();
                                new AlertDialog.Builder(this)
                                        .setTitle("Confirmation")
                                        .setMessage("Are you sure you want to Back to PJP")

                                        .setNegativeButton("CANCEL", (dialog1, which1) -> {
                                            dialog1.dismiss();
                                        })
                                        .setPositiveButton("OK", (dialog1, which1) -> {

                                            dialog1.dismiss();

                                            String remarks = etRemarks.getText().toString();

                                            PreferenceUtil.getInstance(this).setAssetsScannedWithoutVerified(true);
                                            viewModel.updateOutlet(outlet);
                                            viewModel.insertMerchandiseIntoDB(outletId,remarks , outlet.getStatusId());

                                            assetsVerified = false;


                                        })
                                        .show();
                            })
                            .show();
                }else{
                    Toast.makeText(this, "Please scan all assets", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            String remarks = etRemarks.getText().toString();
            viewModel.insertMerchandiseIntoDB(outletId,remarks , outlet.getStatusId());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.loadAssets(outletId);

    }

    @OnClick(R.id.btnShowPlanogram)
    public void showPlanogram(){

        viewModel.getImages();

        viewModel.getPlanogaram().observe(this, strings -> {
            FragmentManager fm = getSupportFragmentManager();
            dialogFragment = ImageDialog.newInstance(strings);
            dialogFragment.show(fm, "Dialog");
        });
    }

    @OnClick(R.id.btnAssetVerification)
    public void coolerVerification(){
        AssetsVerificationActivity.start(this,outletId);
    }

    @OnClick(R.id.btnBeforeMerchandise)
    public void onBeforeMerchandiseClick(){


            type= MerchandiseImgType.BEFORE_MERCHANDISE;
            actionPic(Constant.IntentExtras.ACTION_CAMERA);

    }

    @OnClick(R.id.btnAfterMerchandise)
    public void onAfterMerchandiseClick(){
        type=MerchandiseImgType.AFTER_MERCHANDISE;
        actionPic(Constant.IntentExtras.ACTION_CAMERA);
    }

    /**
     * Navigate to ImageCropperActivity with provided action {camera-action,gallery-action}
     * @param action
     */
    private void actionPic(String action) {
//        Intent intent = new  Intent(this, ImageCropperActivity.class);
//        intent.putExtra("ACTION", action);
//        startActivityForResult(intent, REQUEST_CODE_IMAGE);

        ImagePicker.Companion.with(this)
                .provider(ImageProvider.CAMERA) //StudentProfile can only select image from Camera
                .maxResultSize(800, 800) //Final image resolution will be less than 1080 x 1080(Optional)
                .start(); //Default Request Code is ImagePicker.REQUEST_CODE

//        requestCameraPermission();
    }

    private void requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ,Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // check if all permissions are granted
                if (report.areAllPermissionsGranted()) {
                    takePic();
                }

                if(report.getDeniedPermissionResponses().size()> 0){
                    Log.d("Heloo" , "Denied");
                }
                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    // show alert dialog navigating to Settings
                    showSettingsDialog(OutletMerchandiseActivity.this);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        })
                .onSameThread()
                .check();

    }

    private void takePic() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> list = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);

        for (ApplicationInfo applicationInfo : list){
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1){
                if(applicationInfo.loadLabel(pm).toString().equalsIgnoreCase("Camera")) {
                   takePictureIntent.setPackage(applicationInfo.packageName);
                    break;
                }
            }
        }

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Continue only if the File was successfully created
            mFileTemp = createImageFile();
            if (mFileTemp != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        mFileTemp);
                mImageUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("return-data",true);

                startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE);
            }
        }
    }

    private File createImageFile() {
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";

            String folder = Environment.getExternalStorageDirectory() + File.separator + "EDS/Images/";
            //Create Dir folder if it does not exist
            File storageDir = new File(folder);

            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            File  image = File.createTempFile(imageFileName, ".jpg", storageDir);
            mImagePath = image.getAbsolutePath();
            return image;
        } catch (IOException ex) {
            // Error occurred while creating the File

        }
        return null;
    }

    public File createWaterMark(Bitmap imageBitmap){

        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String date = simpleDateFormat.format(new Date());
        WatermarkText watermarkText = new WatermarkText(date)
                .setTextColor(Color.RED)
                .setTextAlpha(150)
                .setTextSize(20);

        Bitmap bitmap = WatermarkBuilder
                .create(this, imageBitmap)
                .loadWatermarkText(watermarkText)
                .getWatermark()
                .getOutputImage();

//        return saveImageToExternalStorage(bitmap);
        return Util.saveToInternalStorage(bitmap , this);
    }

    public void saveImageToExternalStorage(Bitmap image) {
        String fullPath = mFileTemp.getAbsolutePath() ;
        try
        {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream fOut = null;

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";


            File file = new File(fullPath);
            if(file.exists())
                file.delete();
            file.createNewFile();
            fOut = new FileOutputStream(file);
            // 100 means no compression, the lower you go, the stronger the compression
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            mImagePath = file.getAbsolutePath();
        }
        catch (Exception e)
        {
            Log.e("saveToExternalStorage()", e.getMessage());
        }
    }


    private Target onActivityTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap rotatedBitmap, Picasso.LoadedFrom from) {

            runOnUiThread(() ->{
                File file  = createWaterMark(rotatedBitmap);
                viewModel.saveImages(file.getPath() , type);
            });
        }
        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            switch (requestCode){

//                case REQUEST_CODE_IMAGE:
//                    Bitmap bitmap = null;
//                    try {
//                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
//                        bitmap = Util.captureImageOrientation(mImagePath , bitmap);
//                        if (bitmap != null)
//                            createWaterMark(bitmap);
//                        Bitmap orientationBitmap = BitmapFactory.decodeFile(mImagePath);
//                        Util.captureImageOrientation(mImagePath , orientationBitmap);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
////                    String imagePath = data.getStringExtra(Constant.IntentExtras.IMAGE_PATH);
//                    if(mImagePath!=null) {
//                        compress(mImagePath,type);
//                    }
//                    break;

                case ImagePicker.REQUEST_CODE:
//                    Bitmap simpleBitmap = null;
//                    try {
//                        simpleBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Bitmap rotatedBitmap = null;
//                    File file = Util.saveToInternalStorage(simpleBitmap , this);
//                    simpleBitmap = BitmapFactory.decodeFile(file.getPath());
//                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q){
//                        rotatedBitmap = Util.captureImageOrientation(file.getAbsolutePath() , simpleBitmap);
//                    }else{
//                        rotatedBitmap = Util.captureImageOrientation(new File(data.getData().getPath()).getAbsolutePath(), simpleBitmap);
//                    }

//                    File rotatedFile = null;
//                    if (rotatedBitmap != null)
//                        rotatedFile =  createWaterMark(rotatedBitmap);
//                    if (rotatedFile != null)

                    Picasso.get().load(data.getData()).into(onActivityTarget);
                    break;
                case REQUEST_CODE:
                    setResult(RESULT_OK,data);
                    finish();
                    break;
            }
        }
    }

    public void compress(String actualImagePath,int type){
        File actualImage = new File(actualImagePath);
        new Compressor(this)
                .setMaxWidth(640).setMaxHeight(480).setQuality(70)
                .compressToFileAsFlowable(actualImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        file ->{
                            if(Util.moveFile(file,actualImage.getParentFile()))
                                viewModel.saveImages(actualImage.getPath(),type);
                        }, throwable -> {
                           // throwable.printStackTrace();
                            Toast.makeText(this, throwable.getMessage()+"", Toast.LENGTH_SHORT).show();
                        });
    }

    @Override
    public void onBackPressed() {
               // Toast.makeText(this, "Complete order or checkout without order!", Toast.LENGTH_SHORT).show();
        }

}
