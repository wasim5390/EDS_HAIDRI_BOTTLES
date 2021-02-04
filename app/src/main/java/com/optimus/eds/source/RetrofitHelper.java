package com.optimus.eds.source;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.optimus.eds.AnnotationExclusionStrategy;
import com.optimus.eds.Constant;
import com.optimus.eds.EdsApplication;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by sidhu on 4/4/2019.
 */

public class RetrofitHelper implements Constant {
//     public static final String BASE_URL = "http://173.214.164.251:92/api/"; //volka UAT
     public static final String BASE_URL = "http://173.214.164.251:91/"; // MEM UAT
   // public static final String BASE_URL = "http://173.214.164.251:93/api/"; //Qarshi UAT

    // public static final String BASE_URL = "http://optimuseds.com/UATAPI/"; // staging
//      public static final String BASE_URL = "http://optimuseds.com/API/";

    private static RetrofitHelper instance;
    private Retrofit retrofit;
    private API service;

    private static final String TAG = "RetrofitHelper";
    private RetrofitHelper () {

        retrofit = getRetrofit();
        service = retrofit.create(API.class);
    }

    public static RetrofitHelper getInstance() {
        if (instance==null) {
            instance = new RetrofitHelper();
        }
        return instance;
    }

    public API getApi() {
        return service;
    }

    public class AuthorizationInterceptor implements Interceptor {
        Response response;
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(EdsApplication.getContext());


            if(request.headers().get("Authorization")==null || request.headers().get("Authorization").isEmpty()){
                String token = Util.getAuthorizationHeader(EdsApplication.getContext());
                Headers headers = request.headers().newBuilder().add("Authorization","Bearer "+ token).build();
                request = request.newBuilder().headers(headers).build();
            }

            response = chain.proceed(request);

            if (response.code()== 401 && !preferenceUtil.getUsername().isEmpty()) {
                API tokenApi =  getRetrofit().create(API.class);
                retrofit2.Response<TokenResponse> tokenResponse= tokenApi.refreshToken("password",preferenceUtil.getUsername(),preferenceUtil.getPassword()).execute();
                if(tokenResponse.isSuccessful()){
                    TokenResponse tokenResponseObj=tokenResponse.body();

                    try {
                        response.close();
                        request= request.newBuilder()
                                .header("Authorization", "Bearer " + tokenResponseObj.getAccessToken()).build();
                        response = chain.proceed(request);
                        PreferenceUtil.getInstance(EdsApplication.getContext()).saveToken(tokenResponseObj.getAccessToken());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    // goto Login as we cannot refresh token
                }
            }
            return response;
        }
    }

    private Retrofit getRetrofit(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(40, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new AuthorizationInterceptor());
        Gson builder = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
        if(retrofit!=null)
            Log.println(100,"Retrofit Create","Duplicate Retrofit Object");
        return retrofit==null?new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(builder))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClientBuilder.build())
                .build():retrofit;
    }


}
