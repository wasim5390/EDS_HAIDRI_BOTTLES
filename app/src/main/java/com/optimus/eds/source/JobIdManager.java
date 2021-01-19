package com.optimus.eds.source;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.logging.Logger;

import androidx.annotation.IntDef;
import androidx.core.content.ContextCompat;

public class JobIdManager {

    public static final int JOB_TYPE_MASTER_UPLOAD = 1;
    public static final int JOB_TYPE_UPDATE_STOCK = 2;
    public static final int JOB_TYPE_MERCHANDISE_UPLOAD = 3;


    @IntDef(value = {
            JOB_TYPE_MASTER_UPLOAD,
            JOB_TYPE_UPDATE_STOCK,
            JOB_TYPE_MERCHANDISE_UPLOAD
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface JobType {
    }

    //16-1 for short. Adjust per your needs
    private static final int JOB_TYPE_SHIFTS = 30;

    public static int getJobId(@JobType int jobType, int objectId) {
        if ( 0 < objectId && objectId < (1<< JOB_TYPE_SHIFTS) ) {
            return (jobType << JOB_TYPE_SHIFTS) + objectId;
        } else {
            String err = String.format("objectId %s must be between %s and %s",
                    objectId,0,(1<<JOB_TYPE_SHIFTS));
            throw new IllegalArgumentException(err);
        }
    }

    public static void cancelJob(Context context,int outletId){
        try {
            int jobId =  getJobId(JobIdManager.JOB_TYPE_MASTER_UPLOAD,outletId);
            JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
            List<JobInfo> jobInfos = jobScheduler.getAllPendingJobs();
            for(JobInfo jobInfo:jobInfos){
                if(jobInfo.getId()==jobId){
                    jobScheduler.cancel(jobId);
                    return;
                }
            }

        }catch (NullPointerException e){
            Log.e("JobCancel:",e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e){
            Log.e("JobCancel:",e.getMessage());
            e.printStackTrace();
        }
    }
}