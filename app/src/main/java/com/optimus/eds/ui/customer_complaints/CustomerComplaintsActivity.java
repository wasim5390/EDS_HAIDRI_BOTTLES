package com.optimus.eds.ui.customer_complaints;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.Spinner;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.R;
import com.optimus.eds.ui.customer_complaints.adapter.CustomerComplaintsAdapter;
import com.optimus.eds.ui.customer_complaints.adapter.DaysAdapter;
import com.optimus.eds.ui.customer_complaints.model.ComplaintReasonModel;
import com.optimus.eds.ui.customer_complaints.model.ComplaintTypeModel;
import com.optimus.eds.ui.customer_complaints.model.DaysModel;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By apple on 5/1/19
 */
public class CustomerComplaintsActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.days_spinner)
    Spinner daysSpinner;
    private CustomerComplaintsAdapter customerComplaintsAdapter;
    private DaysAdapter daysAdapter;

    CustomerComplaintViewModel viewModel;
    List<ComplaintTypeModel> complaintTypeModelList;

    final String[] selectDays = {
            "Choose Preferred Days", "Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday", "Sunday"};

    public static void start(Context context) {
        Intent starter = new Intent(context, CustomerComplaintsActivity.class);
        context.startActivity(starter);
    }

    @Override
    public int getID() {
        return R.layout.activity_customer_complaint;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.customer_complaint));
        complaintTypeModelList = new ArrayList<>();
        viewModel = ViewModelProviders.of(this).get(CustomerComplaintViewModel.class);


        viewModel.getComplaintsData().observe(this, complaintTypeModels -> {
            complaintTypeModelList=complaintTypeModels;
            initAssetsAdapter();
        });

        viewModel.setComplaintsData();

        initDaysSpinner();

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    private void initAssetsAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        customerComplaintsAdapter = new CustomerComplaintsAdapter(complaintTypeModelList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(customerComplaintsAdapter);
    }

    private void initDaysSpinner(){
        List<DaysModel> daysModelList = new ArrayList<>();

        for (int i = 0; i < selectDays.length; i++) {
            DaysModel daysModel = new DaysModel();
            daysModel.setName(selectDays[i]);
            daysModel.setSelected(false);
            daysModelList.add(daysModel);
        }
        daysAdapter = new DaysAdapter(this, 0, daysModelList);
        daysSpinner.setAdapter(daysAdapter);
    }


//    @OnClick(R.id.getData)
    public void getDataButton(){
        List<? extends ExpandableGroup> list = customerComplaintsAdapter.getGroups();

        for (int i=0;i<list.size();i++){
            for (int j=0;j<list.get(i).getItems().size();j++){
                ComplaintReasonModel reasonModel= (ComplaintReasonModel) list.get(i).getItems().get(j);
                Log.e("DATA",reasonModel.getReason());
            }
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        customerComplaintsAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        customerComplaintsAdapter.onRestoreInstanceState(savedInstanceState);
    }

}
