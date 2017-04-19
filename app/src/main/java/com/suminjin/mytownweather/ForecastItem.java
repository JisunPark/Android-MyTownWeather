package com.suminjin.mytownweather;

/**
 * Created by parkjisun on 2017. 4. 19..
 */

public class ForecastItem {
    public String index;
    public String code;
    public String codeString;
    public String data;
    public String dataString;

    public ForecastItem(int index, String code, String codeString, String data, String dataString) {
        this.index = String.format("[%02d ", index + 1);
        this.code = code + "] ";
        this.codeString = codeString + ": ";
        this.data = " (" + data + ")";
        this.dataString = dataString;
    }
}
