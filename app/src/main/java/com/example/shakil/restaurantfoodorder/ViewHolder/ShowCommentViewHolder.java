package com.example.shakil.restaurantfoodorder.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.shakil.restaurantfoodorder.R;

/**
 * Created by shakil on 1/31/2018.
 */

public class ShowCommentViewHolder extends RecyclerView.ViewHolder {

    public TextView txtUserPhone, txtComment;
    public RatingBar ratingBar;

    public ShowCommentViewHolder(View itemView) {
        super(itemView);
        txtUserPhone = itemView.findViewById(R.id.txtUserPhone);
        txtComment = itemView.findViewById(R.id.txtComment);
        ratingBar = itemView.findViewById(R.id.ratingBar);
    }
}
