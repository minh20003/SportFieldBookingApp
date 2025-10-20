package com.example.sportfieldbookingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportfieldbookingapp.R;
import com.example.sportfieldbookingapp.models.Booking;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;

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
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvFieldName, tvDateTime, tvStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.tvBookingFieldName);
            tvDateTime = itemView.findViewById(R.id.tvBookingDateTime);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
        }
    }
}