package org.smartregister.addo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import org.smartregister.CoreLibrary;
import org.smartregister.addo.R;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.presenter.MonthlyActivityDashboardPresenter;
import org.smartregister.addo.repository.MonthlyReportRepository;
import org.smartregister.addo.util.ChartUtil;
import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.domain.IndicatorQuery;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.ReportIndicator;
import org.smartregister.reporting.model.NumericDisplayModel;
import org.smartregister.reporting.view.NumericIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.smartregister.reporting.contract.ReportContract.IndicatorView.CountType.LATEST_COUNT;
import static org.smartregister.reporting.util.ReportingUtil.getIndicatorDisplayModel;

public class MonthlyActivityDashboard extends Fragment implements ReportContract.View, LoaderManager.LoaderCallbacks<List<Map<String, IndicatorTally>>> {

    private static ReportContract.Presenter presenter;
    private ViewGroup visualizationsViewGroup;

    private List<Map<String, IndicatorTally>> indicatorTallies;

    private static final String HAS_LOADED_SAMPLE_DATA = "has_loaded_sample_data";
    private boolean activityStarted = false;
    private boolean hasLoadedSampleData = true;

    private View spacerView;
    private View titleView;

    private String anmUser;
    private MonthlyReportRepository monthlyReportRepository;

    MonthlyActivityDashboard(){
        monthlyReportRepository = new MonthlyReportRepository();
    }

    public static MonthlyActivityDashboard newInstance(){
        MonthlyActivityDashboard fragment = new MonthlyActivityDashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        anmUser = CoreLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM();

        presenter = new MonthlyActivityDashboardPresenter(this);

        addIndicators();

        presenter.scheduleRecurringTallyJob();
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    private void addIndicators(){
        List<ReportIndicator> reportIndicators = new ArrayList<>();
        List<IndicatorQuery> indicatorQueries = new ArrayList<>();

        withIndicator(ChartUtil.adolescentEncounter, reportIndicators);
        withIndicator(ChartUtil.ancEncounter, reportIndicators);
        withIndicator(ChartUtil.childEncounter, reportIndicators);
        withIndicator(ChartUtil.otherEncounter, reportIndicators);
        withIndicator(ChartUtil.pncEncounter, reportIndicators);
        withIndicator(ChartUtil.lastMonthTotalEncounters, reportIndicators);
        withIndicator(ChartUtil.lastMonthReferredEncounters, reportIndicators);
        withIndicator(ChartUtil.currentMonthLinked, reportIndicators);
        withIndicator(ChartUtil.currentMonthLinkedAttended, reportIndicators);
        withIndicator(ChartUtil.lastMonthLinked, reportIndicators);
        withIndicator(ChartUtil.lastMonthAttendedLinked, reportIndicators);
        withIndicator(ChartUtil.currentMonthVisits, reportIndicators);
        withIndicator(ChartUtil.lastMonthVisits, reportIndicators);

        withIndicator(ChartUtil.currentMonthIssuedByAddo, reportIndicators);
        withIndicator(ChartUtil.currentMonthIssuedAttendedByAddo, reportIndicators);
        withIndicator(ChartUtil.lastMonthIssuedByAddo, reportIndicators);
        withIndicator(ChartUtil.lastMonthIssuedAttendedByAddo, reportIndicators);
        withIndicator(ChartUtil.currentMonthCompleted, reportIndicators);
        withIndicator(ChartUtil.lastMonthCompleted, reportIndicators);

        withQuery(ChartUtil.adolescentEncounter, indicatorQueries);
        withQuery(ChartUtil.ancEncounter, indicatorQueries);
        withQuery(ChartUtil.childEncounter, indicatorQueries);
        withQuery(ChartUtil.otherEncounter, indicatorQueries);
        withQuery(ChartUtil.pncEncounter, indicatorQueries);
        withQuery(ChartUtil.lastMonthTotalEncounters, indicatorQueries);
        withQuery(ChartUtil.lastMonthReferredEncounters, indicatorQueries);
        withQuery(ChartUtil.currentMonthLinked, indicatorQueries);
        withQuery(ChartUtil.currentMonthLinkedAttended, indicatorQueries);
        withQuery(ChartUtil.lastMonthLinked, indicatorQueries);
        withQuery(ChartUtil.lastMonthAttendedLinked, indicatorQueries);
        withQuery(ChartUtil.currentMonthVisits, indicatorQueries);
        withQuery(ChartUtil.lastMonthVisits, indicatorQueries);

        withQuery(ChartUtil.currentMonthIssuedByAddo, indicatorQueries);
        withQuery(ChartUtil.currentMonthIssuedAttendedByAddo, indicatorQueries);
        withQuery(ChartUtil.lastMonthIssuedByAddo, indicatorQueries);
        withQuery(ChartUtil.lastMonthIssuedAttendedByAddo, indicatorQueries);
        withQuery(ChartUtil.currentMonthCompleted, indicatorQueries);
        withQuery(ChartUtil.lastMonthCompleted, indicatorQueries);

        presenter.addIndicators(reportIndicators);
        presenter.addIndicatorQueries(indicatorQueries);

    }

    private void withIndicator(String indicatorCode, List<ReportIndicator> indicators){
        ReportIndicator indicator = new ReportIndicator();
        indicator.setKey(indicatorCode);
        switch (indicatorCode){
            case ChartUtil.adolescentEncounter:
                indicator.setDescription("Adolescent visits conducted in the current month");
                break;
            case ChartUtil.ancEncounter:
                indicator.setDescription("ANC visits conducted in the current month");
                break;
            case ChartUtil.pncEncounter:
                indicator.setDescription("PNC visits conducted in the current month");
                break;
            case ChartUtil.childEncounter:
                indicator.setDescription("Children visits conducted in the current month");
                break;
            case ChartUtil.otherEncounter:
                indicator.setDescription("Other client visits conducted in the current month");
                break;
            case ChartUtil.lastMonthTotalEncounters:
                indicator.setDescription("Total Visits conducted in the last month");
                break;
            case ChartUtil.lastMonthReferredEncounters:
                indicator.setDescription("Visits conducted in the current month that were referred");
                break;
            case ChartUtil.currentMonthLinked:
                indicator.setDescription("Clients link to ADDO this month");
                break;
            case ChartUtil.currentMonthLinkedAttended:
                indicator.setDescription("Attended clients that were linked to ADDO this month");
                break;
            case ChartUtil.lastMonthLinked:
                indicator.setDescription("Clients link to ADDO last month");
                break;
            case ChartUtil.lastMonthAttendedLinked:
                indicator.setDescription("Attended clients that were linked to ADDO last month");
                break;
            case ChartUtil.currentMonthIssuedByAddo:
                indicator.setDescription("Referrals issued by an ADDO in the current month");
                break;
            case ChartUtil.currentMonthIssuedAttendedByAddo:
                indicator.setDescription("Current month ADDO Issued referrals that were attended");
                break;
            case ChartUtil.lastMonthIssuedByAddo:
                indicator.setDescription("Referrals issued by and ADDO in the last month");
                break;
            case ChartUtil.lastMonthIssuedAttendedByAddo:
                indicator.setDescription("last month issued referrals that were attended");
                break;
        }
        indicators.add(indicator);
    }

    private void withQuery(String indicatorCode, List<IndicatorQuery> queries){
        IndicatorQuery iQuery = new IndicatorQuery();
        String query = "";
        iQuery.setIndicatorCode(indicatorCode);
        iQuery.setDbVersion(0);
        iQuery.setId(null);
        switch (indicatorCode){
            case ChartUtil.adolescentEncounter:
                query = monthlyReportRepository.getAdolescentVisits();
                break;
            case ChartUtil.childEncounter:
                query = monthlyReportRepository.getChildVisits();
                break;
            case ChartUtil.ancEncounter:
                query = "select count(*) from ( " +
                        "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                        "from visits " +
                        "where datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') " +
                        "and visit_type in ('ANC ADDO Visit' ) " +
                        "and visit_json like \"%"+anmUser+"%\" "+
                        "group by base_entity_id, date_visited" +
                        ")";
                break;
            case ChartUtil.pncEncounter:
                query = "select count(*) from ( " +
                        "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                        "from visits " +
                        "where datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') " +
                        "and visit_type in ('PNC ADDO Visit' ) " +
                        "and visit_json like \"%"+anmUser+"%\" "+
                        "group by base_entity_id, date_visited" +
                        ")";
                break;
            case ChartUtil.otherEncounter:
                query = "select count(*) from ( " +
                        "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                        "from visits " +
                        "where datetime(visit_date/1000, 'unixepoch') < date('now', 'start of month') " +
                        "and visit_type in ('Other Member ADDO Visit') " +
                        "and visit_json like \"%"+anmUser+"%\" "+
                        "group by base_entity_id, date_visited" +
                        ")";
                break;
            case ChartUtil.lastMonthTotalEncounters:
                query = "select count(*) from ( " +
                        "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                        "from visits " +
                        "where datetime(visit_date/1000, 'unixepoch') < date('now', 'start of month') " +
                        "and datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month', '-1 months') " +
                        "and visit_type in ('ANC ADDO Visit', 'Child ADDO Visit', 'PNC ADDO Visit', 'Adolescent ADDO Visit', 'Other Member ADDO Visit' ) " +
                        "and visit_json like \"%"+anmUser+"%\" "+
                        "group by base_entity_id, date_visited" +
                        ")";
                break;
            case ChartUtil.lastMonthReferredEncounters:
                query = "select count(*) from ( " +
                        "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                        "from visits inner join task " +
                        "on visits.base_entity_id = task.for" +
                        "where datetime(visits.visit_date/1000, 'unixepoch') < date('now', 'start of month') " +
                        "and datetime(visits.visit_date/1000, 'unixepoch') > date('now', 'start of month', '-1 months')" +
                        "and task.business_status = 'Linked' " +
                        "and visits.visit_type in ('ANC ADDO Visit', 'Child ADDO Visit', 'PNC ADDO Visit', 'Adolescent ADDO Visit', 'Other Member ADDO Visit' ) " +
                        "and visits.visit_json like \"%"+anmUser+"%\" "+
                        "group by base_entity_id, date_visited" +
                        ")";
                break;
            case ChartUtil.currentMonthLinked:
                query = "select count(*) from tasks where code = 'Linked' and " +
                        currentMonthLimit();
                break;
            case ChartUtil.currentMonthLinkedAttended:
                query = "select count(*) from tasks where code = 'Linked' and " +
                        "business_status = 'Attended' and " +
                        currentMonthLimit();
                break;
            case ChartUtil.lastMonthLinked:
                query = "select count(*) from tasks where code = 'Linked' and " +
                        lastMonthLimit();
                break;
            case ChartUtil.lastMonthAttendedLinked:
                query = "select count(*) from tasks where code = 'Linked' and " +
                        "business_status = 'Attended' and " +
                        lastMonthLimit();
                break;
            case ChartUtil.currentMonthVisits:
                query = "select count(*) from visits where " +
                        "visit_type in ('Adolescent ADDO Visit','Child ADDO Visit','ANC ADDO Visit','PNC ADDO Visit', 'Other Member ADDO Visit') " +
                        "and datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') ";
                break;
            case ChartUtil.lastMonthVisits:
                query = "select count(*) from visits where " +
                        "visit_type in ('Adolescent ADDO Visit','Child ADDO Visit','ANC ADDO Visit','PNC ADDO Visit', 'Other Member ADDO Visit')" +
                        " and datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month', '-1 months) and" +
                        " datetime(visit_date/1000, 'unixepoch') < date('now', 'start of month')";
                break;
            case ChartUtil.currentMonthIssuedByAddo:
                query = "select count(*) from task where code = 'Referral' and " +
                        "owner = '"+anmUser+"' and " +
                        currentMonthLimit();
                break;
            case ChartUtil.currentMonthIssuedAttendedByAddo:
                query = "select count(*) from task where code = 'Referral' and " +
                        "owner = '"+anmUser+"' and status in ('IN_PROGRESS', 'COMPLETED') and " +
                        currentMonthLimit();
                break;
            case ChartUtil.lastMonthIssuedByAddo:
                query = "select count(*) from task where code = 'Referral' and " +
                        "owner = '"+anmUser+"' and " +
                        lastMonthLimit();
                break;
            case ChartUtil.lastMonthIssuedAttendedByAddo:
                query = "select count(*) from task where code = 'Referral' and " +
                        "owner = '"+anmUser+"' and status in ('IN_PROGRESS', 'COMPLETED') and " +
                        lastMonthLimit();
                break;
        }
        iQuery.setQuery(query);
        queries.add(iQuery);
    }

    private String lastMonthLimit(){
        return "datetime(authored_on/1000, 'unixepoch') < date('now', 'start of month') and " +
                "datetime(authored_on/1000, 'unixepoch') > date('now', 'start of month', '-1 months') ";
    }

    private String currentMonthLimit(){
        return "datetime(authored_on/1000, 'unixepoch') > date('now', 'start of month') ";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        spacerView = inflater.inflate(R.layout.report_spacer_view, container, false);
        titleView = inflater.inflate(R.layout.reporting_indicator_title, container, false);
        return inflater.inflate(R.layout.monthly_activities_dashboard_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        visualizationsViewGroup = getView().findViewById(R.id.dashboard_content);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void refreshUI() {
        buildVisualization(visualizationsViewGroup);
    }

    @Override
    public void buildVisualization(ViewGroup mainLayout) {
        mainLayout.removeAllViews();
        createReportViews(mainLayout);
    }

    @Override
    public List<Map<String, IndicatorTally>> getIndicatorTallies() {
        return indicatorTallies;
    }

    @Override
    public void setIndicatorTallies(List<Map<String, IndicatorTally>> indicatorTallies) {
        this.indicatorTallies = indicatorTallies;
    }

    private void createReportViews(ViewGroup mainLayout) {

        mainLayout.addView(getTitleView(getResources().getString(R.string.current_month)));

        NumericDisplayModel visitsConducted = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.currentMonthVisits, R.string.current_month_visits, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), visitsConducted).createView());

        NumericDisplayModel childEnconters = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.childEncounter, R.string.child_encounters, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), childEnconters).createView());

        NumericDisplayModel adolescentEncounters = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.adolescentEncounter, R.string.adolescent_encounters, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), adolescentEncounters).createView());

        NumericDisplayModel ancEncounters = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.ancEncounter, R.string.anc_encounters, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), ancEncounters).createView());

        NumericDisplayModel pncEncounters = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.pncEncounter, R.string.pnc_encounters, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), pncEncounters).createView());

        NumericDisplayModel otherEncounters = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.otherEncounter, R.string.other_encounters, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), otherEncounters).createView());


        addCurrentMonthReferralsIssued(mainLayout);

        addCurrentMonthReferralsIssuedCompleted(mainLayout);

        //Add Space between indicators
        mainLayout.addView(spacerView);

        mainLayout.addView(getTitleView(getResources().getString(R.string.last_month)));

        NumericDisplayModel visitsConductedLastMonth = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.lastMonthVisits, R.string.last_month_visits, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), visitsConductedLastMonth).createView());

        addLastMonthReferralsIssued(mainLayout);
        addLastMonthReferralsIssuedCompleted(mainLayout);
        /*
        NumericDisplayModel totalEncounters = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.lastMonthTotalEncounters, R.string.total_encounters, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), totalEncounters).createView());

        NumericDisplayModel referredEncounters = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.lastMonthReferredEncounters, R.string.referred_encounters, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), referredEncounters).createView());
        */

    }

    private void addCurrentMonthReferralsIssued(ViewGroup mainView){
        NumericDisplayModel currentMonthIssued = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.currentMonthIssuedByAddo, R.string.curr_month_referrals_issued, indicatorTallies);
        mainView.addView(new NumericIndicatorView(getContext(), currentMonthIssued).createView());
    }

    private void addCurrentMonthReferralsIssuedCompleted(ViewGroup mainView){
        NumericDisplayModel currentMonthIssuedCompleted = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.currentMonthIssuedAttendedByAddo, R.string.curr_month_referrals_completed, indicatorTallies);
        mainView.addView(new NumericIndicatorView(getContext(), currentMonthIssuedCompleted).createView());
    }

    private void addLastMonthReferralsIssued(ViewGroup mainView){
        NumericDisplayModel currentMonthIssued = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.lastMonthIssuedByAddo, R.string.last_month_referrals_issued, indicatorTallies);
        mainView.addView(new NumericIndicatorView(getContext(), currentMonthIssued).createView());
    }

    private void addLastMonthReferralsIssuedCompleted(ViewGroup mainView){
        NumericDisplayModel currentMonthIssuedCompleted = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.lastMonthIssuedAttendedByAddo, R.string.last_month_referrals_completed, indicatorTallies);
        mainView.addView(new NumericIndicatorView(getContext(), currentMonthIssuedCompleted).createView());
    }

    View getTitleView(String titleText){
        View tv = LayoutInflater.from(this.getContext()).inflate(R.layout.reporting_indicator_title,null, false);
        TextView title = tv.findViewById(R.id.indicator_title);
        title.setText(titleText);
        return tv;
    }

    @NonNull
    @Override
    public Loader<List<Map<String, IndicatorTally>>> onCreateLoader(int id, @Nullable Bundle args) {
        hasLoadedSampleData = Boolean.parseBoolean(AddoApplication.getInstance().getContext().allSharedPreferences().getPreference(HAS_LOADED_SAMPLE_DATA));
        if (!activityStarted){
            activityStarted = true;
            return new ReportIndicatorsLoader(getContext(), false);
        }else{
            return new ReportIndicatorsLoader(getContext(), true);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Map<String, IndicatorTally>>> loader, List<Map<String, IndicatorTally>> data) {
        setIndicatorTallies(data);
        refreshUI();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Map<String, IndicatorTally>>> loader) {

    }

    private static class ReportIndicatorsLoader extends AsyncTaskLoader<List<Map<String, IndicatorTally>>> {

        boolean loadedIndicators;

        public ReportIndicatorsLoader(Context context, boolean alreadyLoaded) {
            super(context);
            this.loadedIndicators = alreadyLoaded;
        }

        @Nullable
        @Override
        public List<Map<String, IndicatorTally>> loadInBackground() {
            List<Map<String, IndicatorTally>> empty = new ArrayList<>();
            if (!loadedIndicators) {
                AddoApplication.getInstance().getContext().allSharedPreferences().savePreference(HAS_LOADED_SAMPLE_DATA, "true");
                return presenter.fetchIndicatorsDailytallies();
            }else{
                return empty;
            }
        }
    }

}
