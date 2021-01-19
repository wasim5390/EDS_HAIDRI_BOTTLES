package com.optimus.eds.ui.customer_complaints.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CheckedTextView;

import com.optimus.eds.R;
import com.optimus.eds.ui.customer_complaints.model.ComplaintReasonModel;
import com.optimus.eds.ui.customer_complaints.model.ComplaintTypeModel;
import com.thoughtbot.expandablecheckrecyclerview.CheckableChildRecyclerViewAdapter;
import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup;
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

/**
 * Created By apple on 5/1/19
 */
public class CustomerComplaintsAdapter extends CheckableChildRecyclerViewAdapter<CustomerComplaintsAdapter.TypeViewHolder, CustomerComplaintsAdapter.ReasonViewHolder> {


    public CustomerComplaintsAdapter(List<ComplaintTypeModel> groups) {
        super(groups);
    }

    @Override
    public TypeViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.complaint_header_item, parent, false);
        return new TypeViewHolder(view);
    }


    @Override
    public ReasonViewHolder onCreateCheckChildViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.complaint_child_item, parent, false);
        return new ReasonViewHolder(view);
    }

    @Override
    public void onBindCheckChildViewHolder(ReasonViewHolder holder, int flatPosition, CheckedExpandableGroup group, int childIndex) {
        ComplaintReasonModel complaintReasonModel = (ComplaintReasonModel) group.getItems().get(childIndex);
        holder.childCheckedTextView.setText(complaintReasonModel.getReason());
        holder.childCheckedTextView.setChecked(((ComplaintReasonModel) group.getItems().get(childIndex)).isSelected());
        holder.childCheckedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CheckedTextView) view).isChecked()){
                    ((CheckedTextView) view).setChecked(false);
                    ((ComplaintReasonModel) group.getItems().get(childIndex)).setSelected(false);

                }else{
                    ((CheckedTextView) view).setChecked(true);
                    ((ComplaintReasonModel) group.getItems().get(childIndex)).setSelected(true);
                }
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(TypeViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.checkBox.setText(group.getTitle());
    }

    static class TypeViewHolder extends GroupViewHolder {

        private CheckBox checkBox;

        public TypeViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }

    static class ReasonViewHolder extends CheckableChildViewHolder {

        private CheckedTextView childCheckedTextView;

        public ReasonViewHolder(View itemView) {
            super(itemView);
            childCheckedTextView = itemView.findViewById(R.id.list_item_check);
        }

        @Override
        public Checkable getCheckable() {
            return childCheckedTextView;
        }
    }
}
