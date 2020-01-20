package org.smartregister.addo.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.addo.R;
import org.smartregister.addo.domain.Entity;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;

import java.util.List;
import java.util.Map;

public class FamilyMemberAdapter extends ArrayAdapter<Entity> {

    public FamilyMemberAdapter(Context context, List<Entity> members) {
        super(context, 0, members);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Entity member = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.family_member_register_list_row, parent, false);
        }

        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.patient_name_age);
        TextView tvGender = convertView.findViewById(R.id.gender);
        ImageView profile = convertView.findViewById(org.smartregister.family.R.id.profile);
        // Populate the data into the template view using the data object

        String fullName = String.format("%s %s %s", isNull(member.getFirstName()), isNull(member.getMiddleName()), isNull(member.getLastName()));
        tvName.setText(fullName);

        tvGender.setText(member.getGender());
        new ImageRenderHelper(getContext()).refreshProfileImage("8e3738ba-c510-44ba-92d2-49e3938d2415", profile,
                Utils.getMemberProfileImageResourceIDentifier(""));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> res  = member.getRelationships();
                List<String> families = (List<String>) res.get("family");
                if (families.size()>0) {
                    String familyId = families.get(0);
                    Log.d("Family", familyId);
                    CommonPersonObject patient = org.smartregister.family.util.Utils.context().commonrepository(Utils.metadata().familyRegister.tableName)
                            .findByCaseID(familyId);
                    Intent intent = new Intent(getContext(), org.smartregister.family.util.Utils.metadata().profileActivity);
                    intent.putExtra("family_base_entity_id", patient.getCaseId());
                    intent.putExtra("family_head",
                            org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "family_head", false));
                    intent.putExtra("primary_caregiver",
                            org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "primary_caregiver", false));
                    intent.putExtra("village_town",
                            org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "village_town", false));
                    intent.putExtra("family_name",
                            org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "first_name", false));
                    intent.putExtra("go_to_due_page", false);
                    getContext().startActivity(intent);
                }

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    private String isNull(String string) {
        if (string == null) {
            return "";
        } else {
            return string.trim();
        }
    }
}
