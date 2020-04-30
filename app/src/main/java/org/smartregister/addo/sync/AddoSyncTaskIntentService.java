package org.smartregister.addo.sync;

import android.content.Intent;

import org.smartregister.addo.sync.helper.AddoTaskServiceHelper;
import org.smartregister.sync.intent.SyncTaskIntentService;

public class AddoSyncTaskIntentService extends SyncTaskIntentService {
    private AddoTaskServiceHelper taskServiceHelper;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (taskServiceHelper == null) {
            taskServiceHelper = AddoTaskServiceHelper.getInstance();
        }
        super.onHandleIntent(intent);
        taskServiceHelper.syncTasks();
    }
}
