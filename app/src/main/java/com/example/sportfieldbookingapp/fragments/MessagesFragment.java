package com.example.sportfieldbookingapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.activities.ChatActivity;
import com.example.sportfieldbookingapp.adapters.ChatRoomAdapter;
import com.example.sportfieldbookingapp.models.ChatRoom;
import com.example.sportfieldbookingapp.viewmodel.MessagesViewModel;

import java.util.ArrayList;

public class MessagesFragment extends Fragment {

    private static final String TAG = "MessagesFragment";

    // Views
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private TextView tvEmpty;
    private EditText etSearch;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    // Adapter
    private ChatRoomAdapter adapter;

    // ViewModel
    private MessagesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupListeners();

        // Initial load
        viewModel.loadChatRooms(true);
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewChatRooms);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        etSearch = view.findViewById(R.id.etSearch);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MessagesViewModel.class);

        // Observe filtered chat rooms
        viewModel.getFilteredChatRooms().observe(getViewLifecycleOwner(), rooms -> {
            if (rooms != null) {
                adapter.updateRooms(rooms);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe refreshing state
        viewModel.getIsRefreshing().observe(getViewLifecycleOwner(), isRefreshing -> {
            swipeRefreshLayout.setRefreshing(isRefreshing);
        });

        // Observe empty state
        viewModel.getIsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        });

        // Observe errors
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe total unread count (could be used for badge)
        viewModel.getTotalUnreadCount().observe(getViewLifecycleOwner(), count -> {
            // Update badge if needed
        });
    }

    private void setupRecyclerView() {
        adapter = new ChatRoomAdapter(getContext(), new ArrayList<>(), new ChatRoomAdapter.OnChatRoomClickListener() {
            @Override
            public void onChatRoomClick(ChatRoom chatRoom) {
                // Mark as read when opening
                viewModel.markRoomAsRead(chatRoom.getRoomId());

                // Open chat
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("room_id", chatRoom.getRoomId());
                intent.putExtra("other_user_id", chatRoom.getOtherUserId());
                intent.putExtra("other_user_name", chatRoom.getOtherUserName());
                startActivity(intent);
            }

            @Override
            public void onChatRoomLongClick(ChatRoom chatRoom) {
                showChatRoomOptionsDialog(chatRoom);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void showChatRoomOptionsDialog(ChatRoom chatRoom) {
        String[] options = {"Xóa đoạn chat"};
        
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(chatRoom.getOtherUserName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showDeleteChatRoomConfirmation(chatRoom);
                    }
                })
                .show();
    }

    private void showDeleteChatRoomConfirmation(ChatRoom chatRoom) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xóa đoạn chat")
                .setMessage("Đoạn chat sẽ bị xóa ở phía bạn. Nếu có tin nhắn mới, đoạn chat sẽ xuất hiện lại.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteChatRoom(chatRoom.getRoomId());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void setupListeners() {
        // Pull to refresh
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refresh());

        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterRooms(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh when returning to this fragment
        viewModel.refresh();
    }
}
