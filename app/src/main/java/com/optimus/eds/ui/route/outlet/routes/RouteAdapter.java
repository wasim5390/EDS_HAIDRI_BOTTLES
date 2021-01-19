package com.optimus.eds.ui.route.outlet.routes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.optimus.eds.OnClick;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Route;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.OutletViewHolder> {

    List<Route> routeList ;
    OnClick onClick;

    public RouteAdapter(List<Route> routeList , OnClick onClick) {
        this.routeList = routeList;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public OutletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.routes_layout, parent, false);

        return new OutletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutletViewHolder holder, int position) {

        int pos = holder.getAdapterPosition();

        holder.routeName.setText(routeList.get(pos).mRouteName);
        holder.routesCount.setText(String.valueOf(++pos));

        holder.parent.setOnClickListener( v -> {

            onClick.onClick(routeList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class OutletViewHolder extends RecyclerView.ViewHolder{

        TextView routesCount , routeName ;
        ConstraintLayout parent ;

        public OutletViewHolder(@NonNull View itemView) {
            super(itemView);

            routeName = itemView.findViewById(R.id.routeName);
            routesCount = itemView.findViewById(R.id.routesCount);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
