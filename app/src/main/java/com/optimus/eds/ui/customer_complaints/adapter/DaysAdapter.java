package com.optimus.eds.ui.customer_complaints.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import com.optimus.eds.R;
import com.optimus.eds.ui.customer_complaints.model.DaysModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By apple on 5/2/19
 */
public class DaysAdapter extends ArrayAdapter<DaysModel> {

    private Context mContext;
    private ArrayList<DaysModel> daysModelList;

    public DaysAdapter(Context context, int resource, List<DaysModel> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.daysModelList = (ArrayList<DaysModel>) objects;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.complaint_child_item, null);
            holder = new ViewHolder();
            holder.mTextView = (CheckedTextView) convertView.findViewById(R.id.list_item_check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(daysModelList.get(position).getName());
        holder.mTextView.setChecked(daysModelList.get(position).isSelected());

        if ((position == 0)) {
            holder.mTextView.setCheckMarkDrawable(null);
        }

        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CheckedTextView) view).isChecked()){
                    ((CheckedTextView) view).setChecked(false);
                    daysModelList.get(position).setSelected(false);
                }else{
                    ((CheckedTextView) view).setChecked(true);
                    daysModelList.get(position).setSelected(true);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private CheckedTextView mTextView;
    }
}
