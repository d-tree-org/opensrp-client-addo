package org.smartregister.addo.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.addo.contract.SimPrintResultFragmentContract;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.TreeSet;

public class SimPrintIdentificationFrgamentPresenter implements SimPrintResultFragmentContract.Presenter {

    protected WeakReference<SimPrintResultFragmentContract.View> viewReference;

    protected SimPrintResultFragmentContract.Model model;

    protected RegisterConfiguration config;

    protected String familyBaseEntityId;
    protected String familyHead;
    protected String primaryCaregiver;


    protected Set<View> visibleColumns = new TreeSet<>();

    private String viewConfigurationIdentifier;

    public SimPrintIdentificationFrgamentPresenter(SimPrintResultFragmentContract.View view, SimPrintResultFragmentContract.Model model,
                                                   String viewConfigurationIdentifier, String familyBaseEntityId, String familyHead, String primaryCaregiver){

        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.config = model.defaultRegisterConfiguration();
        this.familyBaseEntityId = familyBaseEntityId;
        this.familyHead = familyHead;
        this.primaryCaregiver = primaryCaregiver;

    }

    @Override
    public String getMainCondition() {
        return null;
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
    public void initializeQueries(String s) {

    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String s) {

    }
}
