package com.example.shakil.restaurantfoodorder.Interface;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Shakil on 5/1/2018.
 */

public interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
