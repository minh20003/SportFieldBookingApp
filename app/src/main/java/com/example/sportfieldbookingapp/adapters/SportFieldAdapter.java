package com.example.sportfieldbookingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.models.SportField;

import java.util.List;

public class SportFieldAdapter extends RecyclerView.Adapter<SportFieldAdapter.SportFieldViewHolder> {

    private final Context context;
    private final List<SportField> fieldList;
    private OnItemClickListener listener;

    /**
     * Interface để xử lý sự kiện click trên một item
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * Hàm để Activity có thể set listener cho adapter
     * @param listener The listener that will be triggered on item clicks
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Constructor của Adapter
     */
    public SportFieldAdapter(Context context, List<SportField> fieldList) {
        this.context = context;
        this.fieldList = fieldList;
    }

    @NonNull
    @Override
    public SportFieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sport_field, parent, false);
        return new SportFieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SportFieldViewHolder holder, int position) {
        SportField field = fieldList.get(position);
        holder.tvFieldName.setText(field.getName());
        holder.tvFieldAddress.setText(field.getAddress());

        // Dùng Glide để load ảnh đầu tiên từ danh sách ảnh (nếu có)
        if (field.getImages() != null && !field.getImages().isEmpty()) {
            // Lưu ý: URL ảnh này là giả định, bạn cần thay thế bằng URL thật từ server
            Glide.with(context)
                    .load(field.getImages().get(0))
                    .placeholder(R.mipmap.ic_launcher) // Ảnh hiển thị trong khi chờ tải
                    .error(R.mipmap.ic_launcher_round) // Ảnh hiển thị nếu tải lỗi
                    .into(holder.ivFieldImage);
        }
    }

    @Override
    public int getItemCount() {
        return fieldList != null ? fieldList.size() : 0;
    }

    /**
     * Lớp ViewHolder để chứa và quản lý các View của một item
     */
    public class SportFieldViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFieldImage;
        TextView tvFieldName, tvFieldAddress;

        public SportFieldViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFieldImage = itemView.findViewById(R.id.ivFieldImage);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvFieldAddress = itemView.findViewById(R.id.tvFieldAddress);

            // Xử lý sự kiện click cho toàn bộ item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}