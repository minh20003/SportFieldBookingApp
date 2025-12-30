package com.example.sportfieldbookingapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji2.emojipicker.EmojiPickerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.adapters.ChatMessageAdapter;
import com.example.sportfieldbookingapp.models.ChatMessage;
import com.example.sportfieldbookingapp.viewmodel.ChatViewModel;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements ChatMessageAdapter.MessageActionListener {

    private static final String TAG = "ChatActivity";
    private static final long REFRESH_INTERVAL = 10000; // 10 giây thay vì 5 giây

    // Views
    private RecyclerView recyclerView;
    private ChatMessageAdapter adapter;
    private EditText etMessage;
    private ImageButton btnSend, btnBack, btnCall, btnVideoCall, btnInfo, btnEmoji, btnAttach;
    private ProgressBar progressBar, progressLoadMore;
    private LinearLayout layoutEmpty;
    private TextView tvEmpty, tvUserName, tvOnlineStatus, tvTypingIndicator, tvNetworkStatus;
    private ImageView ivAvatar;
    private View viewOnlineIndicator;

    // Emoji Picker
    private FrameLayout emojiPickerContainer;
    private EmojiPickerView emojiPickerView;
    private boolean isEmojiPickerVisible = false;

    // ViewModel
    private ChatViewModel viewModel;

    // State
    private int roomId;
    private int otherUserId;
    private String otherUserName;
    private String currentUserId;
    private String currentUserName;
    private LinearLayoutManager layoutManager;
    private boolean isLoadingMore = false;

    // Network receiver
    private BroadcastReceiver networkReceiver;

    // Fallback refresh handler
    private Handler refreshHandler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;
    private boolean usePolling = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        roomId = getIntent().getIntExtra("room_id", 0);
        otherUserId = getIntent().getIntExtra("other_user_id", 0);
        otherUserName = getIntent().getStringExtra("other_user_name");

        // Lấy thông tin user hiện tại
        android.content.SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = String.valueOf(prefs.getInt("USER_ID", 0));
        currentUserName = prefs.getString("USER_NAME", "User");

        if (roomId == 0) {
            Toast.makeText(this, "Lỗi: Không tìm thấy phòng chat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupViewModel();
        setupRecyclerView();
        setupEmojiPicker();
        setupListeners();
        setupNetworkReceiver();

        viewModel.init(roomId, otherUserName);

        String draft = viewModel.getDraft();
        if (draft != null && !draft.isEmpty()) {
            etMessage.setText(draft);
            etMessage.setSelection(draft.length());
        }
    }

    private void initViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        ivAvatar = findViewById(R.id.ivAvatar);
        viewOnlineIndicator = findViewById(R.id.viewOnlineIndicator);
        tvUserName = findViewById(R.id.tvUserName);
        tvOnlineStatus = findViewById(R.id.tvOnlineStatus);
        tvTypingIndicator = findViewById(R.id.tvTypingIndicator);
        tvNetworkStatus = findViewById(R.id.tvNetworkStatus);
        btnCall = findViewById(R.id.btnCall);
        btnVideoCall = findViewById(R.id.btnVideoCall);
        btnInfo = findViewById(R.id.btnInfo);

        // Messages
        recyclerView = findViewById(R.id.recyclerViewMessages);
        progressBar = findViewById(R.id.progressBar);
        progressLoadMore = findViewById(R.id.progressLoadMore);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Input
        btnEmoji = findViewById(R.id.btnEmoji);
        etMessage = findViewById(R.id.etMessage);
        btnAttach = findViewById(R.id.btnAttach);
        btnSend = findViewById(R.id.btnSend);

        // Emoji Picker
        emojiPickerContainer = findViewById(R.id.emojiPickerContainer);
        emojiPickerView = findViewById(R.id.emojiPickerView);

        tvUserName.setText(otherUserName != null ? otherUserName : "Chat");
        updateSendButtonState();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        viewModel.getMessages().observe(this, messages -> {
            if (messages != null) {
                int previousSize = adapter.getItemCount();
                int previousLastId = previousSize > 0 ? adapter.getLastMessageId() : -1;
                
                adapter.updateMessages(messages);
                layoutEmpty.setVisibility(messages.isEmpty() ? View.VISIBLE : View.GONE);

                // Chỉ scroll xuống nếu có tin nhắn MỚI thực sự (ID khác)
                if (!messages.isEmpty()) {
                    int newLastId = messages.get(messages.size() - 1).getId();
                    // Scroll nếu: tin nhắn mới hoặc là lần load đầu tiên
                    if (previousSize == 0 || (newLastId != previousLastId && messages.size() > previousSize)) {
                        recyclerView.scrollToPosition(messages.size() - 1);
                    }
                }
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsLoadingMore().observe(this, loading -> {
            isLoadingMore = loading;
            progressLoadMore.setVisibility(loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsSending().observe(this, isSending -> {
            btnSend.setEnabled(!isSending);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOtherUserTyping().observe(this, isTyping -> {
            tvTypingIndicator.setVisibility(isTyping ? View.VISIBLE : View.GONE);
            tvOnlineStatus.setVisibility(isTyping ? View.GONE :
                    (Boolean.TRUE.equals(viewModel.getIsOnline().getValue()) ? View.VISIBLE : View.GONE));
        });

        viewModel.getIsOnline().observe(this, isOnline -> {
            viewOnlineIndicator.setVisibility(isOnline ? View.VISIBLE : View.GONE);
            if (isOnline && !Boolean.TRUE.equals(viewModel.getOtherUserTyping().getValue())) {
                tvOnlineStatus.setVisibility(View.VISIBLE);
                tvOnlineStatus.setText("Đang hoạt động");
            } else {
                tvOnlineStatus.setVisibility(View.GONE);
            }
        });

        viewModel.getNetworkAvailable().observe(this, available -> {
            tvNetworkStatus.setVisibility(available ? View.GONE : View.VISIBLE);
        });

        viewModel.getMessageStatusChanged().observe(this, message -> {
            if (message != null) {
                adapter.updateMessage(message);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ChatMessageAdapter(this, new ArrayList<>(), this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < 0 && !isLoadingMore) {
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    if (firstVisibleItem <= 5) {
                        viewModel.loadMessages(true);
                    }
                }
            }
        });
    }

    private void setupEmojiPicker() {
        // Set up emoji picker callback
        emojiPickerView.setOnEmojiPickedListener(emojiViewItem -> {
            // Insert emoji at cursor position
            int start = Math.max(etMessage.getSelectionStart(), 0);
            int end = Math.max(etMessage.getSelectionEnd(), 0);
            
            etMessage.getText().replace(
                    Math.min(start, end),
                    Math.max(start, end),
                    emojiViewItem.getEmoji(),
                    0,
                    emojiViewItem.getEmoji().length()
            );
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> saveDraftAndFinish());

        btnSend.setOnClickListener(v -> sendMessage());

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSendButtonState();
                viewModel.onUserTyping();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Focus listener to hide emoji picker when keyboard shows
        etMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && isEmojiPickerVisible) {
                hideEmojiPicker();
            }
        });

        etMessage.setOnClickListener(v -> {
            if (isEmojiPickerVisible) {
                hideEmojiPicker();
            }
        });

        // Emoji button toggle
        btnEmoji.setOnClickListener(v -> {
            if (isEmojiPickerVisible) {
                hideEmojiPicker();
                showKeyboard();
            } else {
                hideKeyboard();
                // Delay to let keyboard hide first
                new Handler(Looper.getMainLooper()).postDelayed(this::showEmojiPicker, 100);
            }
        });

        // Voice call button
        btnCall.setOnClickListener(v -> {
            if (otherUserId == 0) {
                Toast.makeText(this, "Không thể gọi điện", Toast.LENGTH_SHORT).show();
                return;
            }
            showCallOptionsDialog();
        });

        // Video call button (nếu có)
        if (btnVideoCall != null) {
            btnVideoCall.setOnClickListener(v -> startVideoCall());
        }

        btnInfo.setOnClickListener(v ->
                Toast.makeText(this, "Thông tin cuộc trò chuyện", Toast.LENGTH_SHORT).show()
        );

        btnAttach.setOnClickListener(v ->
                Toast.makeText(this, "Đính kèm file đang phát triển", Toast.LENGTH_SHORT).show()
        );
    }

    private void showCallOptionsDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Gọi cho " + otherUserName)
                .setItems(new String[]{"📞 Gọi thoại", "📹 Gọi video"}, (dialog, which) -> {
                    if (which == 0) {
                        startVoiceCall();
                    } else {
                        startVideoCall();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void startVoiceCall() {
        CallActivity.startVoiceCall(
                this,
                String.valueOf(otherUserId),
                otherUserName,
                currentUserId,
                currentUserName
        );
    }

    private void startVideoCall() {
        CallActivity.startVideoCall(
                this,
                String.valueOf(otherUserId),
                otherUserName,
                currentUserId,
                currentUserName
        );
    }

    private void showEmojiPicker() {
        isEmojiPickerVisible = true;
        emojiPickerContainer.setVisibility(View.VISIBLE);
        btnEmoji.setImageResource(R.drawable.ic_keyboard);
    }

    private void hideEmojiPicker() {
        isEmojiPickerVisible = false;
        emojiPickerContainer.setVisibility(View.GONE);
        btnEmoji.setImageResource(R.drawable.ic_emoji);
    }

    private void showKeyboard() {
        etMessage.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etMessage, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
        }
    }

    private void setupNetworkReceiver() {
        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                viewModel.checkNetwork();
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    private void updateSendButtonState() {
        String text = etMessage.getText().toString().trim();
        boolean hasText = !TextUtils.isEmpty(text);

        btnSend.setEnabled(hasText);
        btnSend.setAlpha(hasText ? 1.0f : 0.5f);
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) return;

        etMessage.setText("");
        updateSendButtonState();

        // Hide emoji picker if visible
        if (isEmojiPickerVisible) {
            hideEmojiPicker();
        }

        viewModel.sendMessage(content);
    }

    private void saveDraftAndFinish() {
        String draft = etMessage.getText().toString().trim();
        viewModel.saveDraft(draft);
        finish();
    }

    // ==================== MessageActionListener ====================
    @Override
    public void onRetryClick(ChatMessage message) {
        viewModel.retryMessage(message);
    }

    @Override
    public void onDeleteForMeClick(ChatMessage message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xóa tin nhắn")
                .setMessage("Tin nhắn sẽ bị xóa ở phía bạn. Người khác vẫn có thể xem tin nhắn này.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteMessageForMe(message);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onRecallClick(ChatMessage message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thu hồi tin nhắn")
                .setMessage("Tin nhắn sẽ bị thu hồi với tất cả mọi người trong cuộc trò chuyện.")
                .setPositiveButton("Thu hồi", (dialog, which) -> {
                    viewModel.recallMessage(message);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onCopyClick(ChatMessage message) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("message", message.getMessage());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Đã sao chép", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessageVisible(ChatMessage message) {
        viewModel.markMessageAsSeen(message);
    }

    // ==================== Lifecycle ====================
    @Override
    protected void onResume() {
        super.onResume();

        if (usePolling) {
            startPolling();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopPolling();
        viewModel.onUserStoppedTyping();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        if (isEmojiPickerVisible) {
            hideEmojiPicker();
        } else {
            saveDraftAndFinish();
        }
    }

    // ==================== Polling ====================
    private void startPolling() {
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                viewModel.refresh();
                refreshHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    private void stopPolling() {
        if (refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
    }
}
