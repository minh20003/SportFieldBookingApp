package com.example.sportfieldbookingapp.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sportfieldbookingapp.models.ChatRoom;
import com.example.sportfieldbookingapp.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;

public class MessagesViewModel extends AndroidViewModel {
    private static final String TAG = "MessagesViewModel";

    private final ChatRepository repository;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // LiveData
    private final MutableLiveData<List<ChatRoom>> chatRooms = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ChatRoom>> filteredChatRooms = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalUnreadCount = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>(false);

    // State
    private String currentSearchQuery = "";
    private List<ChatRoom> allRooms = new ArrayList<>();

    public MessagesViewModel(@NonNull Application application) {
        super(application);
        repository = ChatRepository.getInstance(application);
    }

    // ==================== Getters ====================
    public LiveData<List<ChatRoom>> getChatRooms() { return chatRooms; }
    public LiveData<List<ChatRoom>> getFilteredChatRooms() { return filteredChatRooms; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsRefreshing() { return isRefreshing; }
    public LiveData<String> getError() { return error; }
    public LiveData<Integer> getTotalUnreadCount() { return totalUnreadCount; }
    public LiveData<Boolean> getIsEmpty() { return isEmpty; }

    // ==================== Load Chat Rooms ====================
    public void loadChatRooms(boolean showLoading) {
        if (showLoading) {
            isLoading.setValue(true);
        } else {
            isRefreshing.setValue(true);
        }

        repository.refreshToken();
        repository.getChatRooms(new ChatRepository.ChatRoomsCallback() {
            @Override
            public void onSuccess(List<ChatRoom> rooms) {
                mainHandler.post(() -> {
                    isLoading.setValue(false);
                    isRefreshing.setValue(false);

                    List<ChatRoom> newRooms = rooms != null ? rooms : new ArrayList<>();
                    
                    // Chỉ cập nhật nếu có thay đổi thực sự
                    if (hasRealChanges(allRooms, newRooms)) {
                        allRooms = newRooms;
                        chatRooms.setValue(allRooms);
                        filterRooms(currentSearchQuery);
                    }

                    // Calculate total unread
                    int unread = 0;
                    for (ChatRoom room : allRooms) {
                        unread += room.getUnreadCount();
                    }
                    totalUnreadCount.setValue(unread);

                    isEmpty.setValue(allRooms.isEmpty());
                });
            }

            @Override
            public void onError(String errorMsg) {
                mainHandler.post(() -> {
                    isLoading.setValue(false);
                    isRefreshing.setValue(false);
                    // Chỉ hiện lỗi nếu đang loading lần đầu
                    if (allRooms.isEmpty()) {
                        error.setValue(errorMsg);
                    }
                    isEmpty.setValue(allRooms.isEmpty());
                });
            }
        });
    }

    private boolean hasRealChanges(List<ChatRoom> oldList, List<ChatRoom> newList) {
        if (oldList.size() != newList.size()) return true;
        if (oldList.isEmpty()) return false;

        // So sánh từng room
        for (int i = 0; i < oldList.size(); i++) {
            ChatRoom oldRoom = oldList.get(i);
            ChatRoom newRoom = null;
            
            // Tìm room tương ứng trong newList
            for (ChatRoom r : newList) {
                if (r.getRoomId() == oldRoom.getRoomId()) {
                    newRoom = r;
                    break;
                }
            }
            
            if (newRoom == null) return true; // Room bị xóa
            
            // So sánh các thuộc tính quan trọng
            if (oldRoom.getUnreadCount() != newRoom.getUnreadCount()) return true;
            
            String oldMsg = oldRoom.getLastMessage();
            String newMsg = newRoom.getLastMessage();
            if (oldMsg == null && newMsg != null) return true;
            if (oldMsg != null && !oldMsg.equals(newMsg)) return true;
            
            String oldTime = oldRoom.getLastMessageTime();
            String newTime = newRoom.getLastMessageTime();
            if (oldTime == null && newTime != null) return true;
            if (oldTime != null && !oldTime.equals(newTime)) return true;
        }
        
        return false;
    }

    public void refresh() {
        loadChatRooms(false);
    }

    // ==================== Search/Filter ====================
    public void filterRooms(String query) {
        currentSearchQuery = query != null ? query.toLowerCase().trim() : "";

        if (currentSearchQuery.isEmpty()) {
            filteredChatRooms.setValue(allRooms);
            isEmpty.setValue(allRooms.isEmpty());
        } else {
            List<ChatRoom> filtered = new ArrayList<>();
            for (ChatRoom room : allRooms) {
                if (room.getOtherUserName() != null &&
                        room.getOtherUserName().toLowerCase().contains(currentSearchQuery)) {
                    filtered.add(room);
                }
            }
            filteredChatRooms.setValue(filtered);
            isEmpty.setValue(filtered.isEmpty());
        }
    }

    // ==================== Update Room ====================
    public void updateRoomUnreadCount(int roomId, int unreadCount) {
        List<ChatRoom> current = chatRooms.getValue();
        if (current == null) return;

        for (ChatRoom room : current) {
            if (room.getRoomId() == roomId) {
                room.setUnreadCount(unreadCount);
                break;
            }
        }

        chatRooms.setValue(current);
        filterRooms(currentSearchQuery);

        // Recalculate total unread
        int total = 0;
        for (ChatRoom room : current) {
            total += room.getUnreadCount();
        }
        totalUnreadCount.setValue(total);
    }

    public void markRoomAsRead(int roomId) {
        updateRoomUnreadCount(roomId, 0);
        repository.markMessagesAsRead(roomId);
    }

    // ==================== New Message Received ====================
    public void onNewMessageReceived(int roomId, String message, String time, int senderId) {
        List<ChatRoom> current = chatRooms.getValue();
        if (current == null) return;

        for (ChatRoom room : current) {
            if (room.getRoomId() == roomId) {
                room.updateLastMessage(message, time, senderId);
                room.incrementUnreadCount();
                break;
            }
        }

        // Sort rooms by last message time (most recent first)
        sortRoomsByLastMessage(current);

        chatRooms.setValue(current);
        filterRooms(currentSearchQuery);

        // Update total unread
        int total = 0;
        for (ChatRoom room : current) {
            total += room.getUnreadCount();
        }
        totalUnreadCount.setValue(total);
    }

    private void sortRoomsByLastMessage(List<ChatRoom> rooms) {
        // Simple sort - room with most recent message first
        // In production, parse timestamps properly
        rooms.sort((r1, r2) -> {
            String t1 = r1.getLastMessageTime();
            String t2 = r2.getLastMessageTime();
            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return t2.compareTo(t1);
        });
    }

    // ==================== Typing Indicator ====================
    public void setRoomTyping(int roomId, boolean isTyping) {
        List<ChatRoom> current = chatRooms.getValue();
        if (current == null) return;

        for (ChatRoom room : current) {
            if (room.getRoomId() == roomId) {
                room.setTyping(isTyping);
                break;
            }
        }

        chatRooms.setValue(current);
        filterRooms(currentSearchQuery);
    }

    // ==================== Online Status ====================
    public void setRoomOnlineStatus(int roomId, boolean isOnline) {
        List<ChatRoom> current = chatRooms.getValue();
        if (current == null) return;

        for (ChatRoom room : current) {
            if (room.getRoomId() == roomId) {
                room.setOnline(isOnline);
                break;
            }
        }

        chatRooms.setValue(current);
        filterRooms(currentSearchQuery);
    }

    // ==================== Delete Chat Room ====================
    public void deleteChatRoom(int roomId) {
        // Remove from local list immediately (optimistic)
        removeRoomFromList(roomId);

        repository.deleteChatRoom(roomId, new ChatRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                mainHandler.post(() -> {
                    // Already removed from list
                });
            }

            @Override
            public void onError(String errorMsg) {
                mainHandler.post(() -> {
                    error.setValue(errorMsg);
                    // Reload to restore if failed
                    loadChatRooms(false);
                });
            }
        });
    }

    private void removeRoomFromList(int roomId) {
        List<ChatRoom> current = chatRooms.getValue();
        if (current == null) return;

        List<ChatRoom> updated = new ArrayList<>();
        for (ChatRoom room : current) {
            if (room.getRoomId() != roomId) {
                updated.add(room);
            }
        }

        allRooms = updated;
        chatRooms.setValue(updated);
        filterRooms(currentSearchQuery);
        isEmpty.setValue(updated.isEmpty());

        // Recalculate total unread
        int total = 0;
        for (ChatRoom room : updated) {
            total += room.getUnreadCount();
        }
        totalUnreadCount.setValue(total);
    }
}
