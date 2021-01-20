package com.optimus.eds.ui.route.outlet.routes;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.OnClick;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.ui.route.outlet.OutletListActivity;
import com.optimus.eds.ui.route.outlet.OutletListViewModel;
import java.util.List;

public class RoutesActivity extends BaseActivity {

    @BindView(R.id.routesRecyclerView)
    RecyclerView outletsRecyclerView;
    private OutletListViewModel viewModel;

    @Override
    public int getID() {
        return R.layout.activity_routes;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.routes));

        viewModel = ViewModelProviders.of(this).get(OutletListViewModel.class);

        viewModel.getRoutes().observe(this , this::onRouteListLoaded);

    }

    public void onRouteListLoaded(List<Route> routes) {

        RouteAdapter routeAdapter = new RouteAdapter(routes , object -> {

            startActivity(new Intent(this , OutletListActivity.class).putExtra("route" , (Route)object));
        });

        outletsRecyclerView.setAdapter(routeAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this , RecyclerView.VERTICAL , false);
        outletsRecyclerView.setLayoutManager(linearLayoutManager);

    }


    public static void start(Context context) {
        Intent starter = new Intent(context, RoutesActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }


}