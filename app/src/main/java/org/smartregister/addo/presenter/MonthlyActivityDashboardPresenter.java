package org.smartregister.addo.presenter;

import org.smartregister.addo.interactor.MonthlyActivityDashboardInteractor;
import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.domain.BaseReportIndicatorsModel;
import org.smartregister.reporting.domain.IndicatorQuery;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.ReportIndicator;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class MonthlyActivityDashboardPresenter implements ReportContract.Presenter {

    private WeakReference<ReportContract.View> weakReference;
    private ReportContract.Model model;
    private ReportContract.Interactor interactor;

    public MonthlyActivityDashboardPresenter(ReportContract.View view){
        this.weakReference = new WeakReference<>(view);
        this.model = new BaseReportIndicatorsModel();
        this.interactor = new MonthlyActivityDashboardInteractor();
    }

    @Override
    public void onResume() {
        getView().refreshUI();
    }

    @Override
    public List<Map<String, IndicatorTally>> fetchIndicatorsDailytallies() {
        return model.getIndicatorsDailyTallies();
    }

    @Override
    public void addIndicators(List<ReportIndicator> indicators) {
        for (ReportIndicator indicator : indicators){
            this.model.addIndicator(indicator);
        }
    }

    @Override
    public void addIndicatorQueries(List<IndicatorQuery> indicatorQueries) {
        for (IndicatorQuery query : indicatorQueries){
            this.model.addIndicatorQuery(query);
        }
    }

    @Override
    public void scheduleRecurringTallyJob() {
        interactor.scheduleDailyTallyJob();
    }

    public ReportContract.View getView(){
        if (weakReference != null){
            return weakReference.get();
        }
        return null;
    }

}
