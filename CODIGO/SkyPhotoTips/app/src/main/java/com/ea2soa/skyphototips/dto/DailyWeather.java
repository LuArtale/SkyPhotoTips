package com.ea2soa.skyphototips.dto;

public class DailyWeather {

    private Long dt;
    private Weather[] weather;
    private Integer clouds;
    private Float visibility;
    private Float pop;
    private Float rain;


    @Override
    public String toString() {
        return "DailyWeather{" +
                "dt=" + dt +
                ", weather=" + weather +
                ", clouds=" + clouds +
                ", visibility=" + visibility +
                ", pop=" + pop +
                ", rain=" + rain +
                '}';
    }

    public Long getDt() {
        return dt;
    }

    public void setDt(Long dt) {
        this.dt = dt;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Integer getClouds() {
        return clouds;
    }

    public void setClouds(Integer clouds) {
        this.clouds = clouds;
    }

    public Float getVisibility() {
        return visibility;
    }

    public void setVisibility(Float visibility) {
        this.visibility = visibility;
    }

    public Float getPop() {
        return pop;
    }

    public void setPop(Float pop) {
        this.pop = pop;
    }

    public Float getRain() {
        return rain;
    }

    public void setRain(Float rain) {
        this.rain = rain;
    }
}
