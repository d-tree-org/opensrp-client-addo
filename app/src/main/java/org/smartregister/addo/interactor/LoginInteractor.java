package org.smartregister.addo.interactor;

import android.os.Handler;

import org.joda.time.DateTime;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.job.AddoAdolescentCloseJob;
import org.smartregister.addo.job.AddoTaskServiceJob;
import org.smartregister.addo.job.BasePncCloseJob;
import org.smartregister.domain.TimeStatus;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Author : Isaya Mollel on 2019-10-18.
 */
public class LoginInteractor extends BaseLoginInteractor implements BaseLoginContract.Interactor {

    public LoginInteractor(BaseLoginContract.Presenter loginPresenter){super(loginPresenter);}

    @Override
    protected void scheduleJobsPeriodically() {
        SyncServiceJob.scheduleJob(SyncServiceJob.TAG, TimeUnit.MINUTES.toMinutes(
                BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        AddoTaskServiceJob.scheduleJob(AddoTaskServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        BasePncCloseJob.scheduleJob(BasePncCloseJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.BASE_PNC_CLOSE_MINUTES), getFlexValue(BuildConfig.BASE_PNC_CLOSE_MINUTES));

        AddoAdolescentCloseJob.scheduleJob(AddoAdolescentCloseJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.ADOLESCENT_CLOSE_MINUTES), getFlexValue(BuildConfig.ADOLESCENT_CLOSE_MINUTES));
    }

    @Override
    protected void scheduleJobsImmediately() {
        super.scheduleJobsImmediately();
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        AddoTaskServiceJob.scheduleJobImmediately(AddoTaskServiceJob.TAG);
        BasePncCloseJob.scheduleJobImmediately(BasePncCloseJob.TAG);
        AddoAdolescentCloseJob.scheduleJobImmediately(AddoAdolescentCloseJob.TAG);
    }

    @Override
    public void loginWithLocalFlag(WeakReference<BaseLoginContract.View> view, boolean localLogin, String userName, String password) {
        if (localLogin) {
            getLoginView().hideKeyboard();
            getLoginView().enableLoginButton(false);
            getLoginView().showProgress(true);

            new Handler().post(() -> localLogin(view, userName, password));

        } else {
            super.loginWithLocalFlag(view, false, userName, password);
        }
    }

    private void localLogin(WeakReference<BaseLoginContract.View> view, String userName, String password) {
        boolean isAuthenticated = getUserService().isUserInValidGroup(userName, password);
        if (!isAuthenticated) {

            getLoginView().showErrorDialog(getApplicationContext().getResources().getString(org.smartregister.R.string.unauthorized));

        } else if (isAuthenticated && (!AllConstants.TIME_CHECK || TimeStatus.OK.equals(getUserService().validateStoredServerTimeZone()))) {

            this.navigateToHomePage(userName, password);

        } else {
            loginWithLocalFlag(view, false, userName, password);
        }
    }

    private void navigateToHomePage(String userName, String password) {

        getUserService().localLogin(userName, password);
        getLoginView().goToHome(false);

        CoreLibrary.getInstance().initP2pLibrary(userName);

        new Thread(() -> {
            Timber.i("Starting DrishtiSyncScheduler %s", DateTime.now().toString());

            scheduleJobsImmediately();

            Timber.i("Started DrishtiSyncScheduler %s", DateTime.now().toString());

            CoreLibrary.getInstance().context().getUniqueIdRepository().releaseReservedIds();
        }).start();
    }

}
