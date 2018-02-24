package com.example.shakil.restaurantfoodorder.Remote;

import com.example.shakil.restaurantfoodorder.Model.MyResponse;
import com.example.shakil.restaurantfoodorder.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by shaki on 11/7/2017.
 */

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAFC7XmiI:APA91bFrX03SaR0k1GlLfxgF-JvfYrzwopSSk4wx26_SB3cxB8APID4FSbi-5xd30uYmyeVnz8k0ohhCbIB4Rr0qPNblBid50lyIJMZkuxj9ZaP5KHxyFieQPc1zeBw0TWq196fptJv_"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
