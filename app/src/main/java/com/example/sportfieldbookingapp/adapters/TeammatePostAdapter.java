package com.example.sportfieldbookingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.models.TeammatePost;
import java.util.List;
import android.view.View;
public class TeammatePostAdapter extends RecyclerView.Adapter<TeammatePostAdapter.PostViewHolder> {

    private final List<TeammatePost> postList;
    private final int currentUserId;
    private OnJoinButtonClickListener joinListener;
    private OnDeleteButtonClickListener deleteListener;
    private OnEditButtonClickListener editListener; // Listener cho nút Sửa
    private OnItemClickListener itemClickListener;
    // Định nghĩa các interface cho các sự kiện click
    public interface OnJoinButtonClickListener { void onJoinClick(TeammatePost post); }
    public interface OnDeleteButtonClickListener { void onDeleteClick(TeammatePost post, int position); }
    public interface OnEditButtonClickListener { void onEditClick(TeammatePost post); }
    public interface OnItemClickListener { void onItemClick(TeammatePost post); }
    // Các hàm để Activity set listener
    public void setOnJoinButtonClickListener(OnJoinButtonClickListener listener) { this.joinListener = listener; }
    public void setOnDeleteButtonClickListener(OnDeleteButtonClickListener listener) { this.deleteListener = listener; }
    public void setOnEditButtonClickListener(OnEditButtonClickListener listener) { this.editListener = listener; }
    public void setOnItemClickListener(OnItemClickListener listener) { this.itemClickListener = listener; }

    public TeammatePostAdapter(List<TeammatePost> postList, int currentUserId) {
        this.postList = postList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teammate_post, parent, false);
        return new PostViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        TeammatePost post = postList.get(position);

        holder.tvSportType.setText("Môn: " + post.getSportType());
        holder.tvPosterName.setText("Người đăng: " + post.getPosterName());
        holder.tvPlayDateTime.setText("Thời gian: " + post.getTimeSlot() + " - " + post.getPlayDate());
        holder.tvPlayersNeeded.setText("Cần tìm: " + post.getPlayersNeeded() + " người");
        holder.tvDescription.setText("Mô tả: " + post.getDescription());

        // Kiểm tra quyền sở hữu để hiển thị/ẩn nút
        if (post.getUserId() == currentUserId) {
            holder.ownerActionsLayout.setVisibility(View.VISIBLE);
            holder.btnJoin.setVisibility(View.GONE);
        } else {
            holder.ownerActionsLayout.setVisibility(View.GONE);
            holder.btnJoin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvSportType, tvPosterName, tvPlayDateTime, tvPlayersNeeded, tvDescription;
        Button btnJoin, btnEdit, btnDelete;
        LinearLayout ownerActionsLayout;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSportType = itemView.findViewById(R.id.tvSportType);
            tvPosterName = itemView.findViewById(R.id.tvPosterName);
            tvPlayDateTime = itemView.findViewById(R.id.tvPlayDateTime);
            tvPlayersNeeded = itemView.findViewById(R.id.tvPlayersNeeded);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            btnJoin = itemView.findViewById(R.id.btnJoin);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            ownerActionsLayout = itemView.findViewById(R.id.ownerActionsLayout);

            btnJoin.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (joinListener != null && position != RecyclerView.NO_POSITION) {
                    joinListener.onJoinClick(postList.get(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (deleteListener != null && position != RecyclerView.NO_POSITION) {
                    deleteListener.onDeleteClick(postList.get(position), position);
                }
            });

            // Set listener cho nút Edit
            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (editListener != null && position != RecyclerView.NO_POSITION) {
                    editListener.onEditClick(postList.get(position));
                }
            });
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (itemClickListener != null && position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(postList.get(position));
                }
            });
        }
    }
}