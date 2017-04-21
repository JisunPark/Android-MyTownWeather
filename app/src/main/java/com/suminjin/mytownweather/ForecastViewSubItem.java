package com.suminjin.mytownweather;

import com.suminjin.data.ApiType;
import com.suminjin.data.DataCode;
import com.suminjin.data.DataCodeBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by parkjisun on 2017. 4. 19..
 */

public class ForecastViewSubItem {
    public String code; // code == category
    public String date;
    public String time;
    public String value;

    public String codeString; // 코드에 대한 사용자용 명칭
    public int viewType = -1; // 코드별로 다르게 그리기 위한 구분자
    public DataCode dataCode = null; // DataCode Enum에서 해당하는 것
    public String dataString = ""; // 데이타를 사용자용으로 변환한 것

    public ForecastViewSubItem(ApiType apiType, String code, String date, String time, String value) {
        this.code = code;
        this.date = date == null ? "" : date;
        this.time = time == null ? "" : time;
        this.value = value;
        this.codeString = code;

        dataCode = getDataCode(code);
        if (dataCode != null) {
            codeString = dataCode.dataName;
            viewType = dataCode.ordinal(); // RecyclerViewAdapter에 viewType이 integer라 ordinal을 전달함
            dataString = DataCodeBuilder.getDataValue(apiType, dataCode, value);

        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (dataCode == null) {
            sb.append(code)
                    .append("(").append(getFormattedTimeString()).append(")")
                    .append(" : ").append(value).append("\n");
        } else {
            sb.append(dataCode.dataName).append("] ")
                    .append("(").append(getFormattedTimeString()).append(")").append(" : ")
                    .append(dataString).append("\n");
        }

        return sb.toString();
    }

    public String getFormattedDateString() {
        String result = date;
        if (!time.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
            try {
                Date d = sdf.parse(date);
                result = sdf2.format(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 시간
     *
     * @return
     */
    public String getFormattedTimeString() {
        String result = time;
        if (!time.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("HHmm", Locale.getDefault());
            SimpleDateFormat sdf2 = new SimpleDateFormat("a hh:mm", Locale.getDefault());
            try {
                Date date = sdf.parse(time);
                result = sdf2.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
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
