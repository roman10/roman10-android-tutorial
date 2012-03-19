LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_ALLOW_UNDEFINED_SYMBOLS=false
LOCAL_MODULE := mmap
LOCAL_SRC_FILES := mmaptest.c
LOCAL_LDLIBS    := -llog
include $(BUILD_SHARED_LIBRARY)
