package com.wycherley.trackmybus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.models.TripHistory;
import java.util.ArrayList;
import java.util.List;

public class TripHistoryAdapter extends RecyclerView.Adapter<TripHistoryAdapter.ViewHolder> {
    private List<TripHistory> trips;
    private OnTripClickListener clickListener;

    public interface OnTripClickListener {
        void onTripClick(TripHistory trip);
    }

    public TripHistoryAdapter() {
        this.trips = new ArrayList<>();
    }

    public void setTrips(List<TripHistory> trips) {
        this.trips = trips;
        notifyDataSetChanged();
    }

    public void setOnTripClickListener(OnTripClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TripHistory trip = trips.get(position);
        holder.bind(trip, clickListener);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivTripIcon;
        TextView tvTripType, tvBusNumber, tvDriverName, tvTime, tvDate, tvLocation;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardTripHistory);
            ivTripIcon = itemView.findViewById(R.id.ivTripIcon);
            tvTripType = itemView.findViewById(R.id.tvTripType);
            tvBusNumber = itemView.findViewById(R.id.tvBusNumber);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }

        void bind(TripHistory trip, OnTripClickListener listener) {
            // Set trip type and icon
            tvTripType.setText(trip.getTripTypeDisplay());

            if (trip.isMorningTrip()) {
                ivTripIcon.setImageResource(android.R.drawable.ic_menu_day);
                cardView.setCardBackgroundColor(
                        itemView.getContext().getResources().getColor(android.R.color.holo_orange_light));
            } else {
                ivTripIcon.setImageResource(android.R.drawable.ic_menu_recent_history);
                cardView.setCardBackgroundColor(
                        itemView.getContext().getResources().getColor(android.R.color.holo_blue_light));
            }

            // Set trip details
            tvBusNumber.setText(trip.getBusNumber());
            tvDriverName.setText(trip.getDriverName());
            tvTime.setText(trip.getFormattedTime());
            tvDate.setText(trip.getFormattedDate());

            if (trip.getLocation() != null && !trip.getLocation().isEmpty()) {
                tvLocation.setText(trip.getLocation());
                tvLocation.setVisibility(View.VISIBLE);
            } else {
                tvLocation.setVisibility(View.GONE);
            }

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTripClick(trip);
                }
            });
        }
    }
}