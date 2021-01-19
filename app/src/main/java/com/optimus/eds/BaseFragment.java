package com.optimus.eds;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by sidhu on 4/12/2018.
 */

public abstract class BaseFragment  extends Fragment implements Constant{
    public BaseActivity mBaseActivity;
    public View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            mBaseActivity = (BaseActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(getID(), container, false);
        ButterKnife.bind(this,view);
        this.view = view;
        initUI(view);
        return view;
    }

    public abstract int getID();

    public abstract void initUI(View view);

    public View getView(){
        return this.view;
    }


/*    public void setHintForInputNumber(String code, EditText editText) {
        Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().getExampleNumber(code);
        if (phoneNumber!=null) {
            String number = PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            editText.setHint(getString(R.string.phone_number_template, number));
        } else {
            editText.setHint(R.string.mobile_number);
        }
    }*/





    protected void showSettingsDialog(){
        mBaseActivity.showSettingsDialog();
    }


}
