package org.smartregister.addo.interactor;

import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.job.AddoTaskServiceJob;
import org.smartregister.addo.job.BasePncCloseJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.concurrent.TimeUnit;

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
    }

    @Override
    protected void scheduleJobsImmediately() {
        super.scheduleJobsImmediately();
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        AddoTaskServiceJob.scheduleJobImmediately(AddoTaskServiceJob.TAG);
        BasePncCloseJob.scheduleJobImmediately(BasePncCloseJob.TAG);
    }
}
