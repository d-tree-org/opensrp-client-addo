package org.smartregister.addo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.smartregister.addo.R;
import org.smartregister.addo.model.FingerPrintScanResultModel;

import java.util.ArrayList;

public class FingerPrintScanAdapter extends RecyclerView.Adapter<FingerPrintScanAdapter.ViewHolder> {


    private ArrayList<FingerPrintScanResultModel> results;
    private Context myContext;

    ClientClicked activity;

    public interface ClientClicked {

        // Here we pass the case_id so as to build the family profile
        void onClientClicked(String index);
    }

    public FingerPrintScanAdapter(Context context, ArrayList<FingerPrintScanResultModel> clients) {
        results = clients;
        myContext = context;
        activity = (ClientClicked) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvClientName, tvGender, tvVillageName;
        ImageView imageview_profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvVillageName = itemView.findViewById(R.id.tvVillageName);
            imageview_profile = itemView.findViewById(R.id.imageview_profile);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //activity.onClientClicked(results.indexOf((FingerPrintScanResultModel) v.getTag()));
                    activity.onClientClicked(results.get(results.indexOf((FingerPrintScanResultModel) v.getTag())).getVillage());
                }
            });
        }
    }

    @NonNull
    @Override
    public FingerPrintScanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fingerprint_result_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FingerPrintScanAdapter.ViewHolder viewHolder, int i) {
        if (results == null) {
            Toast.makeText(myContext,"The fingerprint does not belong to any client.", Toast.LENGTH_SHORT).show();
        } else {
            viewHolder.itemView.setTag(results.get(i));
            viewHolder.tvClientName.setText(results.get(i).getName());
            viewHolder.tvGender.setText(results.get(i).getGender());
            viewHolder.tvVillageName.setText(results.get(i).getVillage());
            viewHolder.imageview_profile.setImageResource(R.drawable.ic_person_black_24dp);

        }

    }

    @Override
    public int getItemCount() {
        return results.size();
    }
}
