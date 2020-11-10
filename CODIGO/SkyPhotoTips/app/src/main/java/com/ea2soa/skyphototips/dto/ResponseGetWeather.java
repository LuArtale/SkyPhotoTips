package com.ea2soa.skyphototips.dto;

import java.util.Arrays;
import java.util.List;

public class ResponseGetWeather {

    private Double lat;
    private Double lon;
    private String timezone;
    private DailyWeather[] daily;


    @Override
    public String toString() {
        return "ResponseGetWeather{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", timezone='" + timezone + '\'' +
                ", daily=" + Arrays.toString(daily) +
                '}';
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public DailyWeather[] getDaily() {
        return daily;
    }

    public void setDaily(DailyWeather[] daily) {
        this.daily = daily;
    }
}
