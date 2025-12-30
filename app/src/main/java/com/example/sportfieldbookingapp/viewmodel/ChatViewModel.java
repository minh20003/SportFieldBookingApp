package com.example.sportfieldbookingapp.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sportfieldbookingapp.models.ChatMessage;
import com.example.sportfieldbookingapp.repository.ChatRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatViewModel extends AndroidViewModel {
    private static final String TAG = "ChatViewModel";
    private static final int PAGE_SIZE = 30;
    private static final long TYPING_TIMEOUT = 2000; // 2 seconds

    private final ChatRepository repository;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // LiveData
    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoadingMore = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSending = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isTyping = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> otherUserTyping = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isOnline = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> hasMoreMessages = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> networkAvailable = new MutableLiveData<>(true);
    private final MutableLiveData<ChatMessage> newMessageReceived = new MutableLiveData<>();
    private final MutableLiveData<ChatMessage> messageStatusChanged = new MutableLiveData<>();

    // State
    private int roomId;
    private String otherUserName;
    private int currentPage = 1;
    private boolean isLoadingMessages = false;
    private String lastSentMessageId = null;
    private final Set<String> sentMessageIds = new HashSet<>();

    // Typing indicator
    private Handler typingHandler = new Handler(Looper.getMainLooper());
    private Runnable typingStopRunnable;
    private boolean currentlyTyping = false;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        repository = ChatRepository.getInstance(application);
    }

    // ==================== Getters ====================
    public LiveData<List<ChatMessage>> getMessages() { return messages; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsLoadingMore() { return isLoadingMore; }
    public LiveData<Boolean> getIsSending() { return isSending; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsTyping() { return isTyping; }
    public LiveData<Boolean> getOtherUserTyping() { return otherUserTyping; }
    public LiveData<Boolean> getIsOnline() { return isOnline; }
    public LiveData<Boolean> getHasMoreMessages() { return hasMoreMessages; }
    public LiveData<Boolean> getNetworkAvailable() { return networkAvailable; }
    public LiveData<ChatMessage> getNewMessageReceived() { return newMessageReceived; }
    public LiveData<ChatMessage> getMessageStatusChanged() { return messageStatusChanged; }

    public int getRoomId() { return roomId; }
    public String getOtherUserName() { return otherUserName; }

    // ==================== Initialize ====================
    public void init(int roomId, String otherUserName) {
        this.roomId = roomId;
        this.otherUserName = otherUserName;
        this.currentPage = 1;
        this.sentMessageIds.clear();

        // Load draft
        String draft = repository.getDraft(roomId);
        if (draft != null) {
            // Will be handled by Activity
        }

        // Mark messages as read
        repository.markMessagesAsRead(roomId);

        // Check network
        networkAvailable.setValue(repository.isNetworkAvailable());

        // Load messages
        loadMessages(false);
    }

    // ==================== Load Messages ====================
    public void loadMessages(boolean loadMore) {
        if (isLoadingMessages) return;

        if (loadMore) {
            if (Boolean.FALSE.equals(hasMoreMessages.getValue())) return;
            isLoadingMore.setValue(true);
            currentPage++;
        } else {
            isLoading.setValue(true);
            currentPage = 1;
        }

        isLoadingMessages = true;

        repository.getMessages(roomId, currentPage, PAGE_SIZE, new ChatRepository.MessagesCallback() {
            @Override
            public void onSuccess(List<ChatMessage> messageList, boolean hasMore) {
                mainHandler.post(() -> {
                    isLoadingMessages = false;
                    isLoading.setValue(false);
                    isLoadingMore.setValue(false);
                    hasMoreMessages.setValue(hasMore);

                    if (loadMore) {
                        List<ChatMessage> current = messages.getValue();
                        if (current != null) {
                            List<ChatMessage> combined = new ArrayList<>(messageList);
                            combined.addAll(current);
                            messages.setValue(combined);
                        }
                    } else {
                        messages.setValue(messageList);
                    }
                });
            }

            @Override
            public void onError(String errorMsg) {
                mainHandler.post(() -> {
                    isLoadingMessages = false;
                    isLoading.setValue(false);
                    isLoadingMore.setValue(false);
                    if (!loadMore) {
                        error.setValue(errorMsg);
                    }
                });
            }
        });
    }

    public void refresh() {
        // Chỉ fetch tin nhắn mới, không reload toàn bộ
        fetchNewMessages();
    }

    private void fetchNewMessages() {
        if (isLoadingMessages) return;

        repository.getMessages(roomId, 1, PAGE_SIZE, new ChatRepository.MessagesCallback() {
            @Override
            public void onSuccess(List<ChatMessage> messageList, boolean hasMore) {
                mainHandler.post(() -> {
                    List<ChatMessage> current = messages.getValue();
                    if (current == null) {
                        messages.setValue(messageList);
                        return;
                    }

                    // Chỉ cập nhật nếu có thay đổi thực sự
                    if (hasRealChanges(current, messageList)) {
                        // Merge tin nhắn mới vào danh sách hiện tại
                        List<ChatMessage> merged = mergeMessages(current, messageList);
                        messages.setValue(merged);
                    }
                });
            }

            @Override
            public void onError(String errorMsg) {
                // Silent fail for refresh - không hiện lỗi
            }
        });
    }

    private boolean hasRealChanges(List<ChatMessage> current, List<ChatMessage> newList) {
        if (current.size() != newList.size()) return true;
        if (current.isEmpty()) return false;

        // So sánh tin nhắn cuối cùng
        ChatMessage lastCurrent = current.get(current.size() - 1);
        ChatMessage lastNew = newList.get(newList.size() - 1);

        // Kiểm tra ID khác nhau = có tin nhắn mới
        if (lastCurrent.getId() != lastNew.getId()) return true;

        // Kiểm tra status thay đổi
        if (lastCurrent.getStatus() != lastNew.getStatus()) return true;

        // Kiểm tra recalled status
        if (lastCurrent.isRecalled() != lastNew.isRecalled()) return true;

        // Kiểm tra các tin nhắn khác có thay đổi status/recalled không
        for (int i = 0; i < current.size() && i < newList.size(); i++) {
            ChatMessage c = current.get(i);
            ChatMessage n = newList.get(i);
            if (c.getId() == n.getId()) {
                if (c.getStatus() != n.getStatus() || c.isRecalled() != n.isRecalled()) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<ChatMessage> mergeMessages(List<ChatMessage> current, List<ChatMessage> newList) {
        // Nếu không có tin nhắn optimistic (đang gửi), dùng list mới
        boolean hasOptimistic = false;
        for (ChatMessage msg : current) {
            if (msg.getStatus() == ChatMessage.STATUS_SENDING) {
                hasOptimistic = true;
                break;
            }
        }
        
        if (!hasOptimistic) {
            return new ArrayList<>(newList);
        }

        // Có tin nhắn optimistic, cần merge cẩn thận
        java.util.Map<Integer, ChatMessage> serverMessages = new java.util.HashMap<>();
        for (ChatMessage msg : newList) {
            if (msg.getId() > 0) {
                serverMessages.put(msg.getId(), msg);
            }
        }

        List<ChatMessage> result = new ArrayList<>();
        java.util.Set<Integer> addedIds = new java.util.HashSet<>();

        // Thêm tin nhắn từ server trước (theo thứ tự)
        for (ChatMessage msg : newList) {
            result.add(msg);
            if (msg.getId() > 0) {
                addedIds.add(msg.getId());
            }
        }

        // Thêm tin nhắn optimistic (đang gửi) vào cuối
        for (ChatMessage msg : current) {
            if (msg.getStatus() == ChatMessage.STATUS_SENDING && msg.getId() <= 0) {
                // Kiểm tra xem đã có trên server chưa (bằng messageId)
                boolean existsOnServer = false;
                for (ChatMessage serverMsg : newList) {
                    if (msg.getMessageId() != null && msg.getMessageId().equals(serverMsg.getMessageId())) {
                        existsOnServer = true;
                        break;
                    }
                }
                if (!existsOnServer) {
                    result.add(msg);
                }
            }
        }

        return result;
    }

    // ==================== Send Message ====================
    public void sendMessage(String content) {
        if (content == null || content.trim().isEmpty()) return;

        String trimmedContent = content.trim();

        // Prevent duplicate sends
        String tempId = trimmedContent + "_" + System.currentTimeMillis();
        if (sentMessageIds.contains(tempId)) return;

        // Check if same as last message (prevent rapid duplicate)
        if (lastSentMessageId != null && lastSentMessageId.equals(trimmedContent)) {
            return;
        }

        sentMessageIds.add(tempId);
        lastSentMessageId = trimmedContent;
        isSending.setValue(true);

        // Clear draft
        repository.clearDraft(roomId);

        // Send with optimistic UI
        ChatMessage optimisticMsg = repository.sendMessage(roomId, trimmedContent, new ChatRepository.SendMessageCallback() {
            @Override
            public void onSuccess(ChatMessage message) {
                mainHandler.post(() -> {
                    isSending.setValue(false);
                    updateMessageInList(message);
                    messageStatusChanged.setValue(message);

                    // Clear duplicate prevention after success
                    mainHandler.postDelayed(() -> {
                        sentMessageIds.remove(tempId);
                        if (lastSentMessageId != null && lastSentMessageId.equals(trimmedContent)) {
                            lastSentMessageId = null;
                        }
                    }, 1000);
                });
            }

            @Override
            public void onError(String errorMsg) {
                mainHandler.post(() -> {
                    isSending.setValue(false);
                    error.setValue(errorMsg);
                    sentMessageIds.remove(tempId);
                });
            }
        });

        // Add optimistic message to list immediately
        if (optimisticMsg != null) {
            addMessageToList(optimisticMsg);
        }
    }

    private void addMessageToList(ChatMessage message) {
        List<ChatMessage> current = messages.getValue();
        if (current == null) current = new ArrayList<>();

        // Check if already exists
        for (ChatMessage m : current) {
            if (m.getMessageId() != null && m.getMessageId().equals(message.getMessageId())) {
                return;
            }
        }

        List<ChatMessage> updated = new ArrayList<>(current);
        updated.add(message);
        messages.setValue(updated);
    }

    private void updateMessageInList(ChatMessage message) {
        List<ChatMessage> current = messages.getValue();
        if (current == null) return;

        List<ChatMessage> updated = new ArrayList<>();
        boolean found = false;

        for (ChatMessage m : current) {
            if (m.getMessageId() != null && m.getMessageId().equals(message.getMessageId())) {
                updated.add(message);
                found = true;
            } else {
                updated.add(m);
            }
        }

        if (!found) {
            updated.add(message);
        }

        messages.setValue(updated);
    }

    // ==================== Retry Failed Message ====================
    public void retryMessage(ChatMessage message) {
        if (message == null || !message.isFailed()) return;

        repository.retryMessage(message, new ChatRepository.SendMessageCallback() {
            @Override
            public void onSuccess(ChatMessage msg) {
                mainHandler.post(() -> {
                    updateMessageInList(msg);
                    messageStatusChanged.setValue(msg);
                });
            }

            @Override
            public void onError(String errorMsg) {
                mainHandler.post(() -> {
                    error.setValue(errorMsg);
                    updateMessageInList(message);
                });
            }
        });

        updateMessageInList(message);
    }

    // ==================== Delete Message ====================
    public void deleteMessage(ChatMessage message) {
        if (message == null) return;

        // Remove from local list
        List<ChatMessage> current = messages.getValue();
        if (current != null) {
            List<ChatMessage> updated = new ArrayList<>();
            for (ChatMessage m : current) {
                if (!m.getMessageId().equals(message.getMessageId())) {
                    updated.add(m);
                }
            }
            messages.setValue(updated);
        }

        // Remove from pending if failed
        if (message.isFailed()) {
            repository.deleteLocalMessage(message.getMessageId());
        }
    }

    public void deleteMessageForMe(ChatMessage message) {
        if (message == null) return;

        // Remove from local list immediately (optimistic)
        removeMessageFromList(message);

        // Call API
        repository.deleteMessageForMe(message.getId(), new ChatRepository.DeleteCallback() {
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
                    // Re-add message if failed
                    addMessageToList(message);
                });
            }
        });
    }

    public void recallMessage(ChatMessage message) {
        if (message == null) return;

        // Update message locally (optimistic)
        message.setRecalled(true);
        message.setMessage("Tin nhắn đã được thu hồi");
        updateMessageInList(message);

        // Call API
        repository.recallMessage(message.getId(), new ChatRepository.RecallCallback() {
            @Override
            public void onSuccess() {
                mainHandler.post(() -> {
                    messageStatusChanged.setValue(message);
                });
            }

            @Override
            public void onError(String errorMsg) {
                mainHandler.post(() -> {
                    error.setValue(errorMsg);
                    // Revert if failed - reload messages
                    loadMessages(false);
                });
            }
        });
    }

    private void removeMessageFromList(ChatMessage message) {
        List<ChatMessage> current = messages.getValue();
        if (current == null) return;

        List<ChatMessage> updated = new ArrayList<>();
        for (ChatMessage m : current) {
            boolean shouldRemove = false;
            if (message.getMessageId() != null && m.getMessageId() != null) {
                shouldRemove = m.getMessageId().equals(message.getMessageId());
            } else {
                shouldRemove = m.getId() == message.getId();
            }
            if (!shouldRemove) {
                updated.add(m);
            }
        }
        messages.setValue(updated);
    }

    // ==================== Typing Indicator ====================
    public void onUserTyping() {
        if (!currentlyTyping) {
            currentlyTyping = true;
            isTyping.setValue(true);
            // TODO: Send typing event to server via WebSocket/Firebase
        }

        // Reset typing timeout
        if (typingStopRunnable != null) {
            typingHandler.removeCallbacks(typingStopRunnable);
        }

        typingStopRunnable = () -> {
            currentlyTyping = false;
            isTyping.setValue(false);
            // TODO: Send stop typing event to server
        };

        typingHandler.postDelayed(typingStopRunnable, TYPING_TIMEOUT);
    }

    public void onUserStoppedTyping() {
        if (typingStopRunnable != null) {
            typingHandler.removeCallbacks(typingStopRunnable);
        }
        currentlyTyping = false;
        isTyping.setValue(false);
    }

    // Called when receiving typing event from other user
    public void setOtherUserTyping(boolean typing) {
        otherUserTyping.setValue(typing);
    }

    // ==================== Draft ====================
    public void saveDraft(String content) {
        repository.saveDraft(roomId, content);
    }

    public String getDraft() {
        return repository.getDraft(roomId);
    }

    // ==================== Network ====================
    public void checkNetwork() {
        networkAvailable.setValue(repository.isNetworkAvailable());
    }

    // ==================== Mark as Seen ====================
    public void markMessageAsSeen(ChatMessage message) {
        if (message == null || message.isMine() || message.isSeen()) return;

        message.markAsSeen();
        // TODO: Send seen event to server
    }

    // ==================== Cleanup ====================
    @Override
    protected void onCleared() {
        super.onCleared();
        if (typingStopRunnable != null) {
            typingHandler.removeCallbacks(typingStopRunnable);
        }
    }

    // ==================== Copy Message ====================
    public String getMessageContent(ChatMessage message) {
        return message != null ? message.getMessage() : "";
    }
}
