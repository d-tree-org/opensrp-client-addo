package org.smartregister.addo.presenter;

import org.smartregister.addo.contract.ScanFingerPrintFragmentContract;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;

import java.util.Set;
import java.util.TreeSet;

public class ScanFingerPrintFragmentPresenter implements ScanFingerPrintFragmentContract.Presenter {

    protected ScanFingerPrintFragmentContract.View viewReference;

    protected RegisterConfiguration config;


    protected Set<View> visibleColumns = new TreeSet<>();

    private String viewConfigurationIdentifier;

    public ScanFingerPrintFragmentPresenter(ScanFingerPrintFragmentContract.View viewReference,  String viewConfigurationIdentifier) {
        this.viewReference = viewReference;
        this.config = config;
        this.visibleColumns = visibleColumns;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String mainCondition) {

    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String uniqueId) {

    }
}
