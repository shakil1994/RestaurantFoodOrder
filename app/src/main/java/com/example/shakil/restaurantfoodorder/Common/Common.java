package com.example.shakil.restaurantfoodorder.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.shakil.restaurantfoodorder.Model.User;
import com.example.shakil.restaurantfoodorder.Remote.APIService;
import com.example.shakil.restaurantfoodorder.Remote.RetrofitClient;

/**
 * Created by shaki on 11/2/2017.
 */

public class Common {
    public static User currentUser;

    //public static String PHONE_TEXT = "userPhone";

    //public static final String INTENT_FOOD_ID = "FoodId";

    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);

    }


    public static String convertCodeToStatus(String status){
        if (status.equals("0")){
            return "Placed";
        }
        else if (status.equals("1")){
            return "On my way";
        }
        else {
            return "Shipped";
        }
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            if (info != null){
                for (int i = 0; i < info.length; i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
