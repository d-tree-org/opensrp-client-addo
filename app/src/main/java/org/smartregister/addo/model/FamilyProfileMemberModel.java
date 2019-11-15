package org.smartregister.addo.model;

import org.apache.commons.lang3.ArrayUtils;
import org.smartregister.addo.util.ChildDBConstants;
import org.smartregister.family.model.BaseFamilyProfileMemberModel;

public class FamilyProfileMemberModel extends BaseFamilyProfileMemberModel {

    @Override
    protected String[] mainColumns(String tableName) {
        String[] columns = super.mainColumns(tableName);
        String[] newColumns = new String[]{
                tableName + "." + ChildDBConstants.KEY.ENTITY_TYPE,
                tableName + "." + org.smartregister.addo.util.Constants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER
        };

        return ArrayUtils.addAll(columns, newColumns);
    }
}