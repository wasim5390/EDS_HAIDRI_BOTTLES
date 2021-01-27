package com.optimus.eds.ui.cash_memo;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.ui.AlertDialogManager;
import com.optimus.eds.ui.customer_input.CustomerInputActivity;
import com.optimus.eds.ui.order.OrderBookingActivity;
import com.optimus.eds.ui.route.outlet.OutletListActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.optimus.eds.EdsApplication.getContext;
import static com.optimus.eds.utils.Util.formatCurrency;

public class CashMemoActivity extends BaseActivity {


    private static final int RES_CODE = 0x101;
    private Long outletId;


    @BindView(R.id.rvCartItems)
    RecyclerView rvCartItems;

    @BindView(R.id.tvOutletName)
    TextView tvOutletName;

    @BindView(R.id.tvGrandTotal)
    TextView tvGrandTotal;

    @BindView(R.id.tvFreeQty)
    TextView tvFreeQty;

    @BindView(R.id.tvQty)
    TextView tvQty;

    @BindView(R.id.btnNext)
    AppCompatButton btnNext;
    @BindView(R.id.btnEditOrder)
    AppCompatButton btnEditOrder;

    private CashMemoAdapter cartAdapter;
    private CashMemoViewModel viewModel;
    private boolean cashMemoEditable;
    BottomSheetDialog bottomSheetDialog;
    public static void start(Context context, Long outletId,int resCode) {
        Intent starter = new Intent(context, CashMemoActivity.class);
        starter.putExtra("OutletId",outletId);
        ((Activity)context).startActivityForResult(starter,resCode);
    }


    @Override
    public int getID() {
        return R.layout.activity_cash_memo;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        outletId =  getIntent().getLongExtra("OutletId",0);
        ButterKnife.bind(this);
        setToolbar(getString(R.string.cash_memo));
        viewModel = ViewModelProviders.of(this).get(CashMemoViewModel.class);
        initAdapter();
        setObserver();

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    private void configUi(){
        if(!cashMemoEditable){
            btnNext.setVisibility(View.GONE);
            btnEditOrder.setText("Outlets");
        }
    }

    private void setObserver(){
        viewModel.loadOutlet(outletId).observe(this, this::onOutletLoaded);
        viewModel.getOrder(outletId).observe(this, orderModel -> {
            cashMemoEditable = orderModel.getOrder().getOrderStatus() != 1;
            configUi();
            updateCart(orderModel.getOrderDetailAndCPriceBreakdowns());
            updatePricesOnUi(orderModel);


        });
    }

    private void onOutletLoaded(Outlet outlet) {
        tvOutletName.setText(outlet.getOutletName().concat(" - "+ outlet.getLocation()));
    }
    private void createBreakDownDialogSheet(List<UnitPriceBreakDown> breakDowns,Double subTotal,Double grandTotal){
        // using BottomSheetDialog
        View dialogView = getLayoutInflater().inflate(R.layout.breakdown_bottom_sheet, null);

        LinearLayout parent = dialogView.findViewById(R.id.invoice_breakdown_view);
        TextView tvSubTotal = dialogView.findViewById(R.id.sub_total);
        TextView tvTotal = dialogView.findViewById(R.id.total);
        LayoutInflater inflater =  LayoutInflater.from(getContext());
        for(UnitPriceBreakDown priceBreakDown:breakDowns) {
            Double unitPrice;
            LinearLayout rateView = (LinearLayout) inflater.inflate(R.layout.rate_child_layout, null);
            TextView title = rateView.findViewById(R.id.productRate);
            TextView rate = rateView.findViewById(R.id.tvProductRate);
            unitPrice = priceBreakDown.getBlockPrice();

            rate.setText(formatCurrency(unitPrice.doubleValue()));
            title.setText(priceBreakDown.getPriceConditionType());
            parent.addView(rateView);
        }
        tvSubTotal.setText(formatCurrency(subTotal));
        tvTotal.setText(formatCurrency(grandTotal,0));
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(dialogView);
    }
    private void updatePricesOnUi(OrderModel order){
        if(!order.getOrder().getPriceBreakDown().isEmpty()){
            createBreakDownDialogSheet(order.getOrder().getPriceBreakDown(),order.getOrder().getSubTotal(),order.getOrder().getPayable());
        }
        tvGrandTotal.setText(formatCurrency(order.getOrder().getPayable(),0));
        Long carton=0l,units=0l;
        for(OrderDetailAndPriceBreakdown detailAndPriceBreakdown:order.getOrderDetailAndCPriceBreakdowns())
        {
            Integer cQty = detailAndPriceBreakdown.getOrderDetail().getCartonQuantity();
            Integer uQty = detailAndPriceBreakdown.getOrderDetail().getUnitQuantity();
            carton+= cQty!=null?cQty:0;
            units+=uQty!=null?uQty:0;
        }
        tvQty.setText(carton +"."+ units);
        tvFreeQty.setText(String.valueOf(order.getFreeAvailableQty()));

    }


    private void initAdapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvCartItems.setLayoutManager(layoutManager);
        rvCartItems.setHasFixedSize(true);
        cartAdapter = new CashMemoAdapter(this);
        rvCartItems.setAdapter(cartAdapter);
        rvCartItems.setNestedScrollingEnabled(false);
    }


    private void updateCart(List<OrderDetailAndPriceBreakdown> products) {

        cartAdapter.populateCartItems(products, productsWithFreeItem -> {
            viewModel.updateOrder(productsWithFreeItem);
        },isAvailable -> {
            if(cashMemoEditable) {
                btnNext.setVisibility(isAvailable ? View.VISIBLE : View.GONE);
                if(!isAvailable) {
                    AlertDialogManager.getInstance()
                            .showVerificationAlertDialog(this, "Oops!", Constant.PRICING_CASHMEMO_ERROR, verified -> {
                                if (verified)
                                    finish();
                            });
                    cartAdapter.unRegisterPriceListener();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case RES_CODE:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        }
    }

    @OnClick(R.id.btnNext)
    public void navigateToCustomerInput(){
        CustomerInputActivity.start(this,outletId,RES_CODE);
    }

    @OnClick(R.id.btnEditOrder)
    public void upNavigate(){
        Intent intent = new Intent(this,cashMemoEditable?OrderBookingActivity.class: OutletListActivity.class);
        intent.putExtras(getIntent());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        this.finish();
    }

    @OnClick(R.id.header)
    public void breakDownBottomSheet(){
        if(bottomSheetDialog!=null)
        bottomSheetDialog.show();
    }


}
