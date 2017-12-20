package com.example.shakil.restaurantfoodorder.Model;

import java.util.List;

/**
 * Created by shaki on 11/7/2017.
 */

public class MyResponse {
    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;
}
