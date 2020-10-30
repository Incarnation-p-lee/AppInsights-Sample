package com.ipa.sample.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Weather {

    private WeatherInfo weatherinfo;

    @Getter
    @Setter
    public static class WeatherInfo {

        private String city;

        private String cityId;

        private String temp;

        private String wd;

        private String ws;

        private String sd;

        private String ap;

        private String njd;

        private String wse;

        private String time;

        private String sm;

        private String isRadar;

        private String radar;
    }
}
