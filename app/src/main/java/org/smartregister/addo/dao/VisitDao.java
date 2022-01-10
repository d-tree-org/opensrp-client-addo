package org.smartregister.addo.dao;

import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.dao.AbstractDao;

import java.util.Date;
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

        DataMap<Visit> dataMap = cursor -> {
            Visit visit = new Visit();
            visit.setVisitId(getCursorValue(cursor, "visit_id"));
            visit.setVisitType(getCursorValue(cursor, "visit_type"));
            visit.setParentVisitID(getCursorValue(cursor, "parent_visit_id"));
            visit.setPreProcessedJson(getCursorValue(cursor, "pre_processed"));
            visit.setBaseEntityId(getCursorValue(cursor, "base_entity_id"));
            visit.setDate(new Date(Long.parseLong(getCursorValue(cursor, "visit_date"))));
            visit.setJson(getCursorValue(cursor, "visit_json"));
            visit.setFormSubmissionId(getCursorValue(cursor, "form_submission_id"));
            visit.setProcessed(getCursorIntValue(cursor, "processed") == 1);
            visit.setCreatedAt(new Date(Long.parseLong(getCursorValue(cursor, "created_at"))));
            visit.setUpdatedAt(new Date(Long.parseLong(getCursorValue(cursor, "updated_at"))));

            return visit;
        };

        List<Visit> visits = readData(sql, dataMap);
        if (visits == null || visits.size() != 1)
            return null;

        return visits.get(0);
    }
}
