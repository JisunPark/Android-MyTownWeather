package com.suminjin.data;

/**
 * Created by parkjisun on 2017. 4. 17..
 */

public enum DataCode {
    T1H("기온", "°C", -50, 10),
    RN1("1시간 강수량", "mm", -1, 8),
    SKY("하늘상태", null, -1, 4),
    UUU("동서바람성분", "m/s", -100, 12),
    VVV("남북바람성분", "m/s", -100, 12),
    REH("습도", "%", -1, 8),
    PTY("강수형태", null, -1, 4),
    LGT("낙뢰", null, -1, 4),
    WSD("풍속", "l", -1, 10),
    POP("강수확률", "%", -1, 8),
    R06("6시간 강수", "mm", -1, 8),
    S06("6시간 신적설", "범주(1cm", -1, 8 ),
    T3H("3시간 기", "°C", -50, 10),
    TMN("아침 최저기온", "°C", -50, 10),
    TMX("낮 최고기", "°C", -50, 10),
    WAV("파고", "M", -1, 8),
    VEC("풍향", "m/s", -1, 10);

    public String dataName;
    public String unit;
    public int missing;
    public int compressionBitCount;

    DataCode(String dataName, String unit, int missing, int compressionBitCount) {
        this.dataName = dataName;
//        this.unit = unit == null ? name().toLowerCase() : unit;
        this.unit = unit == null ? "" : unit;
        this.missing = missing;
        this.compressionBitCount = compressionBitCount;
    }
}
