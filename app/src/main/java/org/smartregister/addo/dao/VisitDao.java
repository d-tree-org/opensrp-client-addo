package org.smartregister.addo.dao;

import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.dao.AbstractDao;

import java.util.List;

public class VisitDao extends AbstractDao {

    public static Visit getLatestVisit(String baseEntityID, String visitType, String encounterType) {
        String sql = "select * from visits\n" +
                "inner join visit_details on visits.visit_id = visit_details.visit_id\n" +
                "where visits.base_entity_id = '" + baseEntityID + "'\n" +
                "and visits.visit_type = '" + visitType + "'\n" +
                "and visit_details.visit_key = 'encounter_type'\n" +
                "and visit_details.details = '" + encounterType + "'\n" +
                "order by visits.visit_date DESC limit 1;";

        DataMap<List<Visit>> dataMap = cursor -> {
            List<Visit> visits= AncLibrary.getInstance().visitRepository().readVisits(cursor);
            if (cursor != null)
                cursor.close();

            return visits;
        };

        List<Visit> visits = readData(sql, dataMap).get(0);
        if (visits == null || visits.size() != 1)
            return null;

        return visits.get(0);
    }
}
