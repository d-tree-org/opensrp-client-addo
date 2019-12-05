package org.smartregister.addo.model;

import org.json.JSONObject;
import org.smartregister.addo.contract.SimPrintIdentificationRegisterContract;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.Utils;
import org.smartregister.location.helper.LocationHelper;

import java.util.List;

public class SimPrintIdentificationRegisterModel implements SimPrintIdentificationRegisterContract.Model {

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void saveLanguage(String language) {

    }

    @Override
    public String getLocationId(String locationName) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationName);
    }

    @Override
    public List<FamilyEventClient> processRegistration(String jsonString) {
        return null;
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        return null;
    }

    @Override
    public String getInitials() {
        return Utils.getUserInitials();
    }
}
