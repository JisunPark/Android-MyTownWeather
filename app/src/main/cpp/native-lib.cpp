#include <jni.h>
#include <string>
#include "convert_grid.h"

extern "C"
JNIEXPORT jintArray JNICALL Java_com_suminjin_mytownweather_SearchActivity_convertLocation(
        JNIEnv *env,
        jobject /* this */, jdouble a, jdouble b) {

    int *xy = convert_xy(0, a, b); // 0 : 위경도->격자    1: 격자->위경도
    LOGE("x %d", xy[0]);
    LOGE("y %d", xy[1]);

    int size = 2;
    jintArray result = env->NewIntArray(size);

    // move from the temp structure to the java structure
    env->SetIntArrayRegion(result, 0, size, xy);
    return result;
}
