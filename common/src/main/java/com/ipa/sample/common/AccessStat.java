package com.ipa.sample.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AccessStat {

    private Date accessDate;

    private long timeCostInMilliSeconds;

    private Weather weather;
}
