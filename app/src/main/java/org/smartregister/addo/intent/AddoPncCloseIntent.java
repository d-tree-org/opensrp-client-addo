package org.smartregister.addo.intent;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.pnc.repository.PncCloseDateRepository;
import org.smartregister.chw.pnc.util.PncUtil;

import timber.log.Timber;

public class AddoPncCloseIntent extends IntentService {

    private PncCloseDateRepository repository;
    private static final String TAG = "AddoPncCloseIntent";

    public AddoPncCloseIntent() { super(TAG); }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        repository = PncLibrary.getInstance().getPncCloseDateRepository();
        return super.onStartCommand(intent, flags, startId);
    }

    public int getNumberOfPncDays() {
        // This should be the same as in the CHV app
        return 43;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        try {
            PncUtil.updatePregancyOutcome(getNumberOfPncDays(), repository);
        } catch (Exception e) {
            Timber.e(e);
        }

    }
}
