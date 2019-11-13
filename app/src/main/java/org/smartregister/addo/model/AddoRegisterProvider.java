package org.smartregister.addo.model;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.smartregister.addo.R;
import org.smartregister.addo.util.ChildDBConstants;
import org.smartregister.addo.util.Constants;
import org.smartregister.addo.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.provider.FamilyRegisterProvider;
import org.smartregister.family.util.DBConstants;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

public class AddoRegisterProvider extends FamilyRegisterProvider {

    private final Context context;
    private final View.OnClickListener onClickListener;

    public AddoRegisterProvider(Context context, CommonRepository commonRepository,
                                Set visibleColumns, View.OnClickListener onClickListener,
                                View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);

        if (viewHolder.memberIcon == null || !(viewHolder.memberIcon instanceof LinearLayout)) {
            return;
        }

        ((LinearLayout) viewHolder.memberIcon).removeAllViews();

        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        String familyBaseEntityId = pc.getCaseId();
        Utils.startAsyncTask(new UpdateAsyncTask(context, viewHolder, familyBaseEntityId), null);
    }

    private void updateChildIcons(RegisterViewHolder viewHolder, List<Map<String, String>> list, int ancWomanCount) {
        ImageView imageView;
        LinearLayout linearLayout;
        if (ancWomanCount > 0) {
            for (int i = 1; i <= ancWomanCount; i++) {
                imageView = new ImageView(context);
                imageView.setImageResource(R.mipmap.ic_anc_pink);
                linearLayout = (LinearLayout) viewHolder.memberIcon;
                linearLayout.addView(imageView);
            }
        }
        if (list != null && !list.isEmpty()) {
            for (Map<String, String> map : list) {
                imageView = new ImageView(context);
                String gender = map.get(DBConstants.KEY.GENDER);
                if ("Male".equalsIgnoreCase(gender)) {
                    imageView.setImageResource(R.mipmap.ic_boy_child);
                } else {
                    imageView.setImageResource(R.mipmap.ic_girl_child);
                }
                linearLayout = (LinearLayout) viewHolder.memberIcon;
                linearLayout.addView(imageView);
            }
        }

    }

    private List<Map<String, String>> getChildren(String familyEntityId) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(Constants.TABLE_NAME.CHILD, new String[]{DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.GENDER, ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE, ChildDBConstants.KEY.DATE_CREATED, DBConstants.KEY.DOB});
        queryBUilder.mainCondition(String.format(" %s is null AND %s = '%s' AND %s ",
                DBConstants.KEY.DATE_REMOVED,
                DBConstants.KEY.RELATIONAL_ID,
                familyEntityId,
                ChildDBConstants.childAgeLimitFilter()));

        String query = queryBUilder.orderbyCondition(DBConstants.KEY.DOB + " ASC ");

        CommonRepository commonRepository = org.smartregister.family.util.Utils.context().commonrepository(Constants.TABLE_NAME.CHILD);
        List<Map<String, String>> res = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = commonRepository.queryTable(query);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int columncount = cursor.getColumnCount();
                Map<String, String> columns = new HashMap<>();
                for (int i = 0; i < columncount; i++) {
                    columns.put(cursor.getColumnName(i), cursor.getType(i) == Cursor.FIELD_TYPE_NULL ? null : String.valueOf(cursor.getString(i)));
                }
                res.add(columns);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e, e.toString());
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return res;
    }

    public class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final RegisterViewHolder viewHolder;
        private final String familyBaseEntityId;

        private List<Map<String, String>> list;

        public UpdateAsyncTask(Context context, RegisterViewHolder viewHolder, String familyBaseEntityId) {
            this.context = context;
            this.viewHolder = viewHolder;
            this.familyBaseEntityId = familyBaseEntityId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
