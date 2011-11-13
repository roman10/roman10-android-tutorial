/*android specific headers*/
#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
/*standard library*/
#include <time.h>
#include <math.h>
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>
#include <unistd.h>
#include <assert.h>

#include "yuv2rgb/yuv2rgb.h"

#define LOG_TAG "yuv2rgb test"
#define LOG_LEVEL 10
#define LOGI(level, ...) if (level <= LOG_LEVEL) {__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__);}
#define LOGE(level, ...) if (level <= LOG_LEVEL) {__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__);}

/*fill in data for a bitmap*/
JNIEXPORT void JNICALL Java_roman10_tutorial_media_colorconversion_RenderView_naGetConvertedFrame(JNIEnv * pEnv, jobject pObj, jobject pBitmap, jstring pFileName, int _width, int _height) {
	int lRet;
    AndroidBitmapInfo lInfo;
	char* l_videoFileName;
	void* l_Bitmap;
	unsigned char y[_width*_height];
	unsigned char u[_width*_height>>2];
    unsigned char v[_width*_height>>2];
	FILE *file;

    l_videoFileName = (char *)(*pEnv)->GetStringUTFChars(pEnv, pFileName, NULL);
	LOGI(1, "test file name is: %s, with width: %d, height: %d", l_videoFileName, _width, _height);
    //1. retrieve information about the bitmap
    if ((lRet = AndroidBitmap_getInfo(pEnv, pBitmap, &lInfo)) < 0) {
        LOGE(1, "AndroidBitmap_getInfo failed! error = %d", lRet);
        return;
    }
    if (lInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE(1, "Bitmap format is not RGBA_8888!");
        return;
    }
    //2. lock the pixel buffer and retrieve a pointer to it
    if ((lRet = AndroidBitmap_lockPixels(pEnv, pBitmap, &l_Bitmap)) < 0) {
        LOGE(1, "AndroidBitmap_lockPixels() failed! error = %d", lRet);
    }
	//open the test yuv file and read in data
	file = fopen("/sdcard/test.yuv", "rb");
    if (file == NULL) {
        fprintf(stderr, "Failed to read out.yuv\n");
        return;
    }
    fread(y, _width*_height,    1, file);
    fread(u, _width*_height>>2, 1, file);
    fread(v, _width*_height>>2, 1, file);
    fclose(file);
	//3. do yuv->rgb conversion and fill in the data
	LOGI(1, "start color conversion");
	yuv420_2_rgb8888(l_Bitmap, 
			y, 
			v,
			u, 
			_width,												//width
			_height, 											//height
			_width,												//Y span/pitch: No. of bytes in a row
			_width>>1,											//UV span/pitch
			_width<<2,											//bitmap span/pitch
			yuv2rgb565_table,
			0
			);
	LOGI(1, "end color conversion");
	//4. unlock the bitmap
	AndroidBitmap_unlockPixels(pEnv, pBitmap);
}



