package org.smartregister.addo.interactor;

import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;

import java.util.concurrent.TimeUnit;

public class MonthlyActivityDashboardInteractor implements ReportContract.Interactor {

    public MonthlyActivityDashboardInteractor(){}

    @Override
    public void scheduleDailyTallyJob() {
//        RecurringIndicatorGeneratingJob.scheduleJob(RecurringIndicatorGeneratingJob.TAG,
//                TimeUnit.MINUTES.toMillis(1), TimeUnit.MINUTES.toMillis(1));
        RecurringIndicatorGeneratingJob.scheduleJobImmediately(RecurringIndicatorGeneratingJob.TAG);
    }
}
