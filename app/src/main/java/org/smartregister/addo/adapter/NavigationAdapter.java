package org.smartregister.addo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.addo.R;
import org.smartregister.addo.listeners.NavigationListener;
import org.smartregister.addo.model.NavigationOption;
import org.smartregister.addo.util.Constants;

import java.util.List;
import java.util.Locale;

;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.MyViewHolder> {

    private List<NavigationOption> navigationOptionList;
    private String selectedView = Constants.DrawerMenu.ALL_FAMILIES;
    private View.OnClickListener onClickListener;
    private Context context;

    public NavigationAdapter(List<NavigationOption> navigationOptionList, Activity context) {
        this.navigationOptionList = navigationOptionList;
        this.context = context;
        this.onClickListener = new NavigationListener(context, this);
    }

    public String getSelectedView(){
        if (selectedView != null || selectedView.equals(""))
            setSelectedView(Constants.DrawerMenu.ALL_FAMILIES);
            return selectedView;
    }

    public void setSelectedView(String selectedView) {
        this.selectedView = selectedView;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.navigation_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        NavigationOption model = navigationOptionList.get(position);
        holder.tvName.setText(context.getResources().getText(model.getTitleID()));
        holder.tvCount.setText(String.format(Locale.getDefault(), "%d", model.getRegisterCount()));
        holder.tvCount.setVisibility(View.INVISIBLE);

        holder.ivIcon.setImageResource(model.getResourceID());
        holder.ivIcon.setColorFilter(context.getResources().getColor(R.color.pink_500));

        holder.getView().setTag(model.getMenuTitle());

        if (selectedView != null && selectedView.equals(model.getMenuTitle())) {
            holder.tvCount.setTextColor(context.getResources().getColor(R.color.light_blue_900));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.light_blue_900));
            holder.ivIcon.setColorFilter(context.getResources().getColor(R.color.light_blue_900));
        } else {
            holder.tvCount.setTextColor(context.getResources().getColor(R.color.dark_grey_text));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.dark_grey_text));
            holder.ivIcon.setColorFilter(context.getResources().getColor(R.color.dark_grey_text));
        }

    }

    @Override
    public int getItemCount() {
        return navigationOptionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvCount;
        public ImageView ivIcon;

        private View myView;

        public MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvCount = view.findViewById(R.id.tvCount);
            ivIcon = view.findViewById(R.id.ivIcon);

            if (onClickListener != null) {
                view.setOnClickListener(onClickListener);
            }

            myView = view;
        }

        public View getView() {
            return myView;
        }
    }
}
