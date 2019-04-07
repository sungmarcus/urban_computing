package com.example.sungm.urbanapp;


import com.example.sungm.urbanapp.objects.LuasStation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LuasService {
    @GET("xml/get.ashx?")
    Call<LuasStation> getStation(@Query("action") String action, @Query("stop") String stop, @Query("encrypt") String encrypt);
}