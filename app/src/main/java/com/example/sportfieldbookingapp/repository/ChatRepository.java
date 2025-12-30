package com.example.sportfieldbookingapp.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sportfieldbookingapp.api.ApiClient;
import com.example.sportfieldbookingapp.api.ApiService;
import com.example.sportfieldbookingapp.models.ChatMessage;
import com.example.sportfieldbookingapp.models.ChatMessageListResponse;
import com.example.sportfieldbookingapp.models.ChatRoom;
import com.example.sportfieldbookingapp.models.ChatRoomListResponse;
import com.example.sportfieldbookingapp.models.DeleteChatRoomRequest;
import com.example.sportfieldbookingapp.models.DeleteMessageRequest;
import com.example.sportfieldbookingapp.models.GenericResponse;
import com.example.sportfieldbookingapp.models.SendMessageRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {
    private static final String TAG = "ChatRepository";
    private static ChatRepository instance;

    private final ApiService apiService;
    private final Context context;
    private String authToken;
    private int currentUserId;

    // Cache
    private final Map<Integer, List<ChatMessage>> messageCache = new ConcurrentHashMap<>();
    private final Map<Integer, ChatRoom> roomCache = new ConcurrentHashMap<>();
    private final List<ChatMessage> pendingMessages = new ArrayList<>();
    private final Map<String, ChatMessage> pendingMessageMap = new LinkedHashMap<>();

    // Draft messages
    private final Map<Integer, String> draftMessages = new ConcurrentHashMap<>();

    private ChatRepository(Context context) {
        this.context = context.getApplicationContext();
        this.apiService = ApiClient.getClient().create(ApiService.class);
        loadAuthToken();
    }

    public static synchronized ChatRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ChatRepository(context);
        }
        return instance;
    }

    private void loadAuthToken() {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("USER_TOKEN", null);
        currentUserId = prefs.getInt("USER_ID", 0);
        if (token != null) {
            authToken = "Bearer " + token;
        }
    }

    public void refreshToken() {
        loadAuthToken();
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public String getCurrentUserName() {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return prefs.getString("USER_NAME", "");
    }

    // ==================== Network Check ====================
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // ==================== Chat Rooms ====================
    public void getChatRooms(ChatRoomsCallback callback) {
        if (authToken == null) {
            callback.onError("Chưa đăng nhập");
            return;
        }

        apiService.getChatRooms(authToken).enqueue(new Callback<ChatRoomListResponse>() {
            @Override
            public void onResponse(Call<ChatRoomListResponse> call, Response<ChatRoomListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<ChatRoom> rooms = response.body().getData();
                    if (rooms != null) {
                        for (ChatRoom room : rooms) {
                            roomCache.put(room.getRoomId(), room);
                        }
                    }
                    callback.onSuccess(rooms != null ? rooms : new ArrayList<>());
                } else {
                    callback.onError("Không thể tải danh sách chat");
                }
            }

            @Override
            public void onFailure(Call<ChatRoomListResponse> call, Throwable t) {
                Log.e(TAG, "getChatRooms failed: " + t.getMessage());
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // ==================== Messages ====================
    public void getMessages(int roomId, int page, int limit, MessagesCallback callback) {
        if (authToken == null) {
            callback.onError("Chưa đăng nhập");
            return;
        }

        apiService.getChatMessages(authToken, roomId, page, limit).enqueue(new Callback<ChatMessageListResponse>() {
            @Override
            public void onResponse(Call<ChatMessageListResponse> call, Response<ChatMessageListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<ChatMessage> messages = response.body().getData();
                    if (messages == null) messages = new ArrayList<>();

                    // Merge with pending messages
                    List<ChatMessage> merged = mergeWithPendingMessages(roomId, messages);

                    // Update cache
                    if (page == 1) {
                        messageCache.put(roomId, new ArrayList<>(merged));
                    } else {
                        List<ChatMessage> cached = messageCache.get(roomId);
                        if (cached != null) {
                            cached.addAll(0, messages);
                        }
                    }

                    callback.onSuccess(merged, response.body().hasMore());
                } else {
                    callback.onError("Không thể tải tin nhắn");
                }
            }

            @Override
            public void onFailure(Call<ChatMessageListResponse> call, Throwable t) {
                Log.e(TAG, "getMessages failed: " + t.getMessage());
                // Return cached messages if available
                List<ChatMessage> cached = messageCache.get(roomId);
                if (cached != null && !cached.isEmpty()) {
                    callback.onSuccess(cached, false);
                } else {
                    callback.onError("Lỗi kết nối");
                }
            }
        });
    }

    private List<ChatMessage> mergeWithPendingMessages(int roomId, List<ChatMessage> serverMessages) {
        List<ChatMessage> result = new ArrayList<>(serverMessages);

        // Add pending messages that aren't in server response
        for (ChatMessage pending : pendingMessageMap.values()) {
            if (pending.getRoomId() == roomId) {
                boolean found = false;
                for (ChatMessage server : serverMessages) {
                    if (pending.getMessageId().equals(server.getMessageId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    result.add(pending);
                }
            }
        }

        return result;
    }

    // ==================== Send Message ====================
    public ChatMessage sendMessage(int roomId, String content, SendMessageCallback callback) {
        if (authToken == null) {
            callback.onError("Chưa đăng nhập");
            return null;
        }

        if (!isNetworkAvailable()) {
            // Create pending message for offline
            ChatMessage pendingMsg = ChatMessage.createOptimistic(roomId, content, currentUserId, getCurrentUserName());
            pendingMsg.setStatus(ChatMessage.STATUS_FAILED);
            addToPendingMessages(pendingMsg);
            callback.onError("Không có kết nối mạng");
            return pendingMsg;
        }

        // Create optimistic message
        ChatMessage optimisticMsg = ChatMessage.createOptimistic(roomId, content, currentUserId, getCurrentUserName());
        addToPendingMessages(optimisticMsg);

        // Send to server
        SendMessageRequest request = new SendMessageRequest(roomId, content);
        request.setMessageId(optimisticMsg.getMessageId());

        apiService.sendChatMessage(authToken, request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    optimisticMsg.markAsSent();
                    removeFromPendingMessages(optimisticMsg.getMessageId());
                    callback.onSuccess(optimisticMsg);
                } else {
                    optimisticMsg.markAsFailed();
                    callback.onError("Gửi tin nhắn thất bại");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Log.e(TAG, "sendMessage failed: " + t.getMessage());
                optimisticMsg.markAsFailed();
                callback.onError("Lỗi kết nối");
            }
        });

        return optimisticMsg;
    }

    public void retryMessage(ChatMessage message, SendMessageCallback callback) {
        if (message == null || !message.isFailed()) return;

        message.setStatus(ChatMessage.STATUS_SENDING);
        message.incrementRetryCount();

        SendMessageRequest request = new SendMessageRequest(message.getRoomId(), message.getMessage());
        request.setMessageId(message.getMessageId());

        apiService.sendChatMessage(authToken, request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    message.markAsSent();
                    removeFromPendingMessages(message.getMessageId());
                    callback.onSuccess(message);
                } else {
                    message.markAsFailed();
                    callback.onError("Gửi lại thất bại");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                message.markAsFailed();
                callback.onError("Lỗi kết nối");
            }
        });
    }

    private void addToPendingMessages(ChatMessage message) {
        pendingMessageMap.put(message.getMessageId(), message);
    }

    private void removeFromPendingMessages(String messageId) {
        pendingMessageMap.remove(messageId);
    }

    // ==================== Mark as Read ====================
    public void markMessagesAsRead(int roomId) {
        if (authToken == null) return;

        // Update local cache
        ChatRoom room = roomCache.get(roomId);
        if (room != null) {
            room.resetUnreadCount();
        }

        // TODO: Call API to mark as read when backend supports
    }

    // ==================== Draft Messages ====================
    public void saveDraft(int roomId, String content) {
        if (content != null && !content.trim().isEmpty()) {
            draftMessages.put(roomId, content);
            // Also save to SharedPreferences for persistence
            SharedPreferences prefs = context.getSharedPreferences("ChatDrafts", Context.MODE_PRIVATE);
            prefs.edit().putString("draft_" + roomId, content).apply();
        } else {
            draftMessages.remove(roomId);
            SharedPreferences prefs = context.getSharedPreferences("ChatDrafts", Context.MODE_PRIVATE);
            prefs.edit().remove("draft_" + roomId).apply();
        }
    }

    public String getDraft(int roomId) {
        String draft = draftMessages.get(roomId);
        if (draft == null) {
            SharedPreferences prefs = context.getSharedPreferences("ChatDrafts", Context.MODE_PRIVATE);
            draft = prefs.getString("draft_" + roomId, null);
            if (draft != null) {
                draftMessages.put(roomId, draft);
            }
        }
        return draft;
    }

    public void clearDraft(int roomId) {
        draftMessages.remove(roomId);
        SharedPreferences prefs = context.getSharedPreferences("ChatDrafts", Context.MODE_PRIVATE);
        prefs.edit().remove("draft_" + roomId).apply();
    }

    // ==================== Pending Messages for Retry ====================
    public List<ChatMessage> getFailedMessages(int roomId) {
        List<ChatMessage> failed = new ArrayList<>();
        for (ChatMessage msg : pendingMessageMap.values()) {
            if (msg.getRoomId() == roomId && msg.isFailed()) {
                failed.add(msg);
            }
        }
        return failed;
    }

    public void deleteLocalMessage(String messageId) {
        pendingMessageMap.remove(messageId);
    }

    // ==================== Delete Message ====================
    public void deleteMessageForMe(int messageId, DeleteCallback callback) {
        if (authToken == null) {
            callback.onError("Chưa đăng nhập");
            return;
        }

        DeleteMessageRequest request = DeleteMessageRequest.forMe(messageId);
        apiService.deleteMessage(authToken, request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Không thể xóa tin nhắn");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối");
            }
        });
    }

    public void recallMessage(int messageId, RecallCallback callback) {
        if (authToken == null) {
            callback.onError("Chưa đăng nhập");
            return;
        }

        DeleteMessageRequest request = DeleteMessageRequest.forAll(messageId);
        apiService.deleteMessage(authToken, request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess();
                } else {
                    String error = "Không thể thu hồi tin nhắn";
                    try {
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối");
            }
        });
    }

    // ==================== Delete Chat Room ====================
    public void deleteChatRoom(int roomId, DeleteCallback callback) {
        if (authToken == null) {
            callback.onError("Chưa đăng nhập");
            return;
        }

        DeleteChatRoomRequest request = new DeleteChatRoomRequest(roomId);
        apiService.deleteChatRoom(authToken, request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Remove from cache
                    roomCache.remove(roomId);
                    messageCache.remove(roomId);
                    callback.onSuccess();
                } else {
                    callback.onError("Không thể xóa đoạn chat");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối");
            }
        });
    }

    // ==================== Callbacks ====================
    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface RecallCallback {
        void onSuccess();
        void onError(String error);
    }
    public interface ChatRoomsCallback {
        void onSuccess(List<ChatRoom> rooms);
        void onError(String error);
    }

    public interface MessagesCallback {
        void onSuccess(List<ChatMessage> messages, boolean hasMore);
        void onError(String error);
    }

    public interface SendMessageCallback {
        void onSuccess(ChatMessage message);
        void onError(String error);
    }
}
