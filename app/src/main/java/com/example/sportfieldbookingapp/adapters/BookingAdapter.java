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
    private OnReviewButtonClickListener reviewListener;
    private OnCancelButtonClickListener cancelListener; // Listener mới cho nút Hủy

    // Interfaces cho các sự kiện click
    public interface OnReviewButtonClickListener { void onReviewClick(Booking booking); }
    public interface OnCancelButtonClickListener { void onCancelClick(Booking booking, int position); } // Thêm position

    // Hàm set listener
    public void setOnReviewButtonClickListener(OnReviewButtonClickListener listener) { this.reviewListener = listener; }
    public void setOnCancelButtonClickListener(OnCancelButtonClickListener listener) { this.cancelListener = listener; } // Thêm hàm set

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
        holder.tvFieldName.setText(booking.getFieldName());
        String dateTime = "Ngày: " + booking.getBookingDate() + " - Giờ: " + booking.getTimeSlotStart();
        holder.tvDateTime.setText(dateTime);
        String status = "Trạng thái: " + booking.getStatus();
        holder.tvStatus.setText(status);

        // Hiển thị nút "Đánh giá" nếu đơn hàng đã "completed"
        if ("completed".equalsIgnoreCase(booking.getStatus())) {
            holder.btnReview.setVisibility(View.VISIBLE);
        } else {
            holder.btnReview.setVisibility(View.GONE);
        }

        // Hiển thị nút "Hủy Đơn" nếu trạng thái là 'pending' hoặc 'confirmed'
        if ("pending".equalsIgnoreCase(booking.getStatus()) || "confirmed".equalsIgnoreCase(booking.getStatus())) {
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvFieldName, tvDateTime, tvStatus;
        Button btnReview, btnCancel; // Thêm btnCancel

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.tvBookingFieldName);
            tvDateTime = itemView.findViewById(R.id.tvBookingDateTime);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
            btnReview = itemView.findViewById(R.id.btnReview);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking); // Ánh xạ nút Hủy

            // Sự kiện click nút Đánh giá (giữ nguyên)
            btnReview.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (reviewListener != null && position != RecyclerView.NO_POSITION) {
                    reviewListener.onReviewClick(bookingList.get(position));
                }
            });

            // Sự kiện click nút Hủy
            btnCancel.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (cancelListener != null && position != RecyclerView.NO_POSITION) {
                    cancelListener.onCancelClick(bookingList.get(position), position); // Gọi listener Hủy
                }
            });
        }
    }
}