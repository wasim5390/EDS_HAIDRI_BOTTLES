package com.optimus.eds.ui.merchandize;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.ui.order.OrderBookingActivity;
import com.optimus.eds.ui.camera.ImageCropperActivity;
import com.optimus.eds.ui.merchandize.asset_verification.AssetsVerificationActivity;
import com.optimus.eds.ui.merchandize.planogaram.ImageDialog;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.io.File;
import java.util.ArrayList;
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
            }

        });

        viewModel.getMerchandiseImages().observe(this, merchandiseItems -> {
            
            updateMerchandiseList(merchandiseItems);
        });


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

                                        .setPositiveButton("CANCEL", (dialog1, which1) -> {
                                            dialog1.dismiss();
                                        })
                                        .setNegativeButton("OK", (dialog1, which1) -> {

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
        Intent intent = new  Intent(this, ImageCropperActivity.class);
        intent.putExtra("ACTION", action);
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            switch (requestCode){

                case REQUEST_CODE_IMAGE:
                    String imagePath = data.getStringExtra(Constant.IntentExtras.IMAGE_PATH);
                    if(imagePath!=null) {
                        compress(imagePath,type);
                    }
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
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        });
    }

    @Override
    public void onBackPressed() {
               // Toast.makeText(this, "Complete order or checkout without order!", Toast.LENGTH_SHORT).show();
        }

}
