package com.ipa.sample.common;

import com.fasterxml.jackson.annotation.JsonProperty;
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

        @JsonProperty("cityid")
        private String cityId;

        private String temp;

        private String SD;

        private String AP;

        private String WSE;

        private String time;

        private String sm;
    }
}
