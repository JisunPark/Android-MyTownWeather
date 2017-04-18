//
// Created by ParkJisun on 2017. 4. 18..
//
#ifndef TESTNDK_CONVERT_GRID_H
#define TESTNDK_CONVERT_GRID_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <dirent.h>
#include <time.h>
#include <math.h>
#include <android/log.h>

#define NX  149     /* X축 격자점 수 */
#define NY  253     /* Y축 격자점 수 */

#define  LOG_TAG    "jisunLog"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#ifdef __cplusplus
extern "C" {
#endif

struct lamc_parameter {
    float Re;          /* 사용할 지구반경 [ km ]      */
    float grid;        /* 격자간격        [ km ]      */
    float slat1;       /* 표준위도        [degree]    */
    float slat2;       /* 표준위도        [degree]    */
    float olon;        /* 기준점의 경도   [degree]    */
    float olat;        /* 기준점의 위도   [degree]    */
    float xo;          /* 기준점의 X좌표  [격자거리]  */
    float yo;          /* 기준점의 Y좌표  [격자거리]  */
    int first;       /* 시작여부 (0 = 시작)         */
};

int *convert_xy(int code, float a, float b);

int map_conv
        (
                float *lon,                    // 경도(degree)
                float *lat,                    // 위도(degree)
                float *x,                      // X격자 (grid)
                float *y,                      // Y격자 (grid)
                int code,                    // 0 (격자->위경도), 1 (위경도->격자)
                struct lamc_parameter map       // 지도정보
        );

int lamcproj(
        float *lon,                    // 경도(degree)
        float *lat,                    // 위도(degree)
        float *x,                      // X격자 (grid)
        float *y,                      // Y격자 (grid)
        int code,                    // 0 (격자->위경도), 1 (위경도->격자)
        struct lamc_parameter *map       // 지도정보
);

#ifdef __cplusplus
}
#endif

#endif //TESTNDK_CONVERT_GRID_H
