package org.smartregister.addo.presenter;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.addo.contract.AdvancedSearchContract;
import org.smartregister.addo.domain.Entity;
import org.smartregister.addo.interactor.AdvancedSearchInteractor;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.family.contract.FamilyRegisterFragmentContract;
import org.smartregister.family.contract.FamilyRegisterFragmentContract.Presenter;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AdvancedSearchFragmentPresenter implements Presenter, AdvancedSearchContract.Presenter, AdvancedSearchContract.InteractorCallBack {

    protected WeakReference<FamilyRegisterFragmentContract.View> viewReference;
    protected FamilyRegisterFragmentContract.Model model;
    protected RegisterConfiguration config;
    protected Set<View> visibleColumns = new TreeSet();
    protected String viewConfigurationIdentifier;
    protected AdvancedSearchContract.Interactor interactor;

    public AdvancedSearchFragmentPresenter(BaseRegisterFragmentContract.View view, FamilyRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference(view);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.config = model.defaultRegisterConfiguration();
        interactor = new AdvancedSearchInteractor();
    }

    public void processViewConfigurations() {
        if (!StringUtils.isBlank(this.viewConfigurationIdentifier)) {
            ViewConfiguration viewConfiguration = this.model.getViewConfiguration(this.viewConfigurationIdentifier);
            if (viewConfiguration != null) {
                this.config = (RegisterConfiguration)viewConfiguration.getMetadata();
                this.setVisibleColumns(this.model.getRegisterActiveColumns(this.viewConfigurationIdentifier));
            }

            if (this.config.getSearchBarText() != null && this.getView() != null) {
                this.getView().updateSearchBarHint(this.config.getSearchBarText());
            }

        }
    }

    public void initializeQueries(String mainCondition) {
        String tableName = Utils.metadata().familyRegister.tableName;
        String countSelect = this.model.countSelect(tableName, mainCondition);
        String mainSelect = this.model.mainSelect(tableName, mainCondition);
        this.getView().initializeQueryParams(tableName, countSelect, mainSelect);
        //this.getView().initializeAdapter(this.visibleColumns);
        //this.getView().countExecute();
        //this.getView().filterandSortInInitializeQueries();
    }

    public void startSync() {
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        String filterText = model.getFilterText(filterList, getView().getString(org.smartregister.R.string.filter));
        String sortText = model.getSortText(sortField);

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    public void searchGlobally(String uniqueId) {
    }

    protected AdvancedSearchContract.View getView() {
        return this.viewReference != null ? (AdvancedSearchContract.View)this.viewReference.get() : null;
    }

    private void setVisibleColumns(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

    public void setModel(FamilyRegisterFragmentContract.Model model) {
        this.model = model;
    }

    public String getMainCondition() {
        return String.format(" %s is null ", "date_removed");
    }

    @Override
    public String getDefaultSortQuery() {
        return DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }

    @Override
    public String getQueryTable() {
        return null;
    }

    public void search(String searchText) {
        Log.d("Search", "searching in presenter: "  + searchText);
        interactor.search(searchText, this);
    }

    public void onResultsFound(List<Entity> members) {
        System.out.println("Member size: " + members.size());

        getView().showResults(members);
    }
}
