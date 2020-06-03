package org.smartregister.addo.dao;

import android.database.Cursor;
import android.util.Pair;

import org.joda.time.DateTime;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.domain.Entity;
import android.content.ContentValues;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.addo.util.CoreConstants;
import org.smartregister.dao.AbstractDao;
import org.smartregister.domain.Task;
import org.smartregister.repository.BaseRepository;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;
import static org.smartregister.AllConstants.SYNC_STATUS;
import static org.smartregister.addo.util.CoreConstants.BUSINESS_STATUS.LINKED;
import static org.smartregister.addo.util.CoreConstants.DB_CONSTANTS.BUSINESS_STATUS;
import static org.smartregister.addo.util.CoreConstants.DB_CONSTANTS.FOR;
import static org.smartregister.addo.util.CoreConstants.DB_CONSTANTS.STATUS;

public class FamilyDao extends AbstractDao {

    public static Map<String, Integer> getFamilyServiceSchedule(String familyBaseEntityID) {
        String sql = "select visit_state , count(*) totals  from (  " +
                "SELECT s.base_entity_id , m.relational_id , CASE  " +
                "WHEN completion_date  is NOT NULL  THEN  'DONE'  " +
                "WHEN not_done_date is NOT NULL THEN  'NOT_VISIT_THIS_MONTH' " +
                "WHEN strftime('%Y-%m-%d') BETWEEN due_date AND over_due_date THEN  'DUE' " +
                "WHEN strftime('%Y-%m-%d') BETWEEN over_due_date AND expiry_date THEN  'OVERDUE' " +
                "WHEN strftime('%Y-%m-%d')  >= expiry_date  THEN  'EXPIRY'  end  visit_state " +
                "FROM schedule_service s " +
                "LEFT join ec_family_member m on s.base_entity_id  = m.base_entity_id COLLATE NOCASE " +
                "WHERE visit_state is NOT NULL " +
                ") counters   where " +
                " ((counters.relational_id = '" + familyBaseEntityID + "' COLLATE NOCASE) or " +
                " (counters.base_entity_id = '" + familyBaseEntityID + "' COLLATE NOCASE)) " +
                "group by visit_state";


        DataMap<Pair<String, Integer>> dataMap = c -> Pair.create(getCursorValue(c, "visit_state"), getCursorIntValue(c, "totals"));

        Map<String, Integer> visits = new HashMap<>();
        List<Pair<String, Integer>> pairs = AbstractDao.readData(sql, dataMap);
        if (pairs == null || pairs.size() == 0)
            return visits;

        for (Pair<String, Integer> pair : pairs) {
            visits.put(pair.first, pair.second);
        }

        return visits;
    }

    public static String getMemberDueStatus(String memberBaseEntityId) {
        String sql = "select visit_state from (  " +
                "SELECT m.relational_id , CASE  " +
                "WHEN completion_date  is NOT NULL  THEN  'DONE'  " +
                "WHEN not_done_date is NOT NULL THEN  'NOT_VISIT_THIS_MONTH' " +
                "WHEN strftime('%Y-%m-%d') BETWEEN due_date AND over_due_date THEN  'DUE' " +
                "WHEN strftime('%Y-%m-%d') BETWEEN over_due_date AND expiry_date THEN  'OVERDUE' " +
                "WHEN strftime('%Y-%m-%d')  >= expiry_date  THEN  'EXPIRY'  end  visit_state " +
                "FROM schedule_service s " +
                "inner join ec_family_member m on s.base_entity_id  ='" + memberBaseEntityId + "'" + " COLLATE NOCASE " +
                "WHERE visit_state is NOT NULL " +
                ") counters " +
                "group by visit_state";

        DataMap<String> dataMap = c -> getCursorValue(c, "visit_state");
        String dueStatus;
        List<String> dueStatusListString = AbstractDao.readData(sql, dataMap);
        if (dueStatusListString.size() > 0) {
            dueStatus = dueStatusListString.get(0);
        } else {
            dueStatus = "";
        }

        return dueStatus;

    }

    public static long getFamilyCreateDate(String familyBaseEntityID) {
        String sql = "select eventDate from event where eventType = 'Family Registration' and " +
                "baseEntityId = '" + familyBaseEntityID + "' order by eventDate desc limit 1";

        DataMap<Date> dataMap = c -> getCursorValueAsDate(c, "eventDate", getDobDateFormat());
        List<Date> res = AbstractDao.readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return 0;

        return res.get(0).getTime();
    }

    public static boolean isFamily(String baseEntityID) {
        String sql = "select count(*) count from ec_family where base_entity_id = '" + baseEntityID + "'";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }

    public static List<Entity> search(String searchText) {
        Cursor cursor = null;
        List<Entity> entitySet = new ArrayList<>();
        try {
            String query = String.format("SELECT * FROM ec_family_member WHERE first_name LIKE '%%%s%%' " +
                    "OR middle_name LIKE '%%%s%%' OR last_name LIKE '%%%s%%'", searchText, searchText, searchText);
            cursor = AddoApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query,
                    new String[]{});
            while (cursor.moveToNext()) {
                Entity entity = readCursor(cursor);
                entitySet.add(entity);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return entitySet;
    }

    private static Entity readCursor(Cursor cursor) {
        Entity entity = new Entity();

        entity.setBaseEntityId(cursor.getString(cursor.getColumnIndex("base_entity_id")));
        entity.setFirstName(cursor.getString(cursor.getColumnIndex("first_name")));
        entity.setMiddleName(cursor.getString(cursor.getColumnIndex("middle_name")));
        entity.setLastName(cursor.getString(cursor.getColumnIndex("last_name")));
        entity.setGender(cursor.getString(cursor.getColumnIndex("gender")));
        entity.setFamilyId(cursor.getString(cursor.getColumnIndex("relational_id")));

        return entity;
    }

    public static void completeTasksForEntity(@NonNull String entityId) {
        if (StringUtils.isBlank(entityId))
            return;
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, Task.TaskStatus.IN_PROGRESS.name());
        contentValues.put(SYNC_STATUS, BaseRepository.TYPE_Unsynced);
        contentValues.put("last_modified", DateUtil.getMillis(new DateTime()));

        AddoApplication.getInstance().getRepository().getWritableDatabase().update("task", contentValues,
                String.format("%s = ? AND %s =? AND %s =?", FOR, STATUS, BUSINESS_STATUS), new String[]{entityId, Task.TaskStatus.READY.name(), LINKED});
    }
}
