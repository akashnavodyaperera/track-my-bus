package com.wycherley.trackmybus.ui.parent;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.adapters.NotificationAdapter;
import com.wycherley.trackmybus.models.Notification;
import com.wycherley.trackmybus.repositories.NotificationRepository;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;
    private TextView tvClearAll;

    private NotificationAdapter notificationAdapter;
    private NotificationRepository notificationRepository;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Get current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        notificationRepository = NotificationRepository.getInstance();

        initViews();
        setupRecyclerView();
        setupListeners();
        loadNotifications();
    }

    private void initViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        progressBar = findViewById(R.id.progressBar);
        tvClearAll = findViewById(R.id.tvClearAll);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter();
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(notificationAdapter);
    }

    private void setupListeners() {
        // Notification item click
        notificationAdapter.setOnNotificationClickListener(notification -> {
            // Mark as read
            if (!notification.isRead()) {
                notificationRepository.markAsRead(notification.getId(),
                        new NotificationRepository.OnNotificationActionListener() {
                            @Override
                            public void onSuccess() {
                                // Reload to update UI
                                loadNotifications();
                            }

                            @Override
                            public void onError(String error) {
                                // Silent fail
                            }
                        });
            }
        });

        // Delete single notification
        notificationAdapter.setOnDeleteClickListener(notification -> {
            showDeleteDialog(notification);
        });

        // Clear all notifications
        tvClearAll.setOnClickListener(v -> {
            showClearAllDialog();
        });
    }

    private void loadNotifications() {
        showLoading(true);

        notificationRepository.getUserNotifications(currentUserId,
                new NotificationRepository.OnNotificationsLoadListener() {
                    @Override
                    public void onNotificationsLoaded(List<Notification> notifications) {
                        showLoading(false);

                        if (notifications.isEmpty()) {
                            showEmptyState();
                        } else {
                            showNotifications(notifications);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        showLoading(false);
                        Toast.makeText(NotificationsActivity.this,
                                "Error: " + error, Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }
                });
    }

    private void showDeleteDialog(Notification notification) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Notification")
                .setMessage("Are you sure you want to delete this notification?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteNotification(notification);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteNotification(Notification notification) {
        notificationRepository.deleteNotification(notification.getId(),
                new NotificationRepository.OnNotificationActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(NotificationsActivity.this,
                                "Notification deleted", Toast.LENGTH_SHORT).show();
                        loadNotifications();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(NotificationsActivity.this,
                                "Error deleting notification", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showClearAllDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Clear All Notifications")
                .setMessage("Are you sure you want to delete all notifications?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    clearAllNotifications();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearAllNotifications() {
        notificationRepository.clearAllNotifications(currentUserId,
                new NotificationRepository.OnNotificationActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(NotificationsActivity.this,
                                "All notifications cleared", Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(NotificationsActivity.this,
                                "Error clearing notifications", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvNotifications.setVisibility(show ? View.GONE : View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        rvNotifications.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        tvClearAll.setVisibility(View.GONE);
    }

    private void showNotifications(List<Notification> notifications) {
        progressBar.setVisibility(View.GONE);
        rvNotifications.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        tvClearAll.setVisibility(View.VISIBLE);

        notificationAdapter.setNotifications(notifications);
    }
}