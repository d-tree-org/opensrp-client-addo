package org.smartregister.addo.presenter;

import org.smartregister.addo.contract.AddoHomeFragmentContract;
import org.smartregister.addo.model.AddoHomeFragmentModel;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AddoHomeFragmentPresenter implements AddoHomeFragmentContract.Presenter {

    protected WeakReference<AddoHomeFragmentContract.View> viewReference;

    protected AddoHomeFragmentContract.Model model;

    protected RegisterConfiguration config;


    protected Set<View> visibleColumns = new TreeSet<>();

    private String viewConfigurationIdentifier;

    public AddoHomeFragmentPresenter(AddoHomeFragmentContract.View viewReference, AddoHomeFragmentContract.Model model,
                                     String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference<>(viewReference);
        this.model = AddoHomeFragmentModel.getInstance();
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
    }

    @Override
    public void processViewConfigurations() {
    }

    private void setVisibleColumns(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

    protected AddoHomeFragmentContract.View getView() {
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

    @Override
    public List<String> getLocations() {
        return model.getAddoUserAllowedLocation();
    }
}
