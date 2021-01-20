package com.optimus.eds.ui.route.outlet;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutletListItemView extends ConstraintLayout {


    @BindView(R.id.outletName)
    TextView outletName;
    @BindView(R.id.outletCode)
    TextView outletCode;
    @BindView(R.id.orderAmount)
    TextView orderAmount;
    @BindView(R.id.iv_status)
    ImageView ivStatus;

    private OutletListAdapter.Callback callback;
    private Outlet outletItem;

    public OutletListItemView(Context context) {
        super(context);
    }

    public OutletListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OutletListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);

    }

    public void setOutlet(Outlet item, OutletListAdapter.Callback callback) {
        this.callback = callback;
        this.outletItem = item;

        if (item != null) {
            outletName.setText(outletItem.getOutletName().concat(" - "+ outletItem.getLocation()));
            outletCode.setText(getResources().getString(R.string.outlet_code,outletItem.getOutletCode()));
            orderAmount.setText("RS. "+ outletItem.getTotalAmount());
            ivStatus.setVisibility(outletItem.getVisitStatus()!=0?VISIBLE:GONE);
            Integer res = getResource();
            if(res!=null)
            ivStatus.setImageResource(res);
        }
    }

    private Integer getResource(){
        Integer visitStatus = outletItem.getVisitStatus();
        Integer resourceId;
        if(visitStatus<1)
            resourceId = null;
        else if(((visitStatus>1 && visitStatus<=6) || visitStatus>7) && outletItem.getSynced()){
            resourceId = R.drawable.ic_tick_green;
        }else {
            resourceId = R.drawable.ic_tick_red;
        }
        return resourceId;
    }

    @OnClick(R.id.outletView)
    public void onOutletClick(){
        callback.onOutletClick(outletItem);
    }

}
