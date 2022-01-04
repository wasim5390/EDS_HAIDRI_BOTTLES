package com.optimus.eds.ui.customer_input;

import android.app.Activity;
import android.app.DatePickerDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.source.UploadOrdersService;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.util.Calendar;
import java.util.Date;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.optimus.eds.utils.Util.formatCurrency;


public class CustomerInputActivity extends BaseActivity implements SignaturePad.OnSignedListener {

    @BindView(R.id.signaturePad)
    SignaturePad signaturePad;
    @BindView(R.id.tvName)
    TextView tvOutletName;
    @BindView(R.id.tvOrderAmount)
    TextView tvOrderAmount;

    @BindView(R.id.tvCnic)
    TextView tvCNic;
    @BindView(R.id.tvStrn)
    TextView tvStrn;
    @BindView(R.id.customer_mobile_number)
    TextView tvMobile;

    @BindView(R.id.etMobileNumber)
    EditText etMobileNumber;
    @BindView(R.id.customer_cnic)
    EditText etCnic;
    @BindView(R.id.deliveryDateEditText)
    AppCompatEditText etDeliveryDate;
    @BindView(R.id.customer_strn)
    EditText etStrn;
    @BindView(R.id.etCustomerRemarks)
    EditText etCustomerRemarks;

    Bitmap signature = null;
    private Long outletId;
    private Integer statusId;
    private CustomerInputViewModel viewModel;

    Long deliveryDateInMillis = 0L;

    DatePickerDialog datePickerDialog;

    public static void start(Context context, Long outletId, int resCode) {
        Intent starter = new Intent(context, CustomerInputActivity.class);
        starter.putExtra("OutletId", outletId);
        ((Activity) context).startActivityForResult(starter, resCode);
    }

    @Override
    public int getID() {
        return R.layout.activity_customer_input;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.customer_input));
        viewModel = ViewModelProviders.of(this).get(CustomerInputViewModel.class);
        outletId = getIntent().getLongExtra("OutletId", 0);
        signaturePad.setMaxWidth(2);
        signaturePad.setOnSignedListener(this);
        setObserver();

        if (PreferenceUtil.getInstance(this).getDeliveryDate() != -1) {
            deliveryDateInMillis = PreferenceUtil.getInstance(this).getDeliveryDate();
            etDeliveryDate.setText(Util.formatDate("dd/MM/yyyy", PreferenceUtil.getInstance(this).getDeliveryDate()));
        }
    }

    @Override
    public void showProgress() {
        super.showProgressD(this, false);
    }

    @Override
    public void hideProgress() {
        super.hideProgressD();
    }

    private void setObserver() {
        viewModel.loadOutlet(outletId).observe(this, this::onOutletLoaded);
        viewModel.findOrder(outletId);
        viewModel.order().observe(this, this::onOrderLoaded);
        viewModel.getStartUploadService().observe(this, outletId -> {
            if (outletId != null){
                //                UploadOrdersService.startUploadService(getApplication(), outletId);
                viewModel.postOrder();
            } else{
                setResult(RESULT_OK);
                finish();
            }
        });
        viewModel.orderSaved().observe(this, aBoolean -> {
            if (aBoolean) {
                setResult(RESULT_OK);
                finish();
//          //      CustomerComplaintsActivity.start(this);
            } else
                findViewById(R.id.btnNext).setEnabled(true);
        });
        viewModel.isSaving().observe(this, this::setProgress);
        viewModel.showMessage().observe(this, this::showMsg);
         LocalBroadcastManager.getInstance(this).registerReceiver(orderUploadSuccessReceiver,new IntentFilter(Constant.ACTION_ORDER_UPLOAD));
    }


    private void onOutletLoaded(Outlet outlet) {

        statusId = outlet.getStatusId();

        tvOutletName.setText(outlet.getOutletName().concat(" - " + outlet.getLocation()));

        if (PreferenceUtil.getInstance(this).getHideCustomerInfo() != null) {

            if (!PreferenceUtil.getInstance(this).getHideCustomerInfo()) {

                etMobileNumber.setText(outlet.getMobileNumber());
                etCnic.setText(outlet.getCnic());
                etStrn.setText(outlet.getStrn());
            } else {
                etMobileNumber.setVisibility(View.GONE);
                etCnic.setVisibility(View.GONE);
                etStrn.setVisibility(View.GONE);

                tvCNic.setVisibility(View.GONE);
                tvStrn.setVisibility(View.GONE);
                tvMobile.setVisibility(View.GONE);
            }
        }
    }


    private void onOrderLoaded(OrderModel orderModel) {
        tvOrderAmount.setText(formatCurrency(orderModel.getOrder().getPayable()));
    }


    private void showMsg(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void setProgress(boolean isLoading) {
        if (isLoading) {
            showProgress();
        } else {
            findViewById(R.id.btnNext).setEnabled(false);
            hideProgress();
        }
    }


    @OnClick(R.id.btnClearSignature)
    public void clearSignatureClick() {
        signaturePad.clear();
    }

    @OnClick(R.id.deliveryDateEditText)
    public void deliveryDateClickListener() {

        Calendar calendar = null;
        int day, month, year;

        if (PreferenceUtil.getInstance(this).getDeliveryDate() != -1)
            calendar = Util.getCalendarFromMilliseconds(PreferenceUtil.getInstance(this).getDeliveryDate());

        if (calendar == null) {
            final Calendar cldr = Calendar.getInstance();
            day = cldr.get(Calendar.DAY_OF_MONTH);
            month = cldr.get(Calendar.MONTH);
            year = cldr.get(Calendar.YEAR);
        } else {

            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
        }

        // date picker dialog
        datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    etDeliveryDate.setText( (monthOfYear + 1) + "/"+ dayOfMonth   + "/" + year1);

                    final Calendar cldr = Calendar.getInstance();
                    cldr.set(year , (monthOfYear + 1) , dayOfMonth);
                    deliveryDateInMillis = cldr.getTimeInMillis();

                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    @OnClick(R.id.btnNext)
    public void navigateToComplaints() {
        if (signaturePad.isEmpty()) {
            Toast.makeText(this, "Please take customer signature", Toast.LENGTH_SHORT).show();
            return;
        }

        if (etDeliveryDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select delivery date", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.btnNext).setEnabled(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to order?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            String mobileNumber = etMobileNumber.getText().toString();
            String remarks = etCustomerRemarks.getText().toString();
            String cnic = etCnic.getText().toString();
            String strn = etStrn.getText().toString();
            String base64Sign = Util.compressBitmap(signature);

            showProgress();
            findViewById(R.id.btnNext).setEnabled(false);
            viewModel.saveOrder(mobileNumber, remarks, cnic, strn, base64Sign, deliveryDateInMillis, statusId);
        });
        builder.setNegativeButton("Cancel", (dialog, which) ->{
            findViewById(R.id.btnNext).setEnabled(true);
            dialog.cancel();
        } );
        builder.setCancelable(false);
        builder.show();

    }

    @Override
    public void onStartSigning() {

    }

    @Override
    public void onSigned() {
        signature = signaturePad.getSignatureBitmap();

    }

    @Override
    public void onClear() {
        signature = null;
    }


    @Override
    protected void onDestroy() {
        if (orderUploadSuccessReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(orderUploadSuccessReceiver);
        }
        super.onDestroy();
    }


    private BroadcastReceiver orderUploadSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constant.ACTION_ORDER_UPLOAD)){
                MasterModel response = (MasterModel) intent.getSerializableExtra("Response");
                hideProgress();
                if(response!=null && response.isSuccess()){
                    hideProgress();
                    Toast.makeText(context, response.isSuccess()?"Order Uploaded Successfully!":response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                    viewModel.setOrderSaved(true);
                    viewModel.scheduleMerchandiseJob(getApplication(), outletId, PreferenceUtil.getInstance(getApplication()).getToken() , statusId);
                }else{
                    findViewById(R.id.btnNext).setEnabled(true);
                    Toast.makeText(context, response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}
