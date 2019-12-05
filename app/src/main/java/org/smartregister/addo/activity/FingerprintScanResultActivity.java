package org.smartregister.addo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.smartregister.addo.R;
import org.smartregister.addo.adapter.FingerPrintScanAdapter;
import org.smartregister.addo.model.FingerPrintScanResultModel;
import org.smartregister.commonregistry.CommonPersonObject;

import java.util.ArrayList;

import static org.smartregister.addo.util.JsonFormUtils.getCommonRepository;

public class FingerprintScanResultActivity extends AppCompatActivity implements FingerPrintScanAdapter.ClientClicked {

    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<FingerPrintScanResultModel> clients;
    CommonPersonObject pc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_scan_result);

        recyclerView = findViewById(R.id.fp_result_recycle_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        Intent intent = getIntent();

        clients = intent.getParcelableArrayListExtra("clients");

        myAdapter = new FingerPrintScanAdapter( this, clients);

        recyclerView.setAdapter(myAdapter);

    }

    @Override
    public void onClientClicked(String index) {
        pc = getCommonRepository("ec_family").findByCaseID(index);
        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().profileActivity);
        intent.putExtra("family_base_entity_id", index);
       intent.putExtra("family_head",
                org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), "family_head", false));
        intent.putExtra("primary_caregiver",
                org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), "primary_caregiver", false));
        intent.putExtra("village_town",
                org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), "village_town", false));
        intent.putExtra("family_name",
                org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), "first_name", false));
        intent.putExtra("go_to_due_page", false);
        this.startActivity(intent);
    }
}
