package org.smartregister.addo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.addo.R;
import org.smartregister.addo.activity.AddoHomeActivity;
import org.smartregister.view.LocationPickerView;

import java.util.List;

public class AddoLocationRecyclerViewProviderAdapter extends RecyclerView.Adapter<AddoLocationRecyclerViewProviderAdapter.MyViewHolder> {

    private List<String> addoLocation;
    private View.OnClickListener onClickListener;
    private Context context;

    public AddoLocationRecyclerViewProviderAdapter(List<String> addoLocation,  Activity context) {
        this.addoLocation = addoLocation;
        this.context = context;
        this.onClickListener = new AddoLocationAdapterListener(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.addo_villages_list_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (!addoLocation.isEmpty()) {
            String item = this.addoLocation.get(position);
            holder.tvVillageName.setText(item);
            View location = holder.myView;
            if (onClickListener != null) {
                attachLocationOnclickListener(location, item);
            }
        }
    }

    private void attachLocationOnclickListener(View view, String locationItem) {
        view.setOnClickListener(onClickListener);
        view.setTag(locationItem);
    }

    @Override
    public int getItemCount() {
        return this.addoLocation.size();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// Inner Classes /////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LocationPickerView tvVillageName;
        private View myView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVillageName = itemView.findViewById(R.id.village_name);

            myView = itemView;
        }

        public  View getView() {
            return myView;
        }
    }

    public class AddoLocationAdapterListener implements View.OnClickListener {

        private Activity activity;

        public AddoLocationAdapterListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View v) {
            // Here is where we start the activity with fingerprint search functionality
            // Also pass the location selected
            startRegisterActivity(AddoHomeActivity.class);
        }

        private void startRegisterActivity(Class registerClass) {
            Intent intent = new Intent(activity, registerClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            activity.finish();
        }
    }
}
