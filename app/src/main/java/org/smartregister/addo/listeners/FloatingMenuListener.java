package org.smartregister.addo.listeners;

import android.app.Activity;

import org.smartregister.addo.R;

import java.lang.ref.WeakReference;

import timber.log.Timber;

/**
 * Author : Isaya Mollel on 09/09/2019.
 */
public class FloatingMenuListener implements OnClickFloatingMenu {

    private static String TAG = FloatingMenuListener.class.getCanonicalName();
    private WeakReference<Activity> context;
    private String familyBaseEntityId;

    private FloatingMenuListener(Activity context, String familyBaseEntityId) {
        this.context = new WeakReference<>(context);
        this.familyBaseEntityId = familyBaseEntityId;
    }

    private static FloatingMenuListener instance;

    public static FloatingMenuListener getInstance(Activity context, String familyBaseEntityId) {
        if (instance == null) {
            instance = new FloatingMenuListener(context, familyBaseEntityId);
        } else {
            instance.setFamilyBaseEntityId(familyBaseEntityId);
            if (instance.context.get() != context) {
                instance.context = new WeakReference<>(context);
            }
        }
        return instance;
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public FloatingMenuListener setFamilyBaseEntityId(String familyBaseEntityId) {
        this.familyBaseEntityId = familyBaseEntityId;
        return this;
    }

    @Override
    public void onClickMenu(int viewId) {
        if (context.get() != null) {

            if (context.get().isDestroyed()) {
                Timber.d( "Activity Destroyed");
                return;
            }

            switch (viewId) {
                case R.id.call_layout:
                    //FamilyCallDialogFragment.launchDialog(context.get(), familyBaseEntityId);
                    break;

                case R.id.add_new_member_layout:
                    //TODO uncomment to implement adding new member
                    break;
            }
        }
    }

}