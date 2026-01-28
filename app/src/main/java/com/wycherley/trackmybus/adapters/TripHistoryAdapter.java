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

