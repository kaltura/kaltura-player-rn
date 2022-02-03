cmake_minimum_required(VERSION 3.9.0)

set (PACKAGE_NAME "kplayer")
set (BUILD_DIR ${CMAKE_SOURCE_DIR}/build)

add_library(
        ${PACKAGE_NAME}
        SHARED
        src/main/cpp/cpp-adapter.cpp
        src/main/cpp/PlayerJSIHostObject.cpp
)

include_directories(
        "${NODE_MODULES_DIR}/react-native/React"
        "${NODE_MODULES_DIR}/react-native/React/Base"
        "${NODE_MODULES_DIR}/react-native/ReactCommon/jsi"
)

# find libraries

file (GLOB LIBRN_DIR "${BUILD_DIR}/react-native-0*/jni/${ANDROID_ABI}")

find_library(
        LOG_LIB
        log
)
find_library(
        REACT_NATIVE_JNI_LIB
        reactnativejni
        PATHS ${LIBRN_DIR}
        NO_CMAKE_FIND_ROOT_PATH
)

target_link_libraries(
        ${PACKAGE_NAME}
        ${LOG_LIB}
        ${REACT_NATIVE_JNI_LIB}
        android
)