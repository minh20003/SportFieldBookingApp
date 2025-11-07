package com.example.sportfieldbookingapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.models.TimeSlot;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<TimeSlot> timeSlotList;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnItemClickListener listener;

    // Interface cho click listener
    public interface OnItemClickListener {
        void onItemClick(TimeSlot timeSlot);
    }

    // Constructor nhận List<TimeSlot>
    public TimeSlotAdapter(List<TimeSlot> timeSlotList) {
        this.timeSlotList = timeSlotList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot slot = timeSlotList.get(position);

        // Hiển thị thời gian
        holder.tvTimeSlot.setText(slot.getTimeSlot());

        // Format giá tiền
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceText = format.format(slot.getPrice());
        holder.tvPrice.setText(priceText);

        // Xác định trạng thái và màu sắc
        if (!slot.isAvailable()) {
            // Khung giờ đã được đặt - MÀU XÁM
            holder.cardView.setCardBackgroundColor(Color.parseColor("#CCCCCC"));
            holder.tvTimeSlot.setTextColor(Color.parseColor("#666666"));
            holder.tvPrice.setTextColor(Color.parseColor("#666666"));
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText("Đã đặt");
            holder.tvStatus.setTextColor(Color.parseColor("#FF0000"));
            holder.cardView.setEnabled(false);
            holder.cardView.setClickable(false);

        } else if (selectedPosition == position) {
            // Khung giờ được chọn - MÀU XANH
            holder.cardView.setCardBackgroundColor(Color.parseColor("#4CAF50"));
            holder.tvTimeSlot.setTextColor(Color.WHITE);
            holder.tvPrice.setTextColor(Color.WHITE);
            holder.tvStatus.setVisibility(View.GONE);

        } else {
            // Khung giờ available - MÀU TRẮNG
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.tvTimeSlot.setTextColor(Color.BLACK);
            holder.tvPrice.setTextColor(Color.parseColor("#666666"));
            holder.tvStatus.setVisibility(View.GONE);
        }

        // Click listener
        holder.cardView.setOnClickListener(v -> {
            if (slot.isAvailable()) {
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                // Cập nhật hiển thị
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);

                // Gọi listener
                if (listener != null) {
                    listener.onItemClick(slot);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlotList != null ? timeSlotList.size() : 0;
    }

    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTimeSlot;
        TextView tvPrice;
        TextView tvStatus;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewTimeSlot);
            tvTimeSlot = itemView.findViewById(R.id.tvTimeSlot);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}