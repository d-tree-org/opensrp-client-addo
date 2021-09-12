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
import org.smartregister.addo.util.ChartUtil;
import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.domain.IndicatorQuery;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.ReportIndicator;
import org.smartregister.reporting.model.NumericDisplayModel;
import org.smartregister.reporting.view.NumericIndicatorView;

import java.io.CharArrayReader;
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

    MonthlyActivityDashboard(){}

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
        }
        indicators.add(indicator);
    }

    private void withQuery(String indicatorCode, List<IndicatorQuery> queries){
        IndicatorQuery iQuery = new IndicatorQuery();
        iQuery.setIndicatorCode(indicatorCode);
        iQuery.setDbVersion(0);
        iQuery.setId(null);
        switch (indicatorCode){
            case ChartUtil.adolescentEncounter:
                String currentMonthAdolescent = "select count(*) from ( " +
                        "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                        "from visits " +
                        "where datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') " +
                        "and visit_type in ('Adolescent ADDO Visit' ) " +
                        "and visit_json like \"%"+anmUser+"%\" "+
                        "group by base_entity_id, date_visited" +
                        ")";
                iQuery.setQuery(currentMonthAdolescent);
                break;
            case ChartUtil.childEncounter:
                String currentMonthChild = "select count(*) from ( " +
                        "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                        "from visits " +
                        "where datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') " +
                        "and visit_type in ('Child ADDO Visit' ) " +
                        "and visit_json like \"%"+anmUser+"%\" "+
                        "group by base_entity_id, date_visited" +
                        ")";
                iQuery.setQuery(currentMonthChild);
                break;
            case ChartUtil.ancEncounter:
                String currentMonthAnc = "select count(*) from ( " +
                        "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                        "from visits " +
                        "where datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') " +
                        "and visit_type in ('ANC ADDO Visit' ) " +
                        "and visit_json like \"%"+anmUser+"%\" "+
                        "group by base_entity_id, date_visited" +
                        ")";
                iQuery.setQuery(currentMonthAnc);
                break;
            case ChartUtil.pncEncounter:
                String currentMonthPnc = "select count(*) from ( " +
                        "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                        "from visits " +
                        "where datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') " +
                        "and visit_type in ('PNC ADDO Visit' ) " +
                        "and visit_json like \"%"+anmUser+"%\" "+
                        "group by base_entity_id, date_visited" +
                        ")";
                iQuery.setQuery(currentMonthPnc);
                break;
            case ChartUtil.otherEncounter:
                String currentMonthOther = "select count(*) from ( " +
                        "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                        "from visits " +
                        "where datetime(visit_date/1000, 'unixepoch') < date('now', 'start of month') " +
                        "and visit_type in ('Other Member ADDO Visit') " +
                        "and visit_json like \"%"+anmUser+"%\" "+
                        "group by base_entity_id, date_visited" +
                        ")";
                iQuery.setQuery(currentMonthOther);
                break;
            case ChartUtil.lastMonthTotalEncounters:
                String lastMonthTotal = "select count(*) from ( " +
                        "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                        "from visits " +
                        "where datetime(visit_date/1000, 'unixepoch') < date('now', 'start of month') " +
                        "and datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month', '-1 months') " +
                        "and visit_type in ('ANC ADDO Visit', 'Child ADDO Visit', 'PNC ADDO Visit', 'Adolescent ADDO Visit', 'Other Member ADDO Visit' ) " +
                        "and visit_json like \"%"+anmUser+"%\" "+
                        "group by base_entity_id, date_visited" +
                        ")";
                iQuery.setQuery(lastMonthTotal);
                break;
            case ChartUtil.lastMonthReferredEncounters:
                String lastMonthReferred = "select count(*) from ( " +
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
                iQuery.setQuery(lastMonthReferred);
                break;
            case ChartUtil.currentMonthLinked:
                String currentMonthLinkedClients = "select count(*) from tasks where code = 'Linked' and " +
                        "datetime(authored_on/1000, 'unixepoch') > date('now', 'start of month')";
                iQuery.setQuery(currentMonthLinkedClients);
                break;
            case ChartUtil.currentMonthLinkedAttended:
                String currentMonthLinkedAttendedClients = "select count(*) from tasks where code = 'Linked' and " +
                        "business_status = 'Attended' and " +
                        "datetime(authored_on/1000, 'unixepoch') > date('now', 'start of month')";
                iQuery.setQuery(currentMonthLinkedAttendedClients);
                break;
            case ChartUtil.lastMonthLinked:
                String lastMonthLinkedClients = "select count(*) from tasks where code = 'Linked' and " +
                        "datetime(authored_on/1000, 'unixepoch') > date('now', 'start of month', -1 months) and" +
                        " datetime(authored_on/1000, 'unixepoch') < date('now', 'start of month')";
                iQuery.setQuery(lastMonthLinkedClients);
                break;
            case ChartUtil.lastMonthAttendedLinked:
                String lastMonthLinkedAttendedClients = "select count(*) from tasks where code = 'Linked' and " +
                        "business_status = 'Attended' and " +
                        "datetime(authored_on/1000, 'unixepoch') < date('now', 'start of month') and " +
                        "datetime(authored_on/1000, 'unixepoch') > date('now', 'start of month', '-1 months')";
                iQuery.setQuery(lastMonthLinkedAttendedClients);
                break;
            case ChartUtil.currentMonthVisits:
                String currentMonthVisits = "select count(*) from visits where " +
                        "visit_type in ('Adolescent ADDO Visit','Child ADDO Visit','ANC ADDO Visit','PNC ADDO Visit', 'Other Member ADDO Visit)" +
                        " and datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') ";
                iQuery.setQuery(currentMonthVisits);
                break;
            case ChartUtil.lastMonthVisits:
                String lastMonthVisits = "select count(*) from visits where " +
                        "visit_type in ('Adolescent ADDO Visit','Child ADDO Visit','ANC ADDO Visit','PNC ADDO Visit', 'Other Member ADDO Visit)" +
                        " and datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month', '-1 months)";
                iQuery.setQuery(lastMonthVisits);
                break;
        }
        queries.add(iQuery);
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

        addPercentageLinked(mainLayout);

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

        //Add Space between indicators
        mainLayout.addView(spacerView);

        mainLayout.addView(getTitleView(getResources().getString(R.string.last_month)));

        NumericDisplayModel visitsConductedLastMonth = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.lastMonthVisits, R.string.last_month_visits, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), visitsConductedLastMonth).createView());

        addPercentageLinkedLastMonth(mainLayout);

        NumericDisplayModel totalEncounters = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.lastMonthTotalEncounters, R.string.total_encounters, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), totalEncounters).createView());

        NumericDisplayModel referredEncounters = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.lastMonthReferredEncounters, R.string.referred_encounters, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), referredEncounters).createView());

    }

    private void addPercentageLinked(ViewGroup topView){
        NumericDisplayModel currentMonthLinked = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.currentMonthLinked, R.string.current_month, indicatorTallies);
        NumericDisplayModel currentMonthLinkedAttended = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.currentMonthLinkedAttended, R.string.current_month, indicatorTallies);
        int percentage = 0;
        if (currentMonthLinkedAttended.getCount() > 0 && currentMonthLinked.getCount() > 0 ){
            percentage =  Float.floatToIntBits(currentMonthLinkedAttended.getCount()/currentMonthLinked.getCount())*100;
        }

        NumericDisplayModel percentageVisitsForLinked = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.percentageAttended, R.string.curr_month_percentage_attended_visits, indicatorTallies);
        percentageVisitsForLinked.setCount(percentage);
        topView.addView(new NumericIndicatorView(getContext(), percentageVisitsForLinked).createView());
    }

    private void addPercentageLinkedLastMonth(ViewGroup topView){
        NumericDisplayModel lastMonthLinked = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.lastMonthLinked, R.string.last_month, indicatorTallies);
        NumericDisplayModel lastMonthLinkedAttended = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.lastMonthAttendedLinked, R.string.last_month, indicatorTallies);
        int percentage = 0;
        if (lastMonthLinked.getCount() > 0 && lastMonthLinkedAttended.getCount() > 0){
            percentage =  Float.floatToIntBits(lastMonthLinkedAttended.getCount()/lastMonthLinked.getCount())*100;
        }

        NumericDisplayModel percentageVisitsForLinked = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.percentageAttended, R.string.last_month_percentage_attended_visits, indicatorTallies);
        percentageVisitsForLinked.setCount(percentage);
        topView.addView(new NumericIndicatorView(getContext(), percentageVisitsForLinked).createView());
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
