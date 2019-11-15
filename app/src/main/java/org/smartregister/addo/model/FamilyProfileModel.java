package org.smartregister.addo.model;

import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.model.BaseFamilyProfileModel;

public class FamilyProfileModel extends BaseFamilyProfileModel {

    public FamilyProfileModel(String familyName) {
        super(familyName);
    }

    @Override
    public void updateWra(FamilyEventClient familyEventClient) {
        //TODO Call the correct Flavor to handle this
        //new FamilyProfileModelFlv().updateWra(familyEventClient);
    }

    public interface Flavor {
        void updateWra(FamilyEventClient familyEventClient);
    }

}
