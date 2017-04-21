package com.suminjin.data;

/**
 * Created by parkjisun on 2017. 4. 17..
 */
public enum JsonField {
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
    TOTAL_COUNT("totalCount"),
    BASE_DATE("baseDate"),
    BASE_TIME("baseTime");

    public String name;

    JsonField() {
        name = this.name().toLowerCase();
    }

    JsonField(String name) {
        this.name = name;
    }
}

