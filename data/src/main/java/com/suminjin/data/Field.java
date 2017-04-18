package com.suminjin.data;

/**
 * Created by parkjisun on 2017. 4. 17..
 */
public enum Field {
    RESPONSE,
    RESULT_CODE("resultCode"),
    RESULT_MSG("resultMsg"),
    HEADER,
    BODY,
    ITEMS,
    ITEM,
    CATEGORY,
    OBSR_VALUE("obsrValue"),
    FCST_TIME("fcstTime"),
    FCST_VALUE("fcstValue"),
    TOTAL_COUNT("totalCount");

    public String name;

    Field() {
        name = this.name().toLowerCase();
    }

    Field(String name) {
        this.name = name;
    }
}

