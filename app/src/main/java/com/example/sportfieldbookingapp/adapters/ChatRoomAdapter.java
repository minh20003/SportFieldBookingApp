package com.example.sportfieldbookingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.models.ChatRoom;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {

    public interface OnChatRoomClickListener {
        void onChatRoomClick(ChatRoom chatRoom);
    }

    private final LayoutInflater inflater;
    private final List<ChatRoom> chatRooms;
    private final OnChatRoomClickListener listener;

    public ChatRoomAdapter(Context context, List<ChatRoom> chatRooms, OnChatRoomClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.chatRooms = chatRooms;
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
        holder.bind(room, listener);
    }

    @Override
    public int getItemCount() {
        return chatRooms != null ? chatRooms.size() : 0;
    }

    static class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOtherUserName;
        private final TextView tvLastMessage;
        private final TextView tvLastMessageTime;
        private final TextView tvUnreadBadge;

        ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOtherUserName = itemView.findViewById(R.id.tvOtherUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvLastMessageTime = itemView.findViewById(R.id.tvLastMessageTime);
            tvUnreadBadge = itemView.findViewById(R.id.tvUnreadBadge);
        }

        void bind(ChatRoom room, OnChatRoomClickListener listener) {
            tvOtherUserName.setText(room.getOtherUserName() != null ? room.getOtherUserName() : "Người dùng");

            String lastMessage = room.getLastMessage();
            if (lastMessage == null || lastMessage.trim().isEmpty()) {
                lastMessage = "Chưa có tin nhắn";
            }
            tvLastMessage.setText(lastMessage);

            String lastTime = room.getLastMessageTime();
            tvLastMessageTime.setText(lastTime != null ? lastTime : "");

            int unread = room.getUnreadCount();
            if (unread > 0) {
                tvUnreadBadge.setVisibility(View.VISIBLE);
                tvUnreadBadge.setText(unread > 99 ? "99+" : String.valueOf(unread));
            } else {
                tvUnreadBadge.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChatRoomClick(room);
                }
            });
        }
    }
}




