package org.smartregister.addo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.opensrp.api.domain.BaseEntity;
import org.smartregister.addo.R;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;

import java.util.List;

public class FamilyMemberAdapter extends ArrayAdapter<BaseEntity> {

    public FamilyMemberAdapter(Context context, List<BaseEntity> members) {
        super(context, 0, members);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BaseEntity member = getItem(position);
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
