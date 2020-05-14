package org.smartregister.addo.custom_views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.style.FadingCircle;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.addo.R;
import org.smartregister.addo.adapter.NavigationAdapter;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.contract.NavigationContract;
import org.smartregister.addo.model.NavigationOption;
import org.smartregister.addo.presenter.NavigationPresenter;
import org.smartregister.addo.util.Constants;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.LangUtils;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class NavigationMenu implements NavigationContract.View, SyncStatusBroadcastReceiver.SyncStatusListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private RecyclerView recyclerView;
    private TextView tvLogout;
    private View rootView = null;
    private ImageView ivSync;
    private ProgressBar pbSync;
    private View parentView;

    private NavigationContract.Presenter mPresenter;

    private static NavigationMenu instance;
    private NavigationAdapter navigationAdapter;
    private List<NavigationOption> navigationOptions;
    private static WeakReference<Activity> activityWeakReference;

    public NavigationMenu(){}

    public static NavigationMenu getInstance(Activity activity, View parentView, Toolbar myToolbar){
        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(instance);
        activityWeakReference = new WeakReference<>(activity);
        int orientation = activity.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (instance == null) {
                instance = new NavigationMenu();
            }

            SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(instance);
            instance.init(activity, parentView, myToolbar);
            return instance;
        } else {

            return null;

        }

    }

    public NavigationAdapter getNavigationAdapter() {
        return navigationAdapter;
    }

    private void init(Activity activity, View myParentView, Toolbar myToolbar) {
        try {
            setParentView(activity, myParentView);
            toolbar = myToolbar;
            parentView = myParentView;
            mPresenter = new NavigationPresenter(this);
            prepareView(activity);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void setParentView(Activity activity, View parentView) {

        if (parentView != null) {
            rootView = parentView;
        } else {
            ViewGroup current = (ViewGroup) ((ViewGroup) (activity.findViewById(android.R.id.content))).getChildAt(0);

            if (!(current instanceof DrawerLayout)) {

                if (current.getParent() != null) {
                    ((ViewGroup) current.getParent()).removeView(current);
                }

                LayoutInflater inflater = LayoutInflater.from(activity);
                ViewGroup contentView = (ViewGroup) inflater.inflate(R.layout.activity_base, null);
                activity.setContentView(contentView);

                rootView = activity.findViewById(R.id.nav_view);
                RelativeLayout rl = activity.findViewById(R.id.nav_content);

                if (current.getParent() != null) {
                    ((ViewGroup) current.getParent()).removeView(current);
                }

                if (current instanceof RelativeLayout) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                            (RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    current.setLayoutParams(params);
                    rl.addView(current);
                } else {
                    rl.addView(current);
                }
            } else {
                rootView = current;
            }
        }

    }

    @Override
    public void prepareView(Activity activity) {

        drawer = activity.findViewById(R.id.drawer_layout);
        recyclerView = rootView.findViewById(R.id.rvOptions);
        tvLogout = rootView.findViewById(R.id.tvLogout);
        recyclerView = rootView.findViewById(R.id.rvOptions);
        ivSync = rootView.findViewById(R.id.ivSyncIcon);

        pbSync = rootView.findViewById(R.id.pbSync);

        ImageView ivLogo = rootView.findViewById(R.id.ivLogo);
        ivLogo.setContentDescription("Afya-tek");
        ivLogo.setImageResource(R.drawable.ic_addo_image);

        TextView tvLogo = rootView.findViewById(R.id.tvLogo);
        tvLogo.setText("Afyatek ADDO App");

        if (pbSync != null) {

            FadingCircle circle = new FadingCircle();
            pbSync.setIndeterminateDrawable(circle);

        }

        registerDrawer(activity);
        registerNavigation(activity);
        registerLogout(activity);
        registerSync(activity);
        registerLanguageSwitcher(activity);

        mPresenter.refreshLastSync();
        mPresenter.refreshNavigationCount(activity);

    }

    public void registerDrawer(Activity parentActivity) {

        if (drawer != null) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(parentActivity, drawer,
                    toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }

    }

    public void registerNavigation(Activity parentActivity){

        if (recyclerView != null) {

            navigationOptions = mPresenter.getOptions();

            if (navigationAdapter == null) {
                navigationAdapter = new NavigationAdapter(navigationOptions, parentActivity);
            }

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(parentActivity);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(navigationAdapter);
        }

    }

    public void registerLogout(final Activity parentActivity) {
        mPresenter.displayCurrentUser();
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(parentActivity);
            }
        });
    }

    public void registerSync(final Activity parentActivity) {
        TextView tvSync = rootView.findViewById(R.id.tvSync);
        ivSync = rootView.findViewById(R.id.ivSyncIcon);
        pbSync = rootView.findViewById(R.id.pbSync);

        View.OnClickListener syncClicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(parentActivity, parentActivity.getResources().getText
                        (R.string.action_start_sync), Toast.LENGTH_SHORT).show();
                mPresenter.sync(parentActivity);

                TextView textView = rootView.findViewById(R.id.referral_count);
                textView.setText("Referrals: " + getReferralCount());
            }
        };

        tvSync.setOnClickListener(syncClicker);
        ivSync.setOnClickListener(syncClicker);

        refreshSyncProgressSpinner();
    }

    public void registerLanguageSwitcher(final Activity parentActivity) {

        View rlIconLang = rootView.findViewById(R.id.rlIconLang);
        final TextView tvLang = rootView.findViewById(R.id.tvLang);

        final String [] languages = {Constants.LANGUAGES.ENGLISH,
        Constants.LANGUAGES.SWAHILI, Constants.LANGUAGES.FRANCAIS};

        Locale current = parentActivity.getResources().getConfiguration().locale;
        tvLang.setText(StringUtils.capitalize(current.getDisplayLanguage()));

        rlIconLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
                builder.setTitle(parentActivity.getString(R.string.choose_language));
                builder.setItems(languages, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String lang = languages[which];
                        Locale LOCALE;
                        switch (lang) {
                            case Constants.LANGUAGES
                                .ENGLISH:
                                LOCALE = Locale.ENGLISH;
                                break;
                            case Constants.LANGUAGES
                                .SWAHILI:
                                LOCALE = new Locale("sw");
                                break;
                            case Constants.LANGUAGES
                                .FRANCAIS:
                                LOCALE = Locale.FRANCE;
                                break;
                            default:
                                LOCALE = Locale.ENGLISH;
                                break;
                        }
                        tvLang.setText(languages[which]);
                        LangUtils.saveLanguage(parentActivity.getApplicationContext(),
                                LOCALE.getLanguage());
                        drawer.closeDrawers();
                        instance = null;
                        Intent intent = parentActivity.getIntent();
                        parentActivity.finish();
                        parentActivity.startActivity(intent);
                        AddoApplication.getInstance().notifyAppContextChange();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    protected void refreshSyncProgressSpinner(){
        if (SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            pbSync.setVisibility(View.VISIBLE);
            ivSync.setVisibility(View.INVISIBLE);
        } else {
            pbSync.setVisibility(View.INVISIBLE);
            ivSync.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refreshLastSync(Date lastSync) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa, MMM d", Locale.getDefault());
        if(rootView != null) {
            TextView tvLastSyncTime = rootView.findViewById(R.id.tvSyncTime);
            if (lastSync != null) {
                tvLastSyncTime.setVisibility(View.VISIBLE);
                tvLastSyncTime.setText(MessageFormat.format(" {0}", sdf.format(lastSync)));
            } else {
                tvLastSyncTime.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void refreshCurrentUser(String name) {

        if (tvLogout != null && activityWeakReference.get() != null) {
            tvLogout.setText(String.format("%s %s", activityWeakReference.get().getResources().getString(R.string.log_out_as), name));
        }

    }

    @Override
    public void logout(Activity activity) {

        Toast.makeText(activity.getApplicationContext(), activity.getResources().getText(R.string.action_log_out), Toast.LENGTH_SHORT).show();
        AddoApplication.getInstance().logoutCurrentUser();

    }

    @Override
    public void refreshCount() {

        navigationAdapter.notifyDataSetChanged();

    }

    @Override
    public void displayToast(Activity activity, String message) {

        if (activity != null) {

            Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onSyncStart() {

        refreshSyncProgressSpinner();

    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {

        Timber.v("onSyncInProgress");

    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {

        refreshSyncProgressSpinner();
        mPresenter.refreshLastSync();

        if (activityWeakReference.get() != null && !activityWeakReference.get().isDestroyed()) {
            mPresenter.refreshNavigationCount(activityWeakReference.get());
        }

    }

    public int getReferralCount() {
        Cursor c = null;
        try {
            String query = "select count(*) from task where status = 'READY'";

            c = AddoApplication.getInstance().getRepository().getReadableDatabase().query(query);

            c.moveToFirst();
            return c.getInt(0);
        }catch (Exception e) {
            Timber.e(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return 0;
    }
}
