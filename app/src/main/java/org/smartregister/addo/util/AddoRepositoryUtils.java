package org.smartregister.addo.util;

public interface AddoRepositoryUtils {

    String UPGRADE_V2 = "ALTER TABLE ec_pregnancy_outcome ADD COLUMN last_interacted_with VARCHAR;";

}
