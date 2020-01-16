package org.smartregister.addo.contract;

import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.Set;

public interface EmptyResultFragmentContract {


    interface View extends BaseRegisterFragmentContract.View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);


        EmptyResultFragmentContract.Presenter presenter();

        void setFamilyHead(String familyHead);

        void setPrimaryCaregiver(String primaryCaregiver);

    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter {

        String getMainCondition();

        String getDefaultSortQuery();

        void setFamilyHead(String familyHead);

        void setPrimaryCaregiver(String primaryCaregiver);

    }

    interface Model {
        RegisterConfiguration defaultRegisterConfiguration();

        ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier);

        Set<org.smartregister.configurableviews.model.View> getRegisterActiveColumns(String viewConfigurationIdentifier);

        String countSelect(String tableName, String mainCondition);

        String mainSelect(String tableName, String mainCondition);
    }
}
