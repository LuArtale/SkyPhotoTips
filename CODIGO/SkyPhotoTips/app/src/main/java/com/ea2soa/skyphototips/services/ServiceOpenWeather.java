package com.ea2soa.skyphototips.services;

import com.ea2soa.skyphototips.dto.ResponseGetWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceOpenWeather {

    @GET("onecall")
    Call<ResponseGetWeather> getWeatherForecast(@Query("lat") String lat,
                                                @Query("lon") String lon,
                                                @Query("exclude") String exclude,
                                                @Query("units") String units,
                                                @Query("appid") String appid);
}
