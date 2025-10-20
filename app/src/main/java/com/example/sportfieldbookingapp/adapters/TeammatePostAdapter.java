package com.example.sportfieldbookingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.models.TeammatePost;
import java.util.List;

public class TeammatePostAdapter extends RecyclerView.Adapter<TeammatePostAdapter.PostViewHolder> {

    private List<TeammatePost> postList;

    public TeammatePostAdapter(List<TeammatePost> postList) {
        this.postList = postList;
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
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvSportType, tvPosterName, tvPlayDateTime, tvPlayersNeeded, tvDescription;
        Button btnJoin;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSportType = itemView.findViewById(R.id.tvSportType);
            tvPosterName = itemView.findViewById(R.id.tvPosterName);
            tvPlayDateTime = itemView.findViewById(R.id.tvPlayDateTime);
            tvPlayersNeeded = itemView.findViewById(R.id.tvPlayersNeeded);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnJoin = itemView.findViewById(R.id.btnJoin);
        }
    }
}