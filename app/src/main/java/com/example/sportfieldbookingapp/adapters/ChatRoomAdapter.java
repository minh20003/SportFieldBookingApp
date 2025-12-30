package com.example.sportfieldbookingapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.models.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {

    public interface OnChatRoomClickListener {
        void onChatRoomClick(ChatRoom chatRoom);
        void onChatRoomLongClick(ChatRoom chatRoom);
    }

    private final LayoutInflater inflater;
    private List<ChatRoom> chatRooms;
    private final OnChatRoomClickListener listener;
    private final Context context;

    public ChatRoomAdapter(Context context, List<ChatRoom> chatRooms, OnChatRoomClickListener listener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.chatRooms = chatRooms != null ? chatRooms : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_chat_room, parent, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        ChatRoom room = chatRooms.get(position);
        holder.bind(room, listener, context);
    }

    @Override
    public int getItemCount() {
        return chatRooms != null ? chatRooms.size() : 0;
    }

    public void updateRooms(List<ChatRoom> newRooms) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RoomDiffCallback(this.chatRooms, newRooms));
        this.chatRooms = new ArrayList<>(newRooms);
        diffResult.dispatchUpdatesTo(this);
    }

    static class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivAvatar;
        private final View viewOnlineIndicator;
        private final TextView tvOtherUserName;
        private final TextView tvLastMessage;
        private final TextView tvLastMessageTime;
        private final TextView tvUnreadBadge;

        ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            viewOnlineIndicator = itemView.findViewById(R.id.viewOnlineIndicator);
            tvOtherUserName = itemView.findViewById(R.id.tvOtherUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvLastMessageTime = itemView.findViewById(R.id.tvLastMessageTime);
            tvUnreadBadge = itemView.findViewById(R.id.tvUnreadBadge);
        }

        void bind(ChatRoom room, OnChatRoomClickListener listener, Context context) {
            // User name
            tvOtherUserName.setText(room.getOtherUserName() != null ? room.getOtherUserName() : "Người dùng");

            // Last message with typing indicator
            if (room.isTyping()) {
                tvLastMessage.setText("Đang nhập...");
                tvLastMessage.setTextColor(context.getResources().getColor(R.color.primary));
                tvLastMessage.setTypeface(null, Typeface.ITALIC);
            } else {
                String lastMessage = room.getLastMessage();
                if (lastMessage == null || lastMessage.trim().isEmpty()) {
                    lastMessage = "Chưa có tin nhắn";
                }
                tvLastMessage.setText(lastMessage);
                tvLastMessage.setTypeface(null, Typeface.NORMAL);

                // Style based on unread
                int unread = room.getUnreadCount();
                if (unread > 0) {
                    tvLastMessage.setTypeface(null, Typeface.BOLD);
                    tvLastMessage.setTextColor(context.getResources().getColor(R.color.text_primary));
                } else {
                    tvLastMessage.setTextColor(context.getResources().getColor(R.color.text_secondary));
                }
            }

            // Last message time
            String lastTime = room.getLastMessageTime();
            tvLastMessageTime.setText(lastTime != null ? formatTime(lastTime) : "");

            // Unread count
            int unread = room.getUnreadCount();
            if (unread > 0) {
                tvUnreadBadge.setVisibility(View.VISIBLE);
                tvUnreadBadge.setText(unread > 99 ? "99+" : String.valueOf(unread));
                tvOtherUserName.setTypeface(null, Typeface.BOLD);
            } else {
                tvUnreadBadge.setVisibility(View.GONE);
                tvOtherUserName.setTypeface(null, Typeface.BOLD);
            }

            // Online indicator
            viewOnlineIndicator.setVisibility(room.isOnline() ? View.VISIBLE : View.GONE);

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChatRoomClick(room);
                }
            });

            // Long click listener for delete
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onChatRoomLongClick(room);
                }
                return true;
            });
        }

        private String formatTime(String time) {
            if (time == null) return "";

            // If time contains date, extract just the time part
            if (time.contains(" ")) {
                String[] parts = time.split(" ");
                if (parts.length > 1) {
                    String timePart = parts[1];
                    if (timePart.length() >= 5) {
                        return timePart.substring(0, 5);
                    }
                }
            }
            return time;
        }
    }

    // DiffUtil for efficient updates
    private static class RoomDiffCallback extends DiffUtil.Callback {
        private final List<ChatRoom> oldList;
        private final List<ChatRoom> newList;

        RoomDiffCallback(List<ChatRoom> oldList, List<ChatRoom> newList) {
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
            return oldList.get(oldItemPosition).getRoomId() == newList.get(newItemPosition).getRoomId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            ChatRoom oldRoom = oldList.get(oldItemPosition);
            ChatRoom newRoom = newList.get(newItemPosition);

            return oldRoom.getUnreadCount() == newRoom.getUnreadCount() &&
                    oldRoom.isTyping() == newRoom.isTyping() &&
                    oldRoom.isOnline() == newRoom.isOnline() &&
                    (oldRoom.getLastMessage() == null ? newRoom.getLastMessage() == null :
                            oldRoom.getLastMessage().equals(newRoom.getLastMessage())) &&
                    (oldRoom.getLastMessageTime() == null ? newRoom.getLastMessageTime() == null :
                            oldRoom.getLastMessageTime().equals(newRoom.getLastMessageTime()));
        }

        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            // Return non-null để chỉ partial update, tránh rebind toàn bộ
            return Boolean.TRUE;
        }
    }
}
