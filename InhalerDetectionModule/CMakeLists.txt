# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

cmake_minimum_required(VERSION 3.4.1)
set (CMAKE_CXX_STANDARD 11)


set(pathToOpenCv ../../../OpenCV-android-sdk)
include_directories(${pathToOpenCv}/sdk/native/jni/include)

add_library( lib_opencv SHARED IMPORTED )
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION
                     ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)

add_library(new_template_props SHARED src/main/cpp/templateProps.cpp)
target_link_libraries(new_template_props lib_opencv)

add_library(new_utils SHARED src/main/cpp/utils.cpp)
target_link_libraries(new_utils new_template_props)

add_library(frame_analyser_take1 SHARED src/main/cpp/mainsubs.cpp)
target_link_libraries(frame_analyser_take1 new_utils)

find_library( log-lib
              log )

add_library( jni_wrapper SHARED src/main/cpp/jniWrapper.cpp)
target_link_libraries(jni_wrapper log frame_analyser_take1)