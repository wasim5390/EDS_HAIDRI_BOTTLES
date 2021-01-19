package com.optimus.eds.ui.scanner;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import com.google.zxing.Result;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.utils.PermissionUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created By apple on 4/30/19
 */
public class ScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private final String TAG = ScannerActivity.class.getSimpleName();

    String tag = TAG;
    @BindView(R.id.scanner)
    ZXingScannerView mScannerView;

    @Override
    public int getID() {
        return R.layout.activity_scanner;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.scan_barcode));
        PermissionUtil.requestPermission(this, Manifest.permission.CAMERA,
                new PermissionUtil.PermissionCallback() {
                    @Override
                    public void onPermissionsGranted(String permission) {

                        mScannerView.setAspectTolerance(0.5f);
                        mScannerView.setAutoFocus(true);
                    }

                    @Override
                    public void onPermissionsGranted() {

                    }

                    @Override
                    public void onPermissionDenied() {
                        finish();
                    }
                });
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        handoverResponse(rawResult.getText());
        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }

    public void handoverResponse(String response) {
        setResult(RESULT_OK, getIntent().putExtra(KEY_SCANNER_RESULT, response));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }
}
