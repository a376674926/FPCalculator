LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := \
        libarity_cal \
        libguava_cal \
        android-support-v4 \

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := FPCalculator

LOCAL_OVERRIDES_PACKAGES := Calculator

#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
                                        libarity_cal:libs/arity-2.1.2.jar \
                                        libguava_cal:libs/guava-r07.jar
include $(BUILD_MULTI_PREBUILT)

# additionally, build tests in sub-folders in a separate .apk
include $(call all-makefiles-under,$(LOCAL_PATH))
