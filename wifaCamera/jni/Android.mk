LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_LIB_TYPE:=STATIC
ifeq ("$(wildcard $(OPENCV_MK_PATH))","")  
#try to load OpenCV.mk from default install location  
include /home/zhitong/OpenCV-2.4.10-android-sdk/sdk/native/jni/OpenCV.mk 
else  
include $(OPENCV_MK_PATH)  
endif  
LOCAL_MODULE    := qiaoliang
LOCAL_SRC_FILES := qiaoliang.cpp
LOCAL_LDLIBS    += -lm -llog 
include $(BUILD_SHARED_LIBRARY)
