package org.smartregister.addo.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.addo.contract.SimPrintResultFragmentContract;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class SimPrintIdentificationFragmentPresenter implements SimPrintResultFragmentContract.Presenter {

    protected WeakReference<SimPrintResultFragmentContract.View> viewReference;

    protected SimPrintResultFragmentContract.Model model;

    protected RegisterConfiguration config;

    protected ArrayList<String> ids;



    protected Set<View> visibleColumns = new TreeSet<>();

    private String viewConfigurationIdentifier;

    public SimPrintIdentificationFragmentPresenter(SimPrintResultFragmentContract.View view, SimPrintResultFragmentContract.Model model,
                                                   String viewConfigurationIdentifier, ArrayList<String> ids){

        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.config = model.defaultRegisterConfiguration();
        this.ids = ids;


    }

    @Override
    public String getMainCondition() {
        //return  String.format(" %s is null ", "date_removed");
        String mainCondition = "";
        StringBuilder stringBuilder= new StringBuilder();
        for (int i=0; i < ids.size(); i++) {

            stringBuilder.append("'"+ids.get(i)+"'").append(",");
        }

        String stringIds = stringBuilder.toString();

        if (stringIds.length() > 0) {
            mainCondition = String.format(" %s IN (" + stringIds.substring(0, stringIds.length()-1)+ ") AND %s is null ", DBConstants.KEY.OBJECT_ID, DBConstants.KEY.DATE_REMOVED);
        }

        return mainCondition;

    }

    @Override
    public String getDefaultSortQuery() {
        return null;
    }

    @Override
    public void setFamilyHead(String familyHead) {

    }

    @Override
    public void setPrimaryCaregiver(String primaryCaregiver) {

    }

    @Override
    public void processViewConfigurations() {

        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return;
        }

        ViewConfiguration viewConfiguration = model.getViewConfiguration(viewConfigurationIdentifier);
        if (viewConfiguration != null) {
            config = (RegisterConfiguration) viewConfiguration.getMetadata();
            setVisibleColumns(model.getRegisterActiveColumns(viewConfigurationIdentifier));
        }

        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(config.getSearchBarText());
        }

    }

    protected SimPrintResultFragmentContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    private void setVisibleColumns(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }



    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = Utils.metadata().familyMemberRegister.tableName;
        String countSelect = this.model.countSelect(tableName, mainCondition);
        String mainSelect = this.model.mainSelect(tableName, mainCondition);
        this.getView().initializeQueryParams(tableName, countSelect, mainSelect);
        this.getView().initializeAdapter(this.visibleColumns);
        this.getView().countExecute();
        this.getView().filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String s) {

    }
}
