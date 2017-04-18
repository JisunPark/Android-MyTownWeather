//
// Created by ParkJisun on 2017. 4. 18..
//

#include "convert_grid.h"

int *convert_xy(int code, float a, float b) {
    float lon, lat, x, y;
    struct lamc_parameter map;

    if (code == 1) {
        x = a;
        y = b;
        if (x < 1 || x > NX || y < 1 || y > NY) {
            LOGE("X-grid range [1,%d] / Y-grid range [1,%d]\n", NX, NY);
            exit(0);
        }
    } else if (code == 0) {
        lon = a;
        lat = b;
    }

    //
    //  동네예보 지도 정보
    //
    map.Re = 6371.00877;     // 지도반경
    map.grid = 5.0;            // 격자간격 (km)
    map.slat1 = 30.0;           // 표준위도 1
    map.slat2 = 60.0;           // 표준위도 2
    map.olon = 126.0;          // 기준점 경도
    map.olat = 38.0;           // 기준점 위도
    map.xo = 210 / map.grid;   // 기준점 X좌표
    map.yo = 675 / map.grid;   // 기준점 Y좌표
    map.first = 0;

    //
    //  동네예보
    //
    map_conv(&lon, &lat, &x, &y, code, map); // 0 : 위경도->격자    1: 격자->위경도

    static int result[2];
    if (code) {
        LOGI("X = %d, Y = %d  --->lon.= %f, lat.= %f\n", (int) x, (int) y, lon, lat);
        result[0] = lon;
        result[1] = lat;
    } else {
        LOGI("lon.= %f, lat.= %f ---> X = %d, Y = %d\n", lon, lat, (int) x, (int) y);
        result[0] = x;
        result[1] = y;
    }

    return result;
}

/*============================================================================*
 *  좌표변환
 *============================================================================*/
int
map_conv
        (
                float *lon,                    // 경도(degree)
                float *lat,                    // 위도(degree)
                float *x,                      // X격자 (grid)
                float *y,                      // Y격자 (grid)
                int code,                    // 0 (격자->위경도), 1 (위경도->격자)
                struct lamc_parameter map       // 지도정보
        ) {
    float lon1, lat1, x1, y1;

    //
    //  위경도 -> (X,Y)
    //
    if (code == 0) {
        lon1 = *lon;
        lat1 = *lat;
        lamcproj(&lon1, &lat1, &x1, &y1, 0, &map);
        *x = (int) (x1 + 1.5);
        *y = (int) (y1 + 1.5);
    }

    //
    //  (X,Y) -> 위경도
    //
    if (code == 1) {
        x1 = *x - 1;
        y1 = *y - 1;
        lamcproj(&lon1, &lat1, &x1, &y1, 1, &map);
        *lon = lon1;
        *lat = lat1;
    }
    return 0;
}

/***************************************************************************
*
*  [ Lambert Conformal Conic Projection ]
*
*      olon, lat : (longitude,latitude) at earth  [degree]
*      o x, y     : (x,y) cordinate in map  [grid]
*      o code = 0 : (lon,lat) --> (x,y)
*               1 : (x,y) --> (lon,lat)
*
***************************************************************************/

int lamcproj(float *lon,                    // 경도(degree)
             float *lat,                    // 위도(degree)
             float *x,                      // X격자 (grid)
             float *y,                      // Y격자 (grid)
             int code,                    // 0 (격자->위경도), 1 (위경도->격자)
             struct lamc_parameter *map       // 지도정보
) {
    static double PI, DEGRAD, RADDEG;
    static double re, olon, olat, sn, sf, ro;
    double slat1, slat2, alon, alat, xn, yn, ra, theta;

    if ((*map).first == 0) {
        PI = asin(1.0) * 2.0;
        DEGRAD = PI / 180.0;
        RADDEG = 180.0 / PI;

        re = (*map).Re / (*map).grid;
        slat1 = (*map).slat1 * DEGRAD;
        slat2 = (*map).slat2 * DEGRAD;
        olon = (*map).olon * DEGRAD;
        olat = (*map).olat * DEGRAD;

        sn = tan(PI * 0.25 + slat2 * 0.5) / tan(PI * 0.25 + slat1 * 0.5);
        sn = log(cos(slat1) / cos(slat2)) / log(sn);
        sf = tan(PI * 0.25 + slat1 * 0.5);
        sf = pow(sf, sn) * cos(slat1) / sn;
        ro = tan(PI * 0.25 + olat * 0.5);
        ro = re * sf / pow(ro, sn);
        (*map).first = 1;
    }

    if (code == 0) {
        ra = tan(PI * 0.25 + (*lat) * DEGRAD * 0.5);
        ra = re * sf / pow(ra, sn);
        theta = (*lon) * DEGRAD - olon;
        if (theta > PI) theta -= 2.0 * PI;
        if (theta < -PI) theta += 2.0 * PI;
        theta *= sn;
        *x = (float) (ra * sin(theta)) + (*map).xo;
        *y = (float) (ro - ra * cos(theta)) + (*map).yo;
    } else {
        xn = *x - (*map).xo;
        yn = ro - *y + (*map).yo;
        ra = sqrt(xn * xn + yn * yn);
        if (sn < 0.0) -ra;
        alat = pow((re * sf / ra), (1.0 / sn));
        alat = 2.0 * atan(alat) - PI * 0.5;
        if (fabs(xn) <= 0.0) {
            theta = 0.0;
        } else {
            if (fabs(yn) <= 0.0) {
                theta = PI * 0.5;
                if (xn < 0.0) -theta;
            } else
                theta = atan2(xn, yn);
        }
        alon = theta / sn + olon;
        *lat = (float) (alat * RADDEG);
        *lon = (float) (alon * RADDEG);
    }
    return 0;
}

