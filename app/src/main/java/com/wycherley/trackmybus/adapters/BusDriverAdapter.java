package com.wycherley.trackmybus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.models.BusDriver;
import java.util.ArrayList;
import java.util.List;

public class BusDriverAdapter extends RecyclerView.Adapter<BusDriverAdapter.ViewHolder> {

    private List<BusDriver> busDrivers;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BusDriver busDriver);
    }

    public BusDriverAdapter() {
        this.busDrivers = new ArrayList<>();
    }

    public void setBusDrivers(List<BusDriver> busDrivers) {
        this.busDrivers = busDrivers;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bus_driver, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusDriver driver = busDrivers.get(position);
        holder.bind(driver, listener);
    }

    @Override
    public int getItemCount() {
        return busDrivers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBusNumber, tvDriverName, tvFromLocation, tvToLocation;
        ImageView ivDriverProfile;

        ViewHolder(View itemView) {
            super(itemView);
            tvBusNumber = itemView.findViewById(R.id.tvBusNumber);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            tvFromLocation = itemView.findViewById(R.id.tvFromLocation);
            tvToLocation = itemView.findViewById(R.id.tvToLocation);
            ivDriverProfile = itemView.findViewById(R.id.ivDriverProfile);
        }

        void bind(BusDriver driver, OnItemClickListener listener) {
            tvBusNumber.setText(driver.getBusNumber());
            tvDriverName.setText(driver.getDriverName());
            tvFromLocation.setText(driver.getFromLocation());
            tvToLocation.setText(driver.getToLocation());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(driver);
                }
            });
        }
    }
}