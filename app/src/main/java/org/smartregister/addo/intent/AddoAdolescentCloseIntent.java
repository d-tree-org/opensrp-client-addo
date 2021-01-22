package org.smartregister.addo.intent;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.smartregister.addo.dao.AdolescentDao;
import org.smartregister.addo.repository.AdolescentCloseRepository;

import timber.log.Timber;

public class AddoAdolescentCloseIntent extends IntentService {

    private static final String TAG  = "AddoAdolescentCloseIntent";
    private AdolescentCloseRepository adolescentCloseRepository;

    private static final int ADOLESCENT_CUTOFF_AGE = 20;

    public AddoAdolescentCloseIntent() { super(TAG);}

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        adolescentCloseRepository = new AdolescentCloseRepository();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        try {

            AdolescentDao.graduateAdolescentToAdult(ADOLESCENT_CUTOFF_AGE, adolescentCloseRepository);

        } catch (Exception e) {
            Timber.e(e);
        }

    }
}
