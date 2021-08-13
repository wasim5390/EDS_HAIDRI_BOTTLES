package com.optimus.eds.ui.scanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.utils.PermissionUtil;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.optimus.eds.Constant.KEY_SCANNER_RESULT;

/**
 * Created By apple on 4/30/19
 */

public class ScannerActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        PermissionUtil.requestPermission(this, Manifest.permission.CAMERA,
                new PermissionUtil.PermissionCallback() {
                    @Override
                    public void onPermissionsGranted(String permission) {

                        IntentIntegrator integrator = new IntentIntegrator(ScannerActivity.this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_39, IntentIntegrator.CODE_128 , IntentIntegrator.CODE_93,
                                IntentIntegrator.EAN_8 , IntentIntegrator.EAN_13 , IntentIntegrator.ITF,
                                IntentIntegrator.RSS_14 , IntentIntegrator.RSS_EXPANDED , IntentIntegrator.UPC_A,
                                IntentIntegrator.UPC_E );
                        integrator.setPrompt("Scan a barcode");
                        integrator.setTorchEnabled(false);
                        integrator.setCameraId(0);  // Use a specific camera of the device
                        integrator.setBeepEnabled(false);
                        integrator.setTorchEnabled(true);
                        integrator.setBarcodeImageEnabled(true);
                        integrator.initiateScan();
                    }

                    @Override
                    public void onPermissionsGranted() {

                    }

                    @Override
                    public void onPermissionDenied() {
                        finish();
                    }
                });

//        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
//        setContentView(mScannerView);                // Set the scanner view as the content view
//
//        mScannerView.setAutoFocus(true);
//        mScannerView.setAspectTolerance(0.5f);
//
//        mScannerView.setFormats(ONE_DIMENSIONAL_FORMATS);
//
//        mScannerView.setCameraDistance(0.05f);



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                finish();
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Intent intent=new Intent();
                intent.putExtra("barCodeText",result.getContents());
                intent.putExtra("barCodeNum",result.getContents().trim());
                intent.putExtra("barCodeImage" , result.getBarcodeImagePath());

                setResult(RESULT_OK, getIntent().putExtra(KEY_SCANNER_RESULT, result.getContents()));
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

}
//public class ScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
//    private final String TAG = ScannerActivity.class.getSimpleName();
//
//    String tag = TAG;
//    @BindView(R.id.scanner)
//    ZXingScannerView mScannerView;
//
//    @Override
//    public int getID() {
//        return R.layout.activity_scanner;
//    }
//
//    @Override
//    public void created(Bundle savedInstanceState) {
//        ButterKnife.bind(this);
//        setToolbar(getString(R.string.scan_barcode));
//        PermissionUtil.requestPermission(this, Manifest.permission.CAMERA,
//                new PermissionUtil.PermissionCallback() {
//                    @Override
//                    public void onPermissionsGranted(String permission) {
//
//                        mScannerView.setAspectTolerance(0.5f);
//                        mScannerView.setAutoFocus(true);
//                    }
//
//                    @Override
//                    public void onPermissionsGranted() {
//
//                    }
//
//                    @Override
//                    public void onPermissionDenied() {
//                        finish();
//                    }
//                });
//    }
//
//    @Override
//    public void showProgress() {
//
//    }
//
//    @Override
//    public void hideProgress() {
//
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
//        mScannerView.startCamera();          // Start camera on resume
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mScannerView.stopCamera();           // Stop camera on pause
//    }
//
//    @Override
//    public void handleResult(Result rawResult) {
//        // Do something with the result here
//        Log.v(TAG, rawResult.getText()); // Prints scan results
//        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
//        handoverResponse(rawResult.getText());
//        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
//    }
//
//    public void handoverResponse(String response) {
//        setResult(RESULT_OK, getIntent().putExtra(KEY_SCANNER_RESULT, response));
//        finish();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_CANCELED) {
//            finish();
//        }
//    }
//}
