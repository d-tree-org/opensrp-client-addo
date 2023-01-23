package org.smartregister.addo.sync;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.smartregister.addo.sync.helper.AddoTaskServiceHelper;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.NetworkUtils;
import org.smartregister.util.SyncUtils;

import java.io.IOException;

public class AddoSyncTaskIntentService extends IntentService {
    private static final String TAG = "AddoSyncTaskIntentService";

    private SyncUtils syncUtils;

    public AddoSyncTaskIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!NetworkUtils.isNetworkAvailable()) {
            sendSyncStatusBroadcastMessage(FetchStatus.noConnection);
            return;
        }
        if (!syncUtils.v1VerifyAuthorization()) {
            try {
                syncUtils.v1LogoutUser();;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;

        }
        sendSyncStatusBroadcastMessage(FetchStatus.fetchStarted);

        doSync();
    }

    private void sendSyncStatusBroadcastMessage(FetchStatus fetchStatus) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        syncUtils = new SyncUtils(getBaseContext());
        return super.onStartCommand(intent, flags, startId);
    }


    private void doSync() {
        sendSyncStatusBroadcastMessage(FetchStatus.fetchStarted);
        AddoTaskServiceHelper taskServiceHelper = AddoTaskServiceHelper.getInstance();

        sendSyncStatusBroadcastMessage(FetchStatus.fetchStarted);
        taskServiceHelper.syncTasks();
    }
}
