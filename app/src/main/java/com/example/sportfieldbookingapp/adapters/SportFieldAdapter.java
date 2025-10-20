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

    private Context context;
    private List<SportField> fieldList;

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
            // Lưu ý: URL ảnh thực tế sẽ cần được xử lý sau này
            // Tạm thời chúng ta sẽ hiển thị ảnh mặc định
            Glide.with(context)
                    .load(field.getImages().get(0)) // Lấy ảnh đầu tiên
                    .placeholder(R.mipmap.ic_launcher) // Ảnh hiển thị trong khi chờ tải
                    .error(R.mipmap.ic_launcher_round) // Ảnh hiển thị nếu tải lỗi
                    .into(holder.ivFieldImage);
        }
    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    // Lớp ViewHolder để chứa các view của một item
    public static class SportFieldViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFieldImage;
        TextView tvFieldName, tvFieldAddress;

        public SportFieldViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFieldImage = itemView.findViewById(R.id.ivFieldImage);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvFieldAddress = itemView.findViewById(R.id.tvFieldAddress);
        }
    }
}