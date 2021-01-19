package com.optimus.eds.ui.camera;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.BuildConfig;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created By apple on 4/23/19
 */
public class ImageCropperActivity extends BaseActivity {

    public static final String TAG = "ImageCropperActivity";

    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";

    private final Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    @BindView(R.id.cropImageView)
    CropImageView mImageView;
    private ContentResolver mContentResolver;

    //Temp file to save cropped image
    private String mImagePath;
    private Uri mImageUri = null;
    //File for capturing camera images
    private File mFileTemp;



    @Override
    public int getID() {
        return R.layout.activity_image_cropper;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.crop_image));
        mContentResolver = getContentResolver();

        mImageView.setFixedAspectRatio(false);
        requestCameraPermission(savedInstanceState);
    }


    /**
     * Requesting camera, permission
     * Once the permission granted, starts camera
     * On permanent denial opens settings dialog
     */
    private void requestCameraPermission(final Bundle savedInstanceState) {
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
                    proceedToCrop(savedInstanceState);
                }

                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    // show alert dialog navigating to Settings
                    showSettingsDialog(ImageCropperActivity.this);
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



    /**
     * Get passed action as extras and call intent accordingly
     * @param savedInstanceState
     */
    private void proceedToCrop(Bundle savedInstanceState) {
        mFileTemp = createImageFile();
        if (savedInstanceState == null || !savedInstanceState.getBoolean("restoreState")) {
            String action = getIntent().getStringExtra("ACTION");

            if (null != action) {
                switch (action) {
                    case Constant.IntentExtras.ACTION_CAMERA:
                        getIntent().removeExtra("ACTION");
                        takePic();
                        return;
                }
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }


    /**
     * Save cropped image and send image path back to calling activity
     */
    private void saveCroppedImage() {
        saveOutput().observe(this,aBoolean -> {
            if (aBoolean) {
                Intent intent = new Intent();
                intent.putExtra(Constant.IntentExtras.IMAGE_PATH, mImagePath);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "Unable to save Image into your device.", Toast.LENGTH_LONG).show();
            }
        });

    }


    /**
     * Create temp image which will be write by camera api
     * @return
     */
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
            errored();
        }
        return null;
    }

    /**
     * Capture pic with camera
     */
    private void takePic() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Continue only if the File was successfully created
            if (mFileTemp != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        mFileTemp);
                mImageUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("return-data",true);

                startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE);
            }else{
                mFileTemp = createImageFile();
            }
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("restoreState", true);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                mImageView.setImageUriAsync(mImageUri);
            } else if (resultCode == RESULT_CANCELED) {
                userCancelled();
            } else {
                errored();
            }

        }
    }


    /**
     * Save cropped image
     * @return
     */
    private LiveData<Boolean> saveOutput() {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();

        mImageView.setOnCropImageCompleteListener((view, result) -> {
            Bitmap croppedImage=  result.getBitmap();
            if (mImageUri != null) {
                OutputStream outputStream = null;
                try {
                    outputStream = mContentResolver.openOutputStream(mImageUri);
                    if (outputStream != null) {
                        croppedImage.compress(mOutputFormat, 80, outputStream);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    mutableLiveData.postValue(false);
                } finally {
                    closeSilently(outputStream);
                }
            } else {
                Log.e(TAG, "not defined image url");
                mutableLiveData.postValue(false);
            }
            croppedImage.recycle();
            mutableLiveData.postValue(true);
        });
        mImageView.getCroppedImageAsync();
        return mutableLiveData;

    }


    public void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    public void userCancelled() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void errored() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
        finish();
    }

    @OnClick({R.id.uploadAnotherImage})
    public void upNavigate(){
        userCancelled();
    }


    @OnClick(R.id.btnSave)
    public void saveImage(){
        saveCroppedImage();
    }

}
