package org.smartregister.addo.repository;

import android.content.ContentValues;
import android.os.Build;

import androidx.annotation.RequiresApi;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.chw.pnc.util.Constants;
import org.smartregister.repository.BaseRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.JulianFields;
import java.time.temporal.TemporalField;
import java.util.Date;

import timber.log.Timber;

public class AdolescentCloseRepository extends BaseRepository {

    public void graduateAdolescent(int cutOffAge) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("is_closed", 1);
        contentValues.put("last_interacted_with", new Date().getTime());

        String where = " cast(julianday(datetime('now')) - julianday(datetime(substr(dob, 1,4) " +
                " || '-' || substr(dob, 6,2) || '-' || substr(dob, 9,2))) as integer) >= ? ";

        String[] whereArgs = new String[]{String.valueOf(getYearsInDays(cutOffAge))};
        database.update("ec_adolescent", contentValues, where, whereArgs);
    }


    private int getYearsInDays(int numberOfYears) {
        int twentyYearsInDays = 365 * numberOfYears; // default it to 365 * 20
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            LocalDateTime dateTimeNow = LocalDateTime.now();
            LocalDateTime dateTimeYearsBack = dateTimeNow.minusYears(numberOfYears);

            try {
                twentyYearsInDays = (int) (dateTimeNow.getLong(JulianFields.JULIAN_DAY) - dateTimeYearsBack.getLong(JulianFields.JULIAN_DAY));
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return twentyYearsInDays;
    }
}
