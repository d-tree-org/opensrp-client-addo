package org.smartregister.addo.dao;

import org.smartregister.addo.repository.AdolescentCloseRepository;
import org.smartregister.dao.AbstractDao;
import java.text.ParseException;

import java.util.List;

import timber.log.Timber;

public class AdolescentDao extends AbstractDao {

    public static boolean isAdolescentMember(String baseEntityID) {

        String sql = "select count(*) count from ec_adolescent where base_entity_id = '" + baseEntityID + "' and is_closed = 0";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);

        if (res == null || res.size() != 1) {
            return false;
        }

        return res.get(0) > 0;
    }

    public static void graduateAdolescentToAdult(int cutOffAge, AdolescentCloseRepository adolescentCloseRepository) {
        adolescentCloseRepository.graduateAdolescent(cutOffAge);
    }

}