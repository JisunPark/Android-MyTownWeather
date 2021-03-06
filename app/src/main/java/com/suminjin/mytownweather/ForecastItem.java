package com.suminjin.mytownweather;

import com.suminjin.data.DataCode;

import java.util.ArrayList;

/**
 * Created by parkjisun on 2017. 4. 21..
 */

public class ForecastItem {
    public String code;
    public int viewType = -1;
    public final DataCode dataCode;

    public ArrayList<ForecastSubItem> list = new ArrayList<>();

    public ForecastItem(String code) {
        this.code = code;
        dataCode = getDataCode(code);
        if (dataCode != null) {
            viewType = dataCode.ordinal();
        }
    }

    /**
     * @param categoryCode
     * @return
     */
    private DataCode getDataCode(String categoryCode) {
        DataCode dataCode = null;
        for (DataCode d : DataCode.values()) {
            if (d.name().equalsIgnoreCase(categoryCode)) {
                dataCode = d;
                break;
            }
        }
        return dataCode;
    }
}
