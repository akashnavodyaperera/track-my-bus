package com.wycherley.trackmybus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.models.Notification;
import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notifications;
    private OnNotificationClickListener clickListener;
    private OnDeleteClickListener deleteListener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Notification notification);
    }

    public NotificationAdapter() {
        this.notifications = new ArrayList<>();
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification, clickListener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNotificationIcon;
        TextView tvMessage, tvDate, tvClear;
        View viewUnreadIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            ivNotificationIcon = itemView.findViewById(R.id.ivNotificationIcon);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvClear = itemView.findViewById(R.id.tvClear);
            viewUnreadIndicator = itemView.findViewById(R.id.viewUnreadIndicator);
        }

        void bind(Notification notification, OnNotificationClickListener clickListener,
                  OnDeleteClickListener deleteListener) {
            // Set message
            tvMessage.setText(notification.getMessage());

            // Set formatted date
            tvDate.setText(notification.getFormattedDate());

            // Show/hide unread indicator
            if (notification.isRead()) {
                viewUnreadIndicator.setVisibility(View.GONE);
                // Make text slightly faded for read notifications
                tvMessage.setAlpha(0.7f);
            } else {
                viewUnreadIndicator.setVisibility(View.VISIBLE);
                tvMessage.setAlpha(1.0f);
            }

            // Item click
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onNotificationClick(notification);
                }
            });

            // Clear/Delete click
            tvClear.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(notification);
                }
            });
        }
    }
}