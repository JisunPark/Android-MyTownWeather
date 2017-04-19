package com.suminjin.data;

import java.util.HashMap;

/**
 * Created by parkjisun on 2017. 4. 19..
 */

public class DataCodeBuilder {

    private static String[] skyCodes = new String[]{"맑음", "구름조금", "구름많음", "흐림"};
    private static String[] ptyCodes = new String[]{"없음", "비", "비/눈", "눈"};
    private static String[] gribLgtCodes = new String[]{"없음", "있음"};
    private static String[] timeLgtCodes = new String[]{"확률없음", "낮음", "보통", "높음"};

    private static String[] windDirection = new String[]{
            "N", "NNE", "NE", "ENE",
            "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW",
            "W", "WNW", "NW", "NNW",
            "N"};

    private static HashMap<Integer, String> rainfallData;
    private static HashMap<Integer, String> snowData;

    /**
     * 강우량 코드
     *
     * @param key
     * @return
     */
    private static String getRainfallString(int key) {
        if (rainfallData == null) {
            rainfallData = new HashMap<>();
            rainfallData.put(0, "없음");
            rainfallData.put(1, "1mm 미만");
            rainfallData.put(5, "1~4mm");
            rainfallData.put(10, "5~9mm");
            rainfallData.put(20, "10~19mm");
            rainfallData.put(40, "20~39mm");
            rainfallData.put(70, "40~69mm");
            rainfallData.put(100, "70mm 이상");
        }
        return rainfallData.containsKey(key) ? rainfallData.get(key) : "정보 없음(code " + key + ")";
    }

    private static String getSnowString(int key) {
        if (snowData == null) {
            snowData = new HashMap<>();
            snowData.put(0, "없음");
            snowData.put(1, "1cm 미만");
            snowData.put(5, "1~4cm");
            snowData.put(10, "5~9cm");
            snowData.put(20, "10~19cm");
            snowData.put(100, "20cm 이상");
        }
        return snowData.containsKey(key) ? snowData.get(key) : "정보 없음(code " + key + ")";
    }

    /**
     * 풍향 코드
     *
     * @param value
     * @return
     */

    private static String getWindDirectionString(int value) {
        int key = (int) ((value + 22.5 * 0.5) / 22.5);
        return (key >= 0 && key < windDirection.length) ? windDirection[key] : "";
    }

    /**
     * 코드 array에서 코드값에 해당하는 string을 가져온다.
     *
     * @param valueStr
     * @param valueArray
     * @return
     */
    private static String getValidValueCode(String valueStr, String[] valueArray) {
        String result = valueStr;
        int val = Integer.parseInt(valueStr);
        if (val >= 0 && val < valueArray.length) {
            result = valueArray[Integer.parseInt(valueStr)];
        }
        return result;
    }

    /**
     * 풍속 통보문
     *
     * @param value
     * @return
     */
    private static String getWindSpeedString(float value) {
        String result = "";
        if (value > 4 && value < 9) {
            result = "(약간강)";
        } else if (value >= 9 && value < 14) {
            result = "(강)";
        } else if (value >= 14) {
            result = "(매우강)";
        }
        return result;
    }

    /**
     * windDirection code에 따라 값을 표시
     *
     * @param apiType
     * @param dataCode
     * @param valueStr
     * @return
     */
    public static String getDataValue(ApiType apiType, DataCode dataCode, String valueStr) {
        String result = valueStr;
        switch (dataCode) {
            case SKY:
                int val = Integer.parseInt(valueStr);
                // sky code는 1~4
                if (val > 0 && val <= skyCodes.length) {
                    result = skyCodes[Integer.parseInt(valueStr) - 1];
                }
                break;
            case PTY:
                result = getValidValueCode(valueStr, ptyCodes);
                break;
            case RN1:
            case R06:
                result = getRainfallString((int) Float.parseFloat(valueStr));
                break;
            case S06:
                result = getSnowString(Integer.parseInt(valueStr));
                break;
            case LGT:
                if (apiType.equals(ApiType.FORECAST_GRIB)) { // 초단기 실황
                    result = getValidValueCode(valueStr, gribLgtCodes);
                } else { // 초단기 예보
                    result = getValidValueCode(valueStr, timeLgtCodes);
                }
                break;
            case UUU:
                float uuuValue = Float.parseFloat(valueStr);
                if (uuuValue > 0) {
                    result = "동 " + Math.abs(uuuValue);
                } else if (uuuValue < 0) {
                    result = "서 " + Math.abs(uuuValue);
                }
                result = result + " " + dataCode.unit;
                break;
            case VVV:
                float vvvValue = Float.parseFloat(valueStr);
                if (vvvValue > 0) {
                    result = "북 " + Math.abs(vvvValue);
                } else if (vvvValue < 0) {
                    result = "남 " + Math.abs(vvvValue);
                }
                result = result + " " + dataCode.unit;
                break;
            case VEC:
                result = getWindDirectionString(Integer.parseInt(valueStr));
                break;
            case WSD:
                result = result + " " + dataCode.unit + getWindSpeedString(Float.parseFloat(valueStr));
                break;
            default:
                result = result + " " + dataCode.unit;
        }
        return result;
    }

}
