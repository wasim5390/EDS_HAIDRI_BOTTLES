package com.optimus.eds.ui.route.outlet;

import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.OutletOrderStatus;

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
    @BindView(R.id.noSale)
    TextView noSale;
    @BindView(R.id.iv_status)
    ImageView ivStatus;
    @BindView(R.id.star)
    ImageView star;
    @BindView(R.id.gift)
    ImageView gift;
    @BindView(R.id.pepsi)
    ImageView pepsi;
    @BindView(R.id.outletView)
    ConstraintLayout outletView;

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

    public void setOutlet(Outlet item, OutletOrderStatus outletOrderStatus, OutletListAdapter.Callback callback) {
        this.callback = callback;
        this.outletItem = item;

        if (item != null) {
            outletName.setText(outletItem.getOutletName().concat(" - " + outletItem.getLocation()));
            outletCode.setText(getResources().getString(R.string.outlet_code, outletItem.getOutletCode()));

            if (outletOrderStatus != null) {
                if (outletOrderStatus.orderStatus != null) {
                    if (outletOrderStatus.orderStatus.getStatus() >= 7) {
                        orderAmount.setText("RS. " + outletOrderStatus.orderStatus.getOrderAmount());
                    } else {
                        orderAmount.setText("RS. " + 0.0);
                    }
                } else {
                    setTotal();
                }
            } else {
                setTotal();
            }


            if (outletItem.getHasHTHDiscount())
                gift.setVisibility(VISIBLE);
            else
                gift.setVisibility(GONE);

            if (outletItem.getHasRentalDiscount())
                star.setVisibility(VISIBLE);
            else
                star.setVisibility(GONE);

            if (outletItem.getHasExclusivityFee())
                pepsi.setVisibility(VISIBLE);
            else
                pepsi.setVisibility(GONE);

//            if(outletItem.getPromoTypeId() != null){
//                if (outletItem.getPromoTypeId() == 1){
//                    star.setVisibility(VISIBLE);
//                }else if (outletItem.getPromoTypeId() == 2){
//                    gift.setVisibility(VISIBLE);
//                }else if (outletItem.getPromoTypeId() == 3){
//                    star.setVisibility(VISIBLE);
//                    gift.setVisibility(VISIBLE);
//                }else{
//                    gift.setVisibility(GONE);
//                    star.setVisibility(GONE);
//                }
//            }


            if (outletItem.getVisitStatus() != 0)
                ivStatus.setVisibility(outletItem.getVisitStatus() != 0 ? VISIBLE : GONE);
            else if (outletItem.getStatusId() != 0)
                ivStatus.setVisibility(outletItem.getStatusId() != 0 ? VISIBLE : GONE);

            Integer res = getResource();
            if (res != null) {
                ivStatus.setImageResource(res);
                ivStatus.setVisibility(VISIBLE);
            } else
                ivStatus.setVisibility(GONE);

            if (outletItem.getZeroSaleOutlet()) {
                outletView.setBackgroundColor(getResources().getColor(R.color.colorBackgorund));
                noSale.setVisibility(VISIBLE);
            } else {
                outletView.setBackgroundColor(getResources().getColor(R.color.white));
                noSale.setVisibility(GONE);
            }
        }


    }

    private Integer getResource() {
        Integer visitStatus = outletItem.getVisitStatus();
        if (visitStatus == 0) {
            visitStatus = outletItem.getStatusId();
            outletItem.setSynced(true);
        }
        Integer resourceId;
        if (visitStatus < 1)
            resourceId = null;
        else if (((visitStatus > 1 && visitStatus <= 6) || visitStatus >= 7) && outletItem.getSynced()) {
            resourceId = R.drawable.ic_tick_green;
        } else {
            resourceId = R.drawable.ic_tick_red;
        }
        return resourceId;
    }

    @OnClick(R.id.outletView)
    public void onOutletClick() {
        callback.onOutletClick(outletItem);
    }


    public void setTotal() {
        if (outletItem.getLastOrder() != null) {
            orderAmount.setText("RS. " + outletItem.getLastOrder().getOrderTotal());
        } else {
            orderAmount.setText("RS. " + "0.0");
        }
    }

}
