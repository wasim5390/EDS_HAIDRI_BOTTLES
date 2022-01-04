package com.optimus.eds.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.ui.home.MainActivity;
import com.optimus.eds.utils.NetworkManager;
import com.optimus.eds.utils.NetworkManagerKotlin;
import com.optimus.eds.utils.PreferenceUtil;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

    // UI references.
    @BindView(R.id.email)
    TextInputEditText mUsernameView;
    @BindView(R.id.password)
    TextInputEditText mPasswordView;
    @BindView(R.id.login_progress)
    View mProgressView;
    @BindView(R.id.login_form)
    View mLoginFormView;

    @BindView(R.id.email_sign_in_button)
    AppCompatButton mLogInButton;

    LoginViewModel viewModel;

    @Override
    public int getID() {
        return R.layout.activity_login;
    }

    @Override
    public void created(Bundle savedInstanceState) {

        if(!PreferenceUtil.getInstance(this).getToken().isEmpty()){
            MainActivity.start(this);
            this.finish();
        }
        ButterKnife.bind(this);
        setToolbar(getString(R.string.title_activity_login));
        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        mLogInButton.setOnClickListener(view -> attemptLogin());
        setObserver();
    }



    private void setObserver(){
        viewModel.getMsg().observe(this,s -> {
            hideProgress();
            Snackbar.make(mLoginFormView,s,Snackbar.LENGTH_LONG).show();
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        // Check for a valid username.
        if (TextUtils.isEmpty(username) ) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        else if (TextUtils.isEmpty(password) ) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            removeFocus();
            showProgress();
            viewModel.login(username,password).observe(this,tokenResponse -> {
               // saveFirebaseAndImei();
                String error= Constant.GENERIC_ERROR;
                hideProgress();
                if(tokenResponse.isSuccess()) {

                    MainActivity.start(this);
                    this.finish();
                }else{
                    Snackbar.make(mLoginFormView,error,Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    public void saveFirebaseAndImei(){
        try {
            NetworkManager.getInstance().findIMEI(this).observe(this, s -> {
                System.out.println("IMEI::" + s);
                retrieveFireBaseToken(s);

            });
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }finally {
            return;
        }
    }

    private void removeFocus(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void retrieveFireBaseToken(String imei){
        if(imei==null)
            return;
        // generate if token is not saved yet.
        FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener(new OnSuccessListener<InstallationTokenResult>() {
            @Override
            public void onSuccess(InstallationTokenResult installationTokenResult) {
                String token = installationTokenResult.getToken();
                saveImeiAndToken(token,imei);
                Log.d(TAG, token);

            }
        });

    }

    /**
     * Save firebase token on server for successfully logged-in to system
     * @param token
     * @param imei
     */
    private void saveImeiAndToken(String token,String imei){

        viewModel.saveFirebaseToken(token,imei).observe(this,baseResponse -> {
            String error= Constant.GENERIC_ERROR;
            hideProgress();
            if(baseResponse.isSuccess()) {
                MainActivity.start(this);
                this.finish();
            }else{
                if(baseResponse.getErrorCode()==2){
                    error = baseResponse.getResponseMsg();
                }
                Snackbar.make(mLoginFormView,error,Snackbar.LENGTH_LONG).show();
            }
        });
    }


    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @Override
    public void showProgress() {
        showProgressD(this,true);
        mLoginFormView.setVisibility(View.GONE);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginFormView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void hideProgress() {
        hideProgressD();
        mLoginFormView.setVisibility(View.VISIBLE);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginFormView.setVisibility(View.VISIBLE);
                    }
                });

            }

}

