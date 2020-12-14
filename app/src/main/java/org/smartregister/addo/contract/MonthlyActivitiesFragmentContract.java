package org.smartregister.addo.contract;

import org.json.JSONArray;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;
import java.util.Set;

public interface MonthlyActivitiesFragmentContract {

    public interface Model {

        RegisterConfiguration defaultRegisterConfiguration();

        ViewConfiguration getViewConfiguration(String var1);

        Set<org.smartregister.configurableviews.model.View> getRegisterActiveColumns(String var1);

        String countSelect(String var1, String var2);

        String mainSelect(String var1, String var2);

        String getFilterText(List<Field> var1, String var2);

        String getSortText(Field var1);

        JSONArray getJsonArray(Response<String> var1);
    }

    public interface View extends BaseRegisterFragmentContract.View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> var1);

        MonthlyActivitiesFragmentContract.Presenter presenter();
    }

    public interface Presenter extends BaseRegisterFragmentContract.Presenter {

        void updateSortAndFilter(List<Field> var1, Field var2);

        String getMainCondition();

        String getDefaultSortQuery();

        String getMainTable();

        String getDueFilterCondition();

    }

}
