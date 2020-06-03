package org.smartregister.addo.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;

import org.smartregister.AllConstants;
import org.smartregister.addo.sync.AddoSyncTaskIntentService;
import org.smartregister.job.BaseJob;

public class AddoTaskServiceJob extends BaseJob {
    public static final String TAG = "AddoTaskServiceJob";

    @NonNull
    @Override
    protected Job.Result onRunJob(@NonNull Job.Params params) {
        Intent intent = new Intent(getApplicationContext(), AddoSyncTaskIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Job.Result.RESCHEDULE : Job.Result.SUCCESS;
    }
}
