package org.smartregister.addo.repository;

import static org.smartregister.family.util.DBConstants.KEY.BASE_ENTITY_ID;

import android.database.Cursor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.CoreLibrary;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.domain.Task;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.repository.Repository;

/**
 * Created by Kassim Sheghembe on 2021-08-18
 */
public class AddoWeeklySummaryRepository {

    private final Repository repository;
    private AppExecutors appExecutors;

    @VisibleForTesting
    AddoWeeklySummaryRepository(Repository repository, AppExecutors appExecutors) {
        this.repository = repository;
        this.appExecutors = appExecutors;
    }

    public AddoWeeklySummaryRepository() {
        this(AddoApplication.getInstance().getRepository(), new AppExecutors());
    }

    public void getReferralCounts(WeeklySummaryCallback callback) {

        Runnable runnable = () -> {
            final String numRef;
            numRef = queryReferralCounts();
            appExecutors.mainThread().execute(() -> callback.onComplete(numRef));
        };

        appExecutors.diskIO().execute(runnable);
    }

    private String queryReferralCounts() {
        Cursor cursor = null;
        try {
            String query = "select * from task where priority = '2' and " +
                    "date(datetime(start/1000, 'unixepoch')) > datetime('now', 'start of day', '-6 days');";
            cursor = repository.getReadableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            return Integer.toString(cursor.getCount());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "0";
    }

    public void getClosedRefferalCount(WeeklySummaryCallback weeklySummaryCallback) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final String closedRefCounts;
                closedRefCounts = queryClosedReferralCounts();
                appExecutors.mainThread().execute(() -> weeklySummaryCallback.onComplete(closedRefCounts));
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    private String queryClosedReferralCounts() {
        Cursor cursor = null;

        try {
            String query = "select * from task where priority = '2' and " +
                    "date(datetime(start/1000, 'unixepoch')) > datetime('now', 'start of day', '-6 days') and " +
                    "status IN ('" + Task.TaskStatus.COMPLETED + "', '" + Task.TaskStatus.IN_PROGRESS +"');";
            cursor = repository.getReadableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            return Integer.toString(cursor.getCount());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "0";
    }

    public void getAddoWeeklyVisit(WeeklySummaryCallback weeklySummaryCallback) {
        Runnable runnable = () -> {
            final String visitsCounts;
            visitsCounts = queryVisitsAddo();
            appExecutors.mainThread().execute(() -> weeklySummaryCallback.onComplete(visitsCounts));
        };
        appExecutors.diskIO().execute(runnable);
    }

    private String queryVisitsAddo() {
        String addoUser = CoreLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM();
        Cursor cursor = null;

        try {
            String query = "select * from visits where visit_json like '%\"providerId\":\""+addoUser+"\"%' and " +
                    "visit_type IN (" +
                    "'Child ADDO Visit', " +
                    "'ANC ADDO Visit', " +
                    "'PNC ADDO Visit', " +
                    "'Other Member ADDO Visit', " +
                    "'Adolescent ADDO Visit') and " +
                    "date(datetime(visit_date/1000, 'unixepoch')) > datetime('now', 'start of day','-6 days') and " +
                    BASE_ENTITY_ID + " IN(select \"for\" from task where priority = '2' and " +
                    "date(datetime(start/1000, 'unixepoch')) > datetime('now', 'start of day', '-6 days') and " +
                    "status IN ('COMPLETED', 'IN_PROGRESS')) group by base_entity_id, visit_date;";
            cursor = repository.getReadableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            return Integer.toString(cursor.getCount());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "0";
    }

    public interface WeeklySummaryCallback {
        void onComplete(java.lang.String result);
    }
}
