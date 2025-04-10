LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := tess
LOCAL_SRC_FILES := \
    jni/tess/baseapi.cpp \
    jni/tess/tesseractmain.cpp
    # 필요한 소스 추가

LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/jni/tess/include \
    $(LOCAL_PATH)/jni/leptonica/include

LOCAL_LDLIBS := -llog -ljnigraphics

include $(BUILD_SHARED_LIBRARY)
