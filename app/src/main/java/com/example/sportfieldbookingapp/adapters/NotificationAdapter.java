// ================================================
// NOTIFICATION ADAPTER
// File: app/src/main/java/com/example/sportfieldbookingapp/adapters/NotificationAdapter.java
// ================================================

package com.example.sportfieldbookingapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.models.Notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<Notification> notifications;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Notification notification);
    }

    public NotificationAdapter(Context context, List<Notification> notifications, OnItemClickListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        // Set title và message
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());

        // Set time (relative time)
        holder.tvTime.setText(getRelativeTime(notification.getCreatedAt()));

        // Set icon dựa vào type
        holder.ivIcon.setImageResource(getIconForType(notification.getType()));

        // Hiển thị unread indicator
        if (notification.isRead()) {
            holder.viewUnreadIndicator.setVisibility(View.GONE);
            holder.tvTitle.setTypeface(null, Typeface.NORMAL);
            holder.tvMessage.setTypeface(null, Typeface.NORMAL);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
        } else {
            holder.viewUnreadIndicator.setVisibility(View.VISIBLE);
            holder.tvTitle.setTypeface(null, Typeface.BOLD);
            holder.tvMessage.setTypeface(null, Typeface.NORMAL);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.unread_notification_bg));
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private int getIconForType(String type) {
        switch (type) {
            case "teammate_join":
                return R.drawable.ic_groups; // Icon người tham gia
            case "booking":
                return R.drawable.ic_bookings; // Icon đặt sân
            case "chat":
                return R.drawable.ic_chat; // Icon chat
            case "promotion":
                return R.drawable.ic_discount; // Icon khuyến mãi
            case "system":
            default:
                return R.drawable.ic_notifications; // Icon thông báo chung
        }
    }

    private String getRelativeTime(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timestamp);
            if (date != null) {
                long timeInMillis = date.getTime();
                CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                        timeInMillis,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE
                );
                return relativeTime.toString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvMessage;
        TextView tvTime;
        View viewUnreadIndicator;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            viewUnreadIndicator = itemView.findViewById(R.id.viewUnreadIndicator);
        }
    }
}