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

        String currentMonthAdolescent = "select count(*) from ( " +
                "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                "from visits " +
                "where datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') " +
                "and visit_type in ('Adolescent ADDO Visit' ) " +
                "and visit_json like \"%"+anmUser+"%\" "+
                "group by base_entity_id, date_visited" +
                ")";

        String currentMonthChild = "select count(*) from ( " +
                "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                "from visits " +
                "where datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') " +
                "and visit_type in ('Child ADDO Visit' ) " +
                "and visit_json like \"%"+anmUser+"%\" "+
                "group by base_entity_id, date_visited" +
                ")";

        String currentMonthAnc = "select count(*) from ( " +
                "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                "from visits " +
                "where datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') " +
                "and visit_type in ('ANC ADDO Visit' ) " +
                "and visit_json like \"%"+anmUser+"%\" "+
                "group by base_entity_id, date_visited" +
                ")";

        String currentMonthPnc = "select count(*) from ( " +
                "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                "from visits " +
                "where datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') " +
                "and visit_type in ('PNC ADDO Visit' ) " +
                "and visit_json like \"%"+anmUser+"%\" "+
                "group by base_entity_id, date_visited" +
                ")";

        String currentMonthOther = "select count(*) from ( " +
                "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                "from visits " +
                "where datetime(visit_date/1000, 'unixepoch') < date('now', 'start of month') " +
                "and visit_type in ('Other Member ADDO Visit') " +
                "and visit_json like \"%"+anmUser+"%\" "+
                "group by base_entity_id, date_visited" +
         ")";

        String lastMonthTotal = "select count(*) from ( " +
                "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                "from visits " +
                "where datetime(visit_date/1000, 'unixepoch') < date('now', 'start of month') " +
                "and datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month', '-1 months') " +
                "and visit_type in ('ANC ADDO Visit', 'Child ADDO Visit', 'PNC ADDO Visit', 'Adolescent ADDO Visit', 'Other Member ADDO Visit' ) " +
                "and visit_json like \"%"+anmUser+"%\" "+
                "group by base_entity_id, date_visited" +
                ")";

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


        ReportIndicator currentMonthAdolescentVisits = new ReportIndicator();
        currentMonthAdolescentVisits.setKey(ChartUtil.adolescentEncounter);
        currentMonthAdolescentVisits.setDescription("Adolescent visits conducted in the current month");
        reportIndicators.add(currentMonthAdolescentVisits);

        ReportIndicator currentMonthANCVisits = new ReportIndicator();
        currentMonthANCVisits.setKey("IND_CURR_ANC");
        currentMonthANCVisits.setDescription("ANC visits conducted in the current month");
        reportIndicators.add(currentMonthANCVisits);

        ReportIndicator currentMonthPNCVisits = new ReportIndicator();
        currentMonthPNCVisits.setKey("IND_CURR_PNC");
        currentMonthPNCVisits.setDescription("PNC visits conducted in the current month");
        reportIndicators.add(currentMonthPNCVisits);

        ReportIndicator currentMonthChildVisits = new ReportIndicator();
        currentMonthChildVisits.setKey("IND_CURR_CHILD");
        currentMonthChildVisits.setDescription("Children visits conducted in the current month");
        reportIndicators.add(currentMonthChildVisits);

        ReportIndicator currentMonthOtherVisits = new ReportIndicator();
        currentMonthOtherVisits.setKey("IND_CURR_OTHER");
        currentMonthOtherVisits.setDescription("Other client visits conducted in the current month");
        reportIndicators.add(currentMonthOtherVisits);

        ReportIndicator lastMonthTotalVisits = new ReportIndicator();
        lastMonthTotalVisits.setKey("IND_LAST_TOTAL");
        lastMonthTotalVisits.setDescription("Total Visits conducted in the last month");
        reportIndicators.add(lastMonthTotalVisits);

        ReportIndicator lastMonthReferredVisits = new ReportIndicator();
        lastMonthReferredVisits.setKey("IND_LAST_REFERRED");
        lastMonthReferredVisits.setDescription("Visits conducted in the current month that were referred");
        reportIndicators.add(lastMonthReferredVisits);

        IndicatorQuery curMonthAddo = new IndicatorQuery();
        curMonthAddo.setIndicatorCode(ChartUtil.adolescentEncounter);
        curMonthAddo.setDbVersion(0);
        curMonthAddo.setId(null);
        curMonthAddo.setQuery(currentMonthAdolescent);
        indicatorQueries.add(curMonthAddo);

        IndicatorQuery curMonthChild = new IndicatorQuery();
        curMonthChild.setIndicatorCode(ChartUtil.childEncounter);
        curMonthChild.setDbVersion(0);
        curMonthChild.setId(null);
        curMonthChild.setQuery(currentMonthChild);
        indicatorQueries.add(curMonthChild);

        IndicatorQuery curMonthANC = new IndicatorQuery();
        curMonthANC.setIndicatorCode(ChartUtil.ancEncounter);
        curMonthANC.setDbVersion(0);
        curMonthANC.setId(null);
        curMonthANC.setQuery(currentMonthAnc);
        indicatorQueries.add(curMonthANC);

        IndicatorQuery curMonthPNC = new IndicatorQuery();
        curMonthPNC.setIndicatorCode(ChartUtil.pncEncounter);
        curMonthPNC.setDbVersion(0);
        curMonthPNC.setId(null);
        curMonthPNC.setQuery(currentMonthPnc);
        indicatorQueries.add(curMonthPNC);

        IndicatorQuery curMonthOther = new IndicatorQuery();
        curMonthOther.setIndicatorCode(ChartUtil.otherEncounter);
        curMonthOther.setDbVersion(0);
        curMonthOther.setId(null);
        curMonthOther.setQuery(currentMonthOther);
        indicatorQueries.add(curMonthOther);

        IndicatorQuery lastMonthTotalIndicatorQuery = new IndicatorQuery();
        lastMonthTotalIndicatorQuery.setIndicatorCode(ChartUtil.lastMonthTotalEncounters);
        lastMonthTotalIndicatorQuery.setDbVersion(0);
        lastMonthTotalIndicatorQuery.setId(null);
        lastMonthTotalIndicatorQuery.setQuery(lastMonthTotal);
        indicatorQueries.add(lastMonthTotalIndicatorQuery);

        IndicatorQuery lastMonthReferredIndicatorQuery = new IndicatorQuery();
        lastMonthReferredIndicatorQuery.setIndicatorCode(ChartUtil.lastMonthReferredEncounters);
        lastMonthReferredIndicatorQuery.setDbVersion(0);
        lastMonthReferredIndicatorQuery.setId(null);
        lastMonthReferredIndicatorQuery.setQuery(lastMonthReferred);
        indicatorQueries.add(lastMonthReferredIndicatorQuery);

        presenter.addIndicators(reportIndicators);
        presenter.addIndicatorQueries(indicatorQueries);

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
        NumericDisplayModel totalEncounters = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.lastMonthTotalEncounters, R.string.total_encounters, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), totalEncounters).createView());

        NumericDisplayModel referredEncounters = getIndicatorDisplayModel(LATEST_COUNT, ChartUtil.lastMonthReferredEncounters, R.string.referred_encounters, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), referredEncounters).createView());

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
