package org.smartregister.addo.model;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import org.smartregister.addo.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.provider.FamilyRegisterProvider;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
