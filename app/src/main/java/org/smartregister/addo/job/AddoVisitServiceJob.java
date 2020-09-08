package org.smartregister.addo.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.chw.anc.intent.HomeVisitIntentService;
import org.smartregister.family.util.Constants;
import org.smartregister.job.BaseJob;

import timber.log.Timber;

public class AddoVisitServiceJob extends BaseJob {
    public static final String TAG = "AddoVisitServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Timber.v("%s started", TAG);
        getApplicationContext().startService(new Intent(getApplicationContext(), HomeVisitIntentService.class));
        return params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
