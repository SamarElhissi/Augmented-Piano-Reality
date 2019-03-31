LOCAL_PATH := $(call my-dir)

                               include $(CLEAR_VARS)
                               OPENCV_INSTALL_MODULES:=on
                               include D:/Work/Android/OpenCV-3.1.0-android-sdk/sdk/native/jni/OpenCV.mk

                               LOCAL_SRC_FILES  := native.cpp
                               LOCAL_LDLIBS     += -llog -ldl

                               LOCAL_MODULE     := piano_project

                               include $(BUILD_SHARED_LIBRARY)