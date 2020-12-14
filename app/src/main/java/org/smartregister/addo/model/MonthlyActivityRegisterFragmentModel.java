package org.smartregister.addo.model;

import org.json.JSONArray;
import org.smartregister.addo.contract.MonthlyActivitiesFragmentContract;
import org.smartregister.chw.anc.util.ConfigHelper;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.Response;

import java.util.List;
import java.util.Set;

public class MonthlyActivityRegisterFragmentModel implements MonthlyActivitiesFragmentContract.Model {

    @Override
    public RegisterConfiguration defaultRegisterConfiguration() {
        return null;
    }

    @Override
    public ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier) {
        return ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().getViewConfiguration(viewConfigurationIdentifier);
    }

    @Override
    public Set<View> getRegisterActiveColumns(String viewConfigurationIdentifier) {
        return ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().getRegisterActiveColumns(viewConfigurationIdentifier);
    }

    @Override
    public String countSelect(String var1, String var2) {
        return "";
    }

    @Override
    public String mainSelect(String var1, String var2) {
        return "";
    }

    @Override
    public String getFilterText(List<Field> var1, String var2) {
        return "";
    }

    @Override
    public String getSortText(Field var1) {
        return "";
    }

    @Override
    public JSONArray getJsonArray(Response<String> var1) {
        return null;
    }

}
