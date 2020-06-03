package org.smartregister.addo.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import org.smartregister.addo.activity.AddoHomeActivity;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.view.activity.BaseRegisterActivity;

public class AddoBottomNavigationListener extends BottomNavigationListener {
    private Activity context;

    public AddoBottomNavigationListener(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        super.onNavigationItemSelected(item);

        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) context;

        if (item.getItemId() == org.smartregister.family.R.id.action_family) {
            if (context instanceof AddoHomeActivity) {
                baseRegisterActivity.switchToBaseFragment();
            } else {
                Intent intent = new Intent(context, AddoHomeActivity.class);
                context.startActivity(intent);
                context.finish();
            }
        } else if (item.getItemId() == org.smartregister.family.R.id.action_job_aids) {
            // TODO Add code for job aid
            return false;
        }

        return true;
    }
}
