package com.wycherley.trackmybus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.models.BusDriver;
import java.util.ArrayList;
import java.util.List;

public class AdminDriverAdapter extends RecyclerView.Adapter<AdminDriverAdapter.ViewHolder> {

    private List<BusDriver> drivers;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;

    public interface OnEditClickListener {
        void onEditClick(BusDriver driver);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(BusDriver driver);
    }

    public AdminDriverAdapter() {
        this.drivers = new ArrayList<>();
    }

    public void setDrivers(List<BusDriver> drivers) {
        this.drivers = drivers;
        notifyDataSetChanged();
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_driver, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusDriver driver = drivers.get(position);
        holder.bind(driver, editClickListener, deleteClickListener);
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBusNumber, tvDriverName, tvRoute, tvContact;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvBusNumber = itemView.findViewById(R.id.tvBusNumber);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvContact = itemView.findViewById(R.id.tvContact);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(BusDriver driver, OnEditClickListener editListener,
                  OnDeleteClickListener deleteListener) {
            tvBusNumber.setText(driver.getBusNumber());
            tvDriverName.setText(driver.getDriverName());
            tvRoute.setText(driver.getFromLocation() + " → " + driver.getToLocation());

            // Show contact info if available
            String contact = "";
            if (driver.getPhoneNumber() != null && !driver.getPhoneNumber().isEmpty()) {
                contact = driver.getPhoneNumber();
            }
            if (driver.getEmail() != null && !driver.getEmail().isEmpty()) {
                if (!contact.isEmpty()) contact += " • ";
                contact += driver.getEmail();
            }
            tvContact.setText(contact.isEmpty() ? "No contact info" : contact);
            tvContact.setVisibility(contact.isEmpty() ? View.GONE : View.VISIBLE);

            btnEdit.setOnClickListener(v -> {
                if (editListener != null) {
                    editListener.onEditClick(driver);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(driver);
                }
            });
        }
    }
}