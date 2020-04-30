package org.smartregister.addo.contract;

import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;
import java.util.Set;

public interface AddoHomeFragmentContract {

    interface View extends BaseRegisterFragmentContract.View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);


        AddoHomeFragmentContract.Presenter presenter();

    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter{

        List<String> getLocations();

        void processViewConfigurations();

    }

    interface Model {

        RegisterConfiguration defaultRegisterConfiguration();

        ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier);

        Set<org.smartregister.configurableviews.model.View> getRegisterActiveColumns(String viewConfigurationIdentifier);

        List<String> getAddoUserAllowedLocation();
    }

    interface Interactor {

    }
}
