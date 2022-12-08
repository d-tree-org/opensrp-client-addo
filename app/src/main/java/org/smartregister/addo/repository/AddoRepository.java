package org.smartregister.addo.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.util.AddoRepositoryUtils;
import org.smartregister.chw.anc.repository.VisitDetailsRepository;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.reporting.repository.DailyIndicatorCountRepository;
import org.smartregister.reporting.repository.IndicatorQueryRepository;
import org.smartregister.reporting.repository.IndicatorRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.repository.UniqueIdRepository;

import timber.log.Timber;

public class AddoRepository extends Repository {


    private static final String TAG = AddoRepository.class.getCanonicalName();
    protected SQLiteDatabase readableDatabase;
    protected SQLiteDatabase writableDatabase;
    private Context context;

    public AddoRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), AddoApplication.createCommonFtsObject(), openSRPContext.sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        EventClientRepository.createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());
        ConfigurableViewsRepository.createTable(database);
        //HomeVisitRepository.createTable(database); Not needed for Addo
        //HomeVisitServiceRepository.createTable(database); Not needed for Addo

        UniqueIdRepository.createTable(database);
        SettingsRepository.onUpgrade(database);

        TaskRepository.createTable(database);

        IndicatorRepository.createTable(database);
        IndicatorQueryRepository.createTable(database);
        DailyIndicatorCountRepository.createTable(database);

        VisitRepository.createTable(database);
        VisitDetailsRepository.createTable(database);
        onUpgrade(database, 1, BuildConfig.DATABASE_VERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w(AddoRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        int upgradeTo = oldVersion +1;

        while (upgradeTo <= newVersion ) {
            // implementation for database upgrades

            if (upgradeTo == 11) {
                upgradeToVersion11(context, db);
            }
            upgradeTo++;

        }


    }

    private void upgradeToVersion11(Context context, SQLiteDatabase db) {
        try {

            db.execSQL(AddoRepositoryUtils.UPGRADE_V2);

        } catch (Exception e) {
            Timber.e(e, "Upgrade to version 2");
        }
    }


    @Override
    public SQLiteDatabase getReadableDatabase() {
        String pass = AddoApplication.getInstance().getPassword().toString();
        if (!pass.isEmpty()) {
            return getReadableDatabase(pass);
        } else {
            throw new IllegalStateException("Password is blank");
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        String pass = AddoApplication.getInstance().getPassword().toString();
        if (!pass.isEmpty()) {
            return getWritableDatabase(pass);
        } else {
            throw new IllegalStateException("Password is blank");
        }
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Timber.e("Database Error. " + e.getMessage());
            return null;
        }

    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }

    private boolean checkIfAppUpdated() {
        return false;
    }

}
