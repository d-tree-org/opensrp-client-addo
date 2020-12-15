package org.smartregister.addo.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.addo.contract.MonthlyActivitiesFragmentContract;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import timber.log.Timber;

public class MonthlyActivityRegisterFragmentPresenter implements MonthlyActivitiesFragmentContract.Presenter {

    protected WeakReference<MonthlyActivitiesFragmentContract.View> viewReference;
    protected MonthlyActivitiesFragmentContract.Model model;
    protected RegisterConfiguration config;
    protected Set<View> visibleColumns = new TreeSet();
    protected String viewConfigurationIdentifier;

    public MonthlyActivityRegisterFragmentPresenter(MonthlyActivitiesFragmentContract.View view, MonthlyActivitiesFragmentContract.Model model, String viewConfigurationIdentifier){
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.config = model.defaultRegisterConfiguration();
    }

    @Override
    public void updateSortAndFilter(List<Field> var1, Field var2) {
    }

    @Override
    public String getMainCondition() {
        return "";
    }

    @Override
    public String getDefaultSortQuery() {
        return "";
    }

    @Override
    public String getMainTable() {
        return null;
    }

    @Override
    public String getDueFilterCondition() {
        return "";
    }

    @Override
    public void processViewConfigurations() {
        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return;
        }

        ViewConfiguration viewConfiguration = model.getViewConfiguration(viewConfigurationIdentifier);
        if (viewConfiguration != null) {
            config = (RegisterConfiguration) viewConfiguration.getMetadata();
            this.visibleColumns = model.getRegisterActiveColumns(viewConfigurationIdentifier);
        }

        try {
            if (config.getSearchBarText() != null && getView() != null) {
                getView().updateSearchBarHint(config.getSearchBarText());
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    protected MonthlyActivitiesFragmentContract.View getView(){
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    @Override
    public void initializeQueries(String s) {

    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String s) {

    }

}
