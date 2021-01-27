package com.optimus.eds.ui.route.outlet;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.OutletOrderStatus;


import java.util.ArrayList;
import java.util.List;

public class OutletListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {


    private final Callback mCallback;
    private List<Outlet> outlets = new ArrayList<>();
    private List<Outlet> outletsFiltered = new ArrayList<>();

    private List<OutletOrderStatus> outletOrderStatuses ;
    private List<OutletOrderStatus> filteredOutletOrderStatuses;

    boolean isPjp = false ;

    public OutletListAdapter( List<Outlet> oLets , List<OutletOrderStatus> outletOrderStatuses,OutletListAdapter.Callback callback) {
        this.outlets.addAll(oLets);
        this.mCallback = callback;
        this.outletOrderStatuses = new ArrayList<>(outletOrderStatuses);
        this.filteredOutletOrderStatuses = new ArrayList<>(outletOrderStatuses);
        this.outletsFiltered.addAll(oLets);
    }

    public void populateOutlets(List<Outlet> outlets , boolean isPjp) {
        List<Outlet> outletList = new ArrayList<>(outlets);
        this.outlets=outletList;
        this.outletsFiltered = outletList;
        this.outletOrderStatuses = new ArrayList<>();
        this.filteredOutletOrderStatuses = new ArrayList<>();
        this.isPjp = isPjp;
        notifyDataSetChanged();

    }

    public void populateOutletOrderStatus(List<OutletOrderStatus> outlets , boolean isPjp) {
        this.outletOrderStatuses = new ArrayList<>(outlets);
        this.filteredOutletOrderStatuses = new ArrayList<>(outlets);
        this.outlets = new ArrayList<>();
        this.outletsFiltered = new ArrayList<>();
        this.isPjp = isPjp;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public OutletListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.outlet_list_item, parent, false);

        return new OutletListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePos = holder.getAdapterPosition();
        Outlet outlet = null;

        OutletOrderStatus outletOrderStatus = null;
        if (!isPjp){
            outlet = outletsFiltered.get(safePos);
        } else{
            outlet = filteredOutletOrderStatuses.get(safePos).outlet;
            outletOrderStatus = filteredOutletOrderStatuses.get(safePos);
        }

        ((OutletListItemView)holder.itemView).setOutlet(outlet , outletOrderStatus ,mCallback);

    }

    @Override
    public int getItemCount() {
        return isPjp ? filteredOutletOrderStatuses.size() : outletsFiltered.size();
    }

    static class OutletListHolder extends RecyclerView.ViewHolder {

        OutletListHolder(View itemView) {
            super(itemView);
        }

    }


    @Override
    public Filter getFilter() {
        if (!isPjp)
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    outletsFiltered = outlets;
                } else {
                    List<Outlet> filteredList = new ArrayList<>();
                    for (Outlet row : outlets) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getOutletName().toLowerCase().contains(charString.toLowerCase()) || row.getOutletCode().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    outletsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = outletsFiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //noinspection unchecked
                outletsFiltered = (ArrayList<Outlet>) filterResults.values;
                notifyDataSetChanged();
            }
        };
        else
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charString = constraint.toString();
                    if (charString.isEmpty()) {
                        filteredOutletOrderStatuses = outletOrderStatuses;
                    } else {
                        List<OutletOrderStatus> filteredList = new ArrayList<>();
                        for (OutletOrderStatus row : outletOrderStatuses) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.outlet.getOutletName().toLowerCase().contains(charString.toLowerCase()) || row.outlet.getOutletCode().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        filteredOutletOrderStatuses = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredOutletOrderStatuses;

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredOutletOrderStatuses = (ArrayList<OutletOrderStatus>) results.values;
                    notifyDataSetChanged();
                }
            };
    }
    interface Callback{
        void onOutletClick(Outlet outlet);
    }
}
