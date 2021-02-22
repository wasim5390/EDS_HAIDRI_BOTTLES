package com.optimus.eds.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.optimus.eds.R;
import com.optimus.eds.model.CustomObject;
import com.optimus.eds.db.entities.Promotion;
import com.optimus.eds.model.LastOrder;
import com.optimus.eds.ui.route.PromotionAdapter;
import com.optimus.eds.ui.route.outlet.detail.LastOrderAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlertDialogManager {
    private static final AlertDialogManager ourInstance = new AlertDialogManager();

    public static AlertDialogManager getInstance() {
        return ourInstance;
    }

    private AlertDialogManager() {
    }


    /**
     * Pass List of [{@CustomObject}] and listener for accepting any from list
     * @param context
     * @param listener
     * @param options
     */
    public void showListAlertDialog(Context context,String title,ListAlertItemClickListener listener, List<CustomObject> options) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.ic_remove_box);
        builderSingle.setTitle(title);
        final ArrayAdapter<CustomObject> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_activated_1);
        arrayAdapter.addAll(options);

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            listener.onAlertItemClick(arrayAdapter.getItem(which));

        });
        builderSingle.setPositiveButton("Cancel", (dialog1, which1) -> dialog1.dismiss());
        builderSingle.show();
    }

    public void showPromotionsDialog(Context context, List<Promotion> promotions){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context );

        LayoutInflater inflater =  (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View dialogView= inflater.inflate(R.layout.promotion_dialog, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        RecyclerView promotionRecyclerView = dialogView.findViewById(R.id.promotionRecyclerView);
        TextView cancelTextView = dialogView.findViewById(R.id.cancel);

        PromotionAdapter promotionAdapter = new PromotionAdapter(promotions);

        promotionRecyclerView.setLayoutManager(new LinearLayoutManager(context , RecyclerView.VERTICAL , false));
        promotionRecyclerView.setAdapter(promotionAdapter);

        cancelTextView.setOnClickListener( view -> {
            dialog.dismiss();
        });

        int width = (int)(context.getResources().getDisplayMetrics().widthPixels * 0.80f);
        int height = (int)(context.getResources().getDisplayMetrics().heightPixels * 0.80f);

        Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void showLastOrderDialog(Context context, LastOrder lastOrder){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context );

        LayoutInflater inflater =  (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View dialogView= inflater.inflate(R.layout.last_orders, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        RecyclerView lastOrdersRecyclerView = dialogView.findViewById(R.id.lastOrdersRecyclerView);
        TextView cancelTextView = dialogView.findViewById(R.id.cancel);

        LastOrderAdapter lastOrderAdapter = new LastOrderAdapter(lastOrder.getOrderDetails());

        lastOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(context , RecyclerView.VERTICAL , false));
        lastOrdersRecyclerView.setAdapter(lastOrderAdapter);

        cancelTextView.setOnClickListener( view -> {
            dialog.dismiss();
        });

        int width = (int)(context.getResources().getDisplayMetrics().widthPixels * 0.80f);
        int height = (int)(context.getResources().getDisplayMetrics().heightPixels * 0.75f);

        Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * Provide current location and destination location for distance measurement
     * @param context
     * @param currentLocation
     * @param outletLocation
     */
    public void showLocationMissMatchAlertDialog(Context context, Location currentLocation,Location outletLocation) {
        double distance = currentLocation.distanceTo(outletLocation);
        BigDecimal dis = new BigDecimal(distance).setScale(2,RoundingMode.HALF_UP);
        LayoutInflater inflater  = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.location_mismatch_dialog,null);
        ((TextView)view.findViewById(R.id.tvOutletLocation)).setText(outletLocation.getLatitude()+" / "+outletLocation.getLongitude());
        ((TextView)view.findViewById(R.id.tvYourLocation)).setText(currentLocation.getLatitude()+" / "+currentLocation.getLongitude());
        ((TextView)view.findViewById(R.id.tvDistance)).setText(String.valueOf(dis).concat(" meters"));
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        builderSingle.setTitle(context.getString(R.string.incorrect_location));
        builderSingle.setView(view);
        builderSingle.setPositiveButton("Ok", (dialog1, which1) -> dialog1.dismiss());
        builderSingle.show();
    }

    /**
     * Provide current reason for no order
     * @param context
     */
    public void showNoOrderAlertDialog(Context context,NoSaleReasonListener reasonListener) {

        LayoutInflater inflater  = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_input,null);
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle(context.getString(R.string.no_order_reason));
        builderSingle.setView(view);
        builderSingle.setPositiveButton("Ok", (dialog1, which1) ->{
            String reason = ((AppCompatEditText) view.findViewById(R.id.etNoSaleReason)).getText().toString();
            if(reason.isEmpty())
                Toast.makeText(context, context.getString(R.string.please_enter_reason_for_no_sale), Toast.LENGTH_SHORT).show();
            else {
                reasonListener.onNoSaleReasonEntered(reason);
                dialog1.dismiss();
            }
        });
        builderSingle.show();
    }

    /**
     * Provide current reason for no order
     * @param context
     */
    public void showVerificationAlertDialog(Context context,String title,String message,VerificationListener mListener) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle(title);
        builderSingle.setMessage(message);
        builderSingle.setPositiveButton("Yes", (dialog1, which1) ->{
                mListener.onVerified(true);
                dialog1.dismiss();
        });
        builderSingle.setNegativeButton("No",(dialog, which) -> {
            mListener.onVerified(false);
            dialog.dismiss();
        });
        builderSingle.show();
    }

    /**
     * This Dialog opens from MainActivity on Reports Click
     * Gives user option to navigate to selected Reports Activity
     * @param context
     */
    public void showReportsSelectionDialog(Context context,String title,ListAlertItemClickListener mListener) {
        List<CustomObject> mList = new ArrayList<>();
        mList.add(new CustomObject(0L,"KPI Reports"));
        mList.add(new CustomObject(1L,"Stock Reports"));

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle(title);
        String[] array = new String[]{"KPI Reports","Stock Reports"};
        builderSingle.setItems(array,(dialog, which) -> {
            mListener.onAlertItemClick(mList.get(which));
            dialog.dismiss();
        });

        builderSingle.show();
    }

    public void showAlertDialog(Context context,String title,String message) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle(title);
        builderSingle.setMessage(message);
        builderSingle.setPositiveButton("Ok", (dialog1, which1) ->{
            dialog1.dismiss();
        });
        builderSingle.show();
    }

    public interface ListAlertItemClickListener{
        void onAlertItemClick(CustomObject object);
    }

    public interface NoSaleReasonListener{
        void onNoSaleReasonEntered(String reason);
    }

    public interface VerificationListener{
        void onVerified(boolean verified);
    }

}
