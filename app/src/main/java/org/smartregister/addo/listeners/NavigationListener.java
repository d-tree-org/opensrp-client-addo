package org.smartregister.addo.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import org.smartregister.addo.R;
import org.smartregister.addo.activity.AddoHomeActivity;
import org.smartregister.addo.adapter.NavigationAdapter;
import org.smartregister.addo.util.Constants;

public class NavigationListener implements View.OnClickListener {

    private Activity activity;
    private NavigationAdapter navigationAdapter;

    public NavigationListener(Activity activity, NavigationAdapter navigationAdapter) {
        this.activity = activity;
        this.navigationAdapter = navigationAdapter;
    }

    @Override
    public void onClick(View v) {

        if (v.getTag() != null) {

            if (v.getTag() instanceof String) {
                String tag = (String) v.getTag();
                //All cases down here needs to be changed to reflect the ADDO application. Based on
                // the funcitonality we need to define a new navigation menu for ADDO application
                switch (tag) {
                    case Constants.DrawerMenu.ALL_FAMILIES:
                        startRegisterActivity(AddoHomeActivity.class);
                        break;
                    case Constants.DrawerMenu.ANC:
                        Toast.makeText(activity.getApplicationContext(), Constants.DrawerMenu.ANC, Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.DrawerMenu.ANC_CLIENTS:
                        Toast.makeText(activity.getApplicationContext(), Constants.DrawerMenu.ANC_CLIENTS, Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.DrawerMenu.CHILD_CLIENTS:
                        Toast.makeText(activity.getApplicationContext(), Constants.DrawerMenu.CHILD_CLIENTS, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

                navigationAdapter.setSelectedView(tag);
            }
        }

    }

    private void startRegisterActivity(Class registerClass) {
        Intent intent = new Intent(activity, registerClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        activity.finish();
    }
}
