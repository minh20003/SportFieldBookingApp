package com.example.sportfieldbookingapp.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.models.ChatMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private final Context context;
    private List<ChatMessage> messages;
    private final MessageActionListener actionListener;

    public interface MessageActionListener {
        void onRetryClick(ChatMessage message);
        void onDeleteForMeClick(ChatMessage message);
        void onRecallClick(ChatMessage message);
        void onCopyClick(ChatMessage message);
        void onMessageVisible(ChatMessage message);
    }

    public ChatMessageAdapter(Context context, List<ChatMessage> messages, MessageActionListener listener) {
        this.context = context;
        this.messages = messages != null ? messages : new ArrayList<>();
        this.actionListener = listener;
        setHasStableIds(true); // Giúp RecyclerView tối ưu hóa việc update
    }

    public ChatMessageAdapter(Context context, List<ChatMessage> messages) {
        this(context, messages, null);
    }

    @Override
    public long getItemId(int position) {
        ChatMessage msg = messages.get(position);
        // Dùng database ID nếu có, nếu không dùng hashCode của messageId
        if (msg.getId() > 0) {
            return msg.getId();
        }
        return msg.getMessageId() != null ? msg.getMessageId().hashCode() : position;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        return message.isMine() ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.item_chat_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.item_chat_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        boolean isLastMessage = position == messages.size() - 1;
        boolean showAvatar = shouldShowAvatar(position);

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message, isLastMessage);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message, showAvatar);
        }

        // Notify when message becomes visible (for seen status)
        if (actionListener != null && !message.isMine()) {
            actionListener.onMessageVisible(message);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            // Full bind
            onBindViewHolder(holder, position);
        } else {
            // Partial update - chỉ cập nhật phần thay đổi
            ChatMessage message = messages.get(position);
            for (Object payload : payloads) {
                if (payload instanceof android.os.Bundle) {
                    android.os.Bundle bundle = (android.os.Bundle) payload;
                    
                    if (bundle.containsKey("status") && holder instanceof SentMessageViewHolder) {
                        ((SentMessageViewHolder) holder).updateStatus(message);
                    }
                    if (bundle.containsKey("recalled")) {
                        if (holder instanceof SentMessageViewHolder) {
                            ((SentMessageViewHolder) holder).updateRecalled(message);
                        } else if (holder instanceof ReceivedMessageViewHolder) {
                            ((ReceivedMessageViewHolder) holder).updateRecalled(message);
                        }
                    }
                }
            }
        }
    }

    private boolean shouldShowAvatar(int position) {
        if (position == messages.size() - 1) return true;

        ChatMessage current = messages.get(position);
        ChatMessage next = messages.get(position + 1);

        return current.isMine() != next.isMine();
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public int getLastMessageId() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(messages.size() - 1).getId();
        }
        return -1;
    }

    // ==================== Update Methods ====================
    public void updateMessages(List<ChatMessage> newMessages) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MessageDiffCallback(this.messages, newMessages));
        this.messages = new ArrayList<>(newMessages);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addMessage(ChatMessage message) {
        if (message != null) {
            messages.add(message);
            notifyItemInserted(messages.size() - 1);
        }
    }

    public void updateMessage(ChatMessage message) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getMessageId() != null &&
                    messages.get(i).getMessageId().equals(message.getMessageId())) {
                messages.set(i, message);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void addMessagesAtStart(List<ChatMessage> newMessages) {
        if (newMessages != null && !newMessages.isEmpty()) {
            messages.addAll(0, newMessages);
            notifyItemRangeInserted(0, newMessages.size());
        }
    }

    // ==================== Helper Methods ====================
    private String getRelativeTime(String timestamp) {
        if (timestamp == null) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timestamp);

            if (date != null) {
                long timeInMillis = date.getTime();

                if (DateUtils.isToday(timeInMillis)) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    return timeFormat.format(date);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
                    return dateFormat.format(date);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("message", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Đã sao chép", Toast.LENGTH_SHORT).show();
    }

    // ==================== ViewHolders ====================
    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime, tvRetry;
        ProgressBar progressSending;
        ImageView ivStatusSending, ivStatusSent, ivStatusDelivered, ivStatusSeen, ivStatusFailed;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRetry = itemView.findViewById(R.id.tvRetry);
            progressSending = itemView.findViewById(R.id.progressSending);
            ivStatusSending = itemView.findViewById(R.id.ivStatusSending);
            ivStatusSent = itemView.findViewById(R.id.ivStatusSent);
            ivStatusDelivered = itemView.findViewById(R.id.ivStatusDelivered);
            ivStatusSeen = itemView.findViewById(R.id.ivStatusSeen);
            ivStatusFailed = itemView.findViewById(R.id.ivStatusFailed);
        }

        void bind(ChatMessage message, boolean isLastMessage) {
            // Kiểm tra tin nhắn đã thu hồi
            if (message.isRecalled()) {
                tvMessage.setText("Tin nhắn đã được thu hồi");
                tvMessage.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                tvMessage.setTypeface(null, android.graphics.Typeface.ITALIC);
                hideAllStatusIcons();
                tvTime.setText(getRelativeTime(message.getCreatedAt()));
                itemView.setOnLongClickListener(null);
                return;
            }

            tvMessage.setText(message.getMessage());
            tvMessage.setTextColor(context.getResources().getColor(android.R.color.white));
            tvMessage.setTypeface(null, android.graphics.Typeface.NORMAL);
            tvTime.setText(getRelativeTime(message.getCreatedAt()));

            // Reset all status icons
            hideAllStatusIcons();

            // Show appropriate status
            switch (message.getStatus()) {
                case ChatMessage.STATUS_SENDING:
                    if (progressSending != null) progressSending.setVisibility(View.VISIBLE);
                    if (ivStatusSending != null) ivStatusSending.setVisibility(View.VISIBLE);
                    break;
                case ChatMessage.STATUS_SENT:
                    if (ivStatusSent != null) ivStatusSent.setVisibility(View.VISIBLE);
                    break;
                case ChatMessage.STATUS_DELIVERED:
                    if (ivStatusDelivered != null) ivStatusDelivered.setVisibility(View.VISIBLE);
                    break;
                case ChatMessage.STATUS_SEEN:
                    // Only show seen for last message
                    if (isLastMessage && ivStatusSeen != null) {
                        ivStatusSeen.setVisibility(View.VISIBLE);
                    } else if (ivStatusDelivered != null) {
                        ivStatusDelivered.setVisibility(View.VISIBLE);
                    }
                    break;
                case ChatMessage.STATUS_FAILED:
                    if (ivStatusFailed != null) ivStatusFailed.setVisibility(View.VISIBLE);
                    if (tvRetry != null) tvRetry.setVisibility(View.VISIBLE);
                    break;
            }

            // Retry click
            if (tvRetry != null) {
                tvRetry.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onRetryClick(message);
                    }
                });
            }

            // Long press menu
            itemView.setOnLongClickListener(v -> {
                showMessageMenu(v, message);
                return true;
            });
        }

        private void hideAllStatusIcons() {
            if (progressSending != null) progressSending.setVisibility(View.GONE);
            if (ivStatusSending != null) ivStatusSending.setVisibility(View.GONE);
            if (ivStatusSent != null) ivStatusSent.setVisibility(View.GONE);
            if (ivStatusDelivered != null) ivStatusDelivered.setVisibility(View.GONE);
            if (ivStatusSeen != null) ivStatusSeen.setVisibility(View.GONE);
            if (ivStatusFailed != null) ivStatusFailed.setVisibility(View.GONE);
            if (tvRetry != null) tvRetry.setVisibility(View.GONE);
        }

        private void showMessageMenu(View anchor, ChatMessage message) {
            PopupMenu popup = new PopupMenu(context, anchor);
            popup.getMenu().add(0, 1, 0, "Sao chép");
            popup.getMenu().add(0, 2, 1, "Thu hồi"); // Thu hồi với tất cả
            popup.getMenu().add(0, 3, 2, "Xóa phía tôi");

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 1:
                        if (actionListener != null) {
                            actionListener.onCopyClick(message);
                        } else {
                            copyToClipboard(message.getMessage());
                        }
                        return true;
                    case 2:
                        if (actionListener != null) {
                            actionListener.onRecallClick(message);
                        }
                        return true;
                    case 3:
                        if (actionListener != null) {
                            actionListener.onDeleteForMeClick(message);
                        }
                        return true;
                }
                return false;
            });

            popup.show();
        }

        // Partial update methods
        void updateStatus(ChatMessage message) {
            hideAllStatusIcons();
            switch (message.getStatus()) {
                case ChatMessage.STATUS_SENDING:
                    if (progressSending != null) progressSending.setVisibility(View.VISIBLE);
                    if (ivStatusSending != null) ivStatusSending.setVisibility(View.VISIBLE);
                    break;
                case ChatMessage.STATUS_SENT:
                    if (ivStatusSent != null) ivStatusSent.setVisibility(View.VISIBLE);
                    break;
                case ChatMessage.STATUS_DELIVERED:
                    if (ivStatusDelivered != null) ivStatusDelivered.setVisibility(View.VISIBLE);
                    break;
                case ChatMessage.STATUS_SEEN:
                    if (ivStatusSeen != null) ivStatusSeen.setVisibility(View.VISIBLE);
                    break;
                case ChatMessage.STATUS_FAILED:
                    if (ivStatusFailed != null) ivStatusFailed.setVisibility(View.VISIBLE);
                    if (tvRetry != null) tvRetry.setVisibility(View.VISIBLE);
                    break;
            }
        }

        void updateRecalled(ChatMessage message) {
            if (message.isRecalled()) {
                tvMessage.setText("Tin nhắn đã được thu hồi");
                tvMessage.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                tvMessage.setTypeface(null, android.graphics.Typeface.ITALIC);
                hideAllStatusIcons();
            }
        }
    }

    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime, tvSenderName;
        ImageView ivAvatar;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }

        void bind(ChatMessage message, boolean showAvatar) {
            // Kiểm tra tin nhắn đã thu hồi
            if (message.isRecalled()) {
                tvMessage.setText("Tin nhắn đã được thu hồi");
                tvMessage.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                tvMessage.setTypeface(null, android.graphics.Typeface.ITALIC);
            } else {
                tvMessage.setText(message.getMessage());
                tvMessage.setTextColor(context.getResources().getColor(R.color.text_primary));
                tvMessage.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
            
            tvTime.setText(getRelativeTime(message.getCreatedAt()));

            if (tvSenderName != null) {
                tvSenderName.setVisibility(View.GONE);
            }

            if (ivAvatar != null) {
                ivAvatar.setVisibility(showAvatar ? View.VISIBLE : View.INVISIBLE);
            }

            // Long press menu - chỉ hiện nếu tin nhắn chưa thu hồi
            if (!message.isRecalled()) {
                itemView.setOnLongClickListener(v -> {
                    showMessageMenu(v, message);
                    return true;
                });
            } else {
                itemView.setOnLongClickListener(null);
            }
        }

        private void showMessageMenu(View anchor, ChatMessage message) {
            PopupMenu popup = new PopupMenu(context, anchor);
            popup.getMenu().add(0, 1, 0, "Sao chép");
            popup.getMenu().add(0, 2, 1, "Xóa phía tôi");

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 1:
                        if (actionListener != null) {
                            actionListener.onCopyClick(message);
                        } else {
                            copyToClipboard(message.getMessage());
                        }
                        return true;
                    case 2:
                        if (actionListener != null) {
                            actionListener.onDeleteForMeClick(message);
                        }
                        return true;
                }
                return false;
            });

            popup.show();
        }

        // Partial update method
        void updateRecalled(ChatMessage message) {
            if (message.isRecalled()) {
                tvMessage.setText("Tin nhắn đã được thu hồi");
                tvMessage.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                tvMessage.setTypeface(null, android.graphics.Typeface.ITALIC);
            }
        }
    }

    // ==================== DiffUtil ====================
    private static class MessageDiffCallback extends DiffUtil.Callback {
        private final List<ChatMessage> oldList;
        private final List<ChatMessage> newList;

        MessageDiffCallback(List<ChatMessage> oldList, List<ChatMessage> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList != null ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newList != null ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            ChatMessage oldMsg = oldList.get(oldItemPosition);
            ChatMessage newMsg = newList.get(newItemPosition);

            // So sánh bằng database ID trước
            if (oldMsg.getId() > 0 && newMsg.getId() > 0) {
                return oldMsg.getId() == newMsg.getId();
            }
            
            // Fallback to messageId (cho optimistic messages)
            if (oldMsg.getMessageId() != null && newMsg.getMessageId() != null) {
                return oldMsg.getMessageId().equals(newMsg.getMessageId());
            }
            
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            ChatMessage oldMsg = oldList.get(oldItemPosition);
            ChatMessage newMsg = newList.get(newItemPosition);

            // So sánh các thuộc tính có thể thay đổi
            if (oldMsg.getStatus() != newMsg.getStatus()) return false;
            if (oldMsg.isRecalled() != newMsg.isRecalled()) return false;
            
            // Message content
            if (oldMsg.getMessage() == null && newMsg.getMessage() != null) return false;
            if (oldMsg.getMessage() != null && !oldMsg.getMessage().equals(newMsg.getMessage())) return false;

            return true;
        }

        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            ChatMessage oldMsg = oldList.get(oldItemPosition);
            ChatMessage newMsg = newList.get(newItemPosition);

            android.os.Bundle diffBundle = new android.os.Bundle();

            if (oldMsg.getStatus() != newMsg.getStatus()) {
                diffBundle.putInt("status", newMsg.getStatus());
            }
            if (oldMsg.isRecalled() != newMsg.isRecalled()) {
                diffBundle.putBoolean("recalled", newMsg.isRecalled());
            }

            // Return payload để chỉ cập nhật phần thay đổi, không rebind toàn bộ
            return diffBundle.size() > 0 ? diffBundle : null;
        }
    }
}
