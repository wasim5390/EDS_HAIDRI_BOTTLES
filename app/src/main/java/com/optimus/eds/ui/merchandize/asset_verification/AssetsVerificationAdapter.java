package com.optimus.eds.ui.merchandize.asset_verification;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.model.AssetStatus;
import com.optimus.eds.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By apple on 4/30/19
 */
public class AssetsVerificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Asset> assetList;
    private AssetVerificationStatusListener listener;
    private int check=0;
    private int assetScanning = 0;
    private List<AssetStatus> assetStatuses;
    private Integer COOLER_SCANNED = 5;

    public AssetsVerificationAdapter(Context context, List<AssetStatus> assetStatuses , AssetVerificationStatusListener listener) {
        this.mContext = context;
        this.listener = listener;
        this.assetList = new ArrayList<>();
        this.assetStatuses = assetStatuses;
    }

    public void populateAssets(List<Asset> assets) {
        this.assetScanning = 0;
        this.check = 0;
        this.assetList = assets;
        notifyDataSetChanged();
    }

    public List<String> getReasons(boolean approved){
        List<String> reasons = new ArrayList<>();
        if(approved)
            reasons.add(mContext.getString(R.string.scanned));
        else
            reasons.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.asset_verification)));

        return reasons;

    }

    @Override
    public AssetsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.assets_verification_list_item, parent, false);

        return new AssetsListHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AssetsListHolder assetsListHolder = (AssetsListHolder) holder;
        Asset asset = assetList.get(position);
//        List<String> reasons = getReasons(asset.getVerified());
        ((AssetsListHolder) holder).codeTv.setText(String.valueOf(asset.getSerialNumber()));
        ((AssetsListHolder) holder).statusTextView.setText(asset.getVerified()?"Verified":"Pending");

        ArrayAdapter<AssetStatus> spinnerArrayAdapter = new ArrayAdapter<AssetStatus>
                (mContext, R.layout.spinner_item, assetStatuses);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);

        assetsListHolder.reasonsSpinner.setAdapter(spinnerArrayAdapter);

        if (asset.getVerified()){
            assetsListHolder.reasonsSpinner.setEnabled(false);
        }

//        String reason = asset.getReason();
        Integer statusId = asset.getStatusid();
        if(statusId != null){
            int index;

            if (asset.getVerified()){
               index = getAssetIndex(COOLER_SCANNED);
            }else{
                index = getAssetIndex(asset.getStatusid());
            }

            assetScanning++;
            assetsListHolder.reasonsSpinner.setSelection(index);

        }else{
            if (asset.getVerified()){

                int index = getAssetIndex(COOLER_SCANNED);
                assetsListHolder.reasonsSpinner.setSelection(index);
            }
        }
//
//        if (assetList.get(position).getVerified()){
//            assetScanning++;
//        }

        assetsListHolder.reasonsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positionListener, long id) {
//                if(++check > 1) {

                if (!((TextView) view).getText().toString().isEmpty()){
                    if (assetList.get(position).getStatusid() == null){
                        assetScanning++;
                    }
                    asset.setReason(((TextView) view).getText().toString());
                    asset.setStatusid(assetStatuses.get(positionListener).getKey());
                    assetList.get(position).setStatusid(assetStatuses.get(positionListener).getKey());
                    listener.onStatusChange(asset);

                    if (asset.getStatusid().equals(COOLER_SCANNED) && !asset.getVerified()){
                        Toast.makeText(mContext, "Please Scan Barcode", Toast.LENGTH_SHORT).show();
                        assetsListHolder.reasonsSpinner.setSelection(0);
                    }else if (!asset.getStatusid().equals(COOLER_SCANNED) && asset.getVerified()){
                        Toast.makeText(mContext, "You have already scan the asset", Toast.LENGTH_SHORT).show();
                        assetsListHolder.reasonsSpinner.setSelection(COOLER_SCANNED);
                    }else if (!asset.getStatusid().equals(COOLER_SCANNED)){
                        assetScanning--;
                    }
                    Log.i("Errorrrr!!!", positionListener + "");
                }else{

                    if (assetList.get(position).getStatusid() != null){
                        assetScanning--;
                        asset.setStatusid(null);
//                        asset.setReason(null);
                        assetList.get(position).setStatusid(null);
                        listener.onStatusChange(asset);

                    }
                }
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public int getAssetScanning() {
        return assetScanning;
    }

    public List<Asset> getAssetList() {
        return assetList;
    }

    public int getAssetIndex(Integer key){
        int count = 0 ;
        for (AssetStatus assetStatus : assetStatuses){
            if (assetStatus.getKey().equals(key)){
                return count;
            }
            count++;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    static class AssetsListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvAssetCode)
        TextView codeTv;
        @BindView(R.id.tvStatus)
        TextView statusTextView;
        @BindView(R.id.spinnerReason)
        Spinner reasonsSpinner;

        AssetsListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
