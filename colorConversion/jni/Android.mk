LOCAL_PATH := $(call my-dir)

#the yuv2rgb library
include $(CLEAR_VARS)
LOCAL_ALLOW_UNDEFINED_SYMBOLS=false
LOCAL_MODULE := yuv2rgb
LOCAL_SRC_FILES := yuv2rgb/yuv2rgb16tab.c yuv2rgb/yuv420rgb8888.s test.c
LOCAL_LDLIBS    := -llog -ljnigraphics -lz -lm
include $(BUILD_SHARED_LIBRARY)
