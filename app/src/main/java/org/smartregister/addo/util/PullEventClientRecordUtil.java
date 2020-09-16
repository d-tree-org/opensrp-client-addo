package org.smartregister.addo.util;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.intent.SyncIntentService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.chw.anc.util.NCUtils.getClientProcessorForJava;

public class PullEventClientRecordUtil {

    public static void pullEventClientRecord(final String baseEntityId,
                                         final ProgressDialog progressDialog) {

        org.smartregister.util.Utils.startAsyncTask(new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                publishProgress();
                Response<String> response = pull(baseEntityId);
                if (response.isFailure()) {
                    return null;
                } else {
                    try {
                        return new JSONObject(response.payload());
                    } catch (Exception e) {
                        Log.e(getClass().getName(), "", e);
                        return null;
                    }
                }
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                progressDialog.dismiss();
            }
        }, null);
    }

    private static Response<String> pull(String baseEntityId) {
        if (baseEntityId == null || baseEntityId.isEmpty()) {
            return new Response<>(ResponseStatus.failure, "entityId doesn't exist");
        }

        Context context = CoreLibrary.getInstance().context();
        DristhiConfiguration configuration = context.configuration();

        String baseUrl = configuration.dristhiBaseURL();

        String paramString = "?baseEntityId=" + urlEncode(baseEntityId.trim()) + "&limit=1000";
        String uri = baseUrl + SyncIntentService.SYNC_URL + paramString;

        Timber.d(new StringBuilder(PullEventClientRecordUtil.class.getCanonicalName()).append(" ").append(uri).toString());

        Response<String> response = new Response<>(ResponseStatus.failure, "");
        try {
            response = context.getHttpAgent().fetch(uri);
            // TO Do: validate response

            JSONObject jsonObject = new JSONObject(response.payload());

            EventClientRepository eventClientRepository = new EventClientRepository();
            Event event = eventClientRepository.convert(jsonObject.getJSONArray("events").get(0).toString(), Event.class);
            Client client = eventClientRepository.convert(jsonObject.getJSONArray("clients").get(0).toString(), Client.class);

            List<EventClient> eventClientList = new ArrayList<>();
            eventClientList.add(new EventClient(event, client));

            JSONArray events = getOutOFCatchmentJsonArray(new JSONObject(response.payload()), "events");
            JSONArray clients = getOutOFCatchmentJsonArray(new JSONObject(response.payload()), "clients");

            AddoApplication.getInstance().getEcSyncHelper().batchSave(events, clients);
            getClientProcessorForJava().processClient(eventClientList);

        } catch (Exception e) {
            Log.d("Pull EventClient error", e.toString());
        }

        return response;
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    private static JSONArray getOutOFCatchmentJsonArray(JSONObject jsonObject, String clients) throws
            JSONException {
        return jsonObject.has(clients) ? jsonObject.getJSONArray(clients) : new JSONArray();
    }
}
