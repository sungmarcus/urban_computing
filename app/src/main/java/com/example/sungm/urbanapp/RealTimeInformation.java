package com.example.sungm.urbanapp;

import com.example.sungm.urbanapp.objects.LuasStation;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Response;

public class RealTimeInformation {
    private static LuasStation luasStation;
    private String apiKey;


    public static LuasStation returnLuasInfo(String stop) {
        final String BASE_URL = "http://luasforecasts.rpa.ie/";
        String encrypt = "false";
        String action = "forecast";
        final CountDownLatch latch = new CountDownLatch(1);
        LuasService service = ApiRetroFit.getRetrofitInstanceXML(BASE_URL).create(LuasService.class);
        Call<LuasStation> call = service.getStation(action, stop, encrypt);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<LuasStation> response = call.execute();
                    luasStation = response.body();
                    latch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return luasStation;
    }
}