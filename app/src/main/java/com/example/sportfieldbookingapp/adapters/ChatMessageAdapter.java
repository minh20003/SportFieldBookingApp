package com.example.sportfieldbookingapp.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.models.ChatMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter cho RecyclerView hiển thị danh sách tin nhắn trong chat
 * Hỗ trợ 2 loại view: tin nhắn gửi đi và tin nhắn nhận được
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;      // Tin nhắn của mình
    private static final int VIEW_TYPE_RECEIVED = 2;  // Tin nhắn nhận được

    private Context context;
    private List<ChatMessage> messages;

    public ChatMessageAdapter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        // Backend đã set field isMine() = true nếu là tin nhắn của mình
        return message.isMine() ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            // Layout cho tin nhắn gửi đi (bên phải, màu xanh)
            View view = LayoutInflater.from(context).inflate(
                    R.layout.item_chat_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            // Layout cho tin nhắn nhận được (bên trái, màu xám)
            View view = LayoutInflater.from(context).inflate(
                    R.layout.item_chat_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    /**
     * Chuyển đổi timestamp thành thời gian hiển thị
     * - Nếu hôm nay: hiển thị giờ (VD: 10:30)
     * - Nếu không: hiển thị ngày + giờ (VD: 15/11 10:30)
     */
    private String getRelativeTime(String timestamp) {
        try {
            // Format từ backend: "yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timestamp);

            if (date != null) {
                long timeInMillis = date.getTime();

                // Kiểm tra nếu là hôm nay
                if (DateUtils.isToday(timeInMillis)) {
                    // Chỉ hiển thị giờ: 10:30
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    return timeFormat.format(date);
                } else {
                    // Hiển thị ngày + giờ: 15/11 10:30
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
                    return dateFormat.format(date);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Nếu lỗi parse, trả về chuỗi gốc
        return timestamp;
    }

    /**
     * ViewHolder cho tin nhắn GỬI ĐI (của mình)
     * Hiển thị bên phải màn hình, nền xanh
     */
    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvTime;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(ChatMessage message) {
            // Set nội dung tin nhắn
            tvMessage.setText(message.getMessage());

            // Set thời gian
            tvTime.setText(getRelativeTime(message.getCreatedAt()));
        }
    }

    /**
     * ViewHolder cho tin nhắn NHẬN ĐƯỢC (từ người khác)
     * Hiển thị bên trái màn hình, nền xám
     */
    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvTime;
        TextView tvSenderName;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
        }

        void bind(ChatMessage message) {
            // Set nội dung tin nhắn
            tvMessage.setText(message.getMessage());

            // Set thời gian
            tvTime.setText(getRelativeTime(message.getCreatedAt()));

            // Set tên người gửi (nếu có)
            if (message.getSenderName() != null && !message.getSenderName().isEmpty()) {
                tvSenderName.setText(message.getSenderName());
                tvSenderName.setVisibility(View.VISIBLE);
            } else {
                tvSenderName.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Cập nhật danh sách tin nhắn và refresh UI
     */
    public void updateMessages(List<ChatMessage> newMessages) {
        if (newMessages != null) {
            this.messages = newMessages;
            notifyDataSetChanged();
        }
    }

    /**
     * Thêm tin nhắn mới vào cuối danh sách
     */
    public void addMessage(ChatMessage message) {
        if (message != null && messages != null) {
            messages.add(message);
            notifyItemInserted(messages.size() - 1);
        }
    }

    /**
     * Xóa tất cả tin nhắn
     */
    public void clearMessages() {
        if (messages != null) {
            messages.clear();
            notifyDataSetChanged();
        }
    }
}