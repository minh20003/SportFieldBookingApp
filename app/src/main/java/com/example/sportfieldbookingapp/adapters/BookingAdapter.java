package com.example.sportfieldbookingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.models.Booking;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final List<Booking> bookingList;
    private OnReviewButtonClickListener listener;

    /**
     * Interface (hợp đồng) để Activity có thể lắng nghe sự kiện
     * khi người dùng nhấn nút "Đánh giá".
     */
    public interface OnReviewButtonClickListener {
        void onReviewClick(Booking booking);
    }

    /**
     * Hàm để Activity có thể truyền listener vào cho adapter.
     */
    public void setOnReviewButtonClickListener(OnReviewButtonClickListener listener) {
        this.listener = listener;
    }

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // Gán dữ liệu cho các TextView
        holder.tvFieldName.setText(booking.getFieldName());
        String dateTime = "Ngày: " + booking.getBookingDate() + " - Giờ: " + booking.getTimeSlotStart();
        holder.tvDateTime.setText(dateTime);
        String status = "Trạng thái: " + booking.getStatus();
        holder.tvStatus.setText(status);

        // Kiểm tra trạng thái đơn hàng để hiển thị hoặc ẩn nút "Đánh giá"
        if ("completed".equalsIgnoreCase(booking.getStatus())) {
            holder.btnReview.setVisibility(View.VISIBLE);
        } else {
            holder.btnReview.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvFieldName, tvDateTime, tvStatus;
        Button btnReview;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.tvBookingFieldName);
            tvDateTime = itemView.findViewById(R.id.tvBookingDateTime);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
            btnReview = itemView.findViewById(R.id.btnReview);

            // Set sự kiện click cho nút "Đánh giá"
            btnReview.setOnClickListener(v -> {
                int position = getAdapterPosition();
                // Đảm bảo listener không null và vị trí hợp lệ
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    // Gọi hàm trong interface, truyền vào đối tượng booking tương ứng
                    listener.onReviewClick(bookingList.get(position));
                }
            });
        }
    }
}