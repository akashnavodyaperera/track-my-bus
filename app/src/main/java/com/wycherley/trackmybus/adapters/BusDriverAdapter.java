package com.wycherley.trackmybus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private OnItemClickListener itemClickListener;
    private OnSetAsMyBusListener setAsMyBusListener;
    private String selectedBusId; // Track which bus is selected

    public interface OnItemClickListener {
        void onItemClick(BusDriver busDriver);
    }

    public interface OnSetAsMyBusListener {
        void onSetAsMyBus(BusDriver busDriver);
    }

    public BusDriverAdapter() {
        this.busDrivers = new ArrayList<>();
    }

    public void setBusDrivers(List<BusDriver> busDrivers) {
        this.busDrivers = busDrivers;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnSetAsMyBusListener(OnSetAsMyBusListener listener) {
        this.setAsMyBusListener = listener;
    }

    public void setSelectedBusId(String busId) {
        this.selectedBusId = busId;
        notifyDataSetChanged();
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
        boolean isSelected = selectedBusId != null && selectedBusId.equals(driver.getId());
        holder.bind(driver, isSelected, itemClickListener, setAsMyBusListener);
    }

    @Override
    public int getItemCount() {
        return busDrivers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBusNumber, tvDriverName, tvFromLocation, tvToLocation;
        ImageView ivDriverProfile;
        Button btnSetAsMyBus;

        ViewHolder(View itemView) {
            super(itemView);
            tvBusNumber = itemView.findViewById(R.id.tvBusNumber);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            tvFromLocation = itemView.findViewById(R.id.tvFromLocation);
            tvToLocation = itemView.findViewById(R.id.tvToLocation);
            ivDriverProfile = itemView.findViewById(R.id.ivDriverProfile);
            btnSetAsMyBus = itemView.findViewById(R.id.btnSetAsMyBus);
        }

        void bind(BusDriver driver, boolean isSelected,
                  OnItemClickListener clickListener, OnSetAsMyBusListener busListener) {
            tvBusNumber.setText(driver.getBusNumber());
            tvDriverName.setText(driver.getDriverName());
            tvFromLocation.setText(driver.getFromLocation());
            tvToLocation.setText(driver.getToLocation());

            // Update button based on selection state
            if (isSelected) {
                btnSetAsMyBus.setText("My Bus ✓");
                btnSetAsMyBus.setEnabled(false);
                btnSetAsMyBus.setAlpha(0.6f);
            } else {
                btnSetAsMyBus.setText("Set as my bus");
                btnSetAsMyBus.setEnabled(true);
                btnSetAsMyBus.setAlpha(1.0f);
            }

            // Set as my bus button
            btnSetAsMyBus.setOnClickListener(v -> {
                if (busListener != null && !isSelected) {
                    busListener.onSetAsMyBus(driver);
                }
            });

            // Card click
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(driver);
                }
            });
        }
    }
}