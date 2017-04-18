package com.suminjin.data;

/**
 * Created by parkjisun on 2017. 4. 17..
 */

public enum ApiType {
    FORECAST_GRIB(R.string.forecast_grib, "ForecastGrib"),
    FORECAST_TIME_DATA(R.string.forecast_time_data, "ForecastTimeData"),
    FORECAST_SPACE_DATA(R.string.forecast_space_data, "ForecastSpaceData");

    public int nameResId;
    public String path;

    ApiType(int nameResId, String path) {
        this.nameResId = nameResId;
        this.path = ServerConfig.BASE_PATH + path;
    }
}
