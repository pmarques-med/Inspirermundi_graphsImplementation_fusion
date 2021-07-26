#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/opencv.hpp>

#include "mainsubs.h"

using namespace std;
using namespace cv;
extern "C"
{

    JNIEXPORT jstring JNICALL
    Java_org_medida_inhalerdetection_InhalerDetectionActivity_stringFromJNI(JNIEnv *env, jobject) {
        std::string hello = "Hello from C++";
        return env->NewStringUTF(hello.c_str());
    }

    //JNIEXPORT void JNICALL Java_org_opencv_samples_tutorial1_Tutorial1Activity_FindFeatures(JNIEnv*, jobject, jlong addrGray, jlong addrRgba);


    JNIEXPORT void JNICALL
    Java_org_medida_inhalerdetection_InhalerDetectionActivity_PrepareTemplate(JNIEnv *env, jobject, jlong imageAddr, jstring javaTemplateString, jlong template1Addr, jlong template2Addr, jlong template3Addr)
    {
        Mat* imagePtr = (Mat*) imageAddr;
        Mat* template1Ptr = (Mat*) template1Addr;
        Mat* template2Ptr = (Mat*) template2Addr;
        Mat* template3Ptr = (Mat*) template3Addr;

        const char *templateString = env->GetStringUTFChars(javaTemplateString, 0);

        PrepareTemplate(imagePtr, templateString, template1Ptr, template2Ptr, template3Ptr);
    }

    JNIEXPORT jboolean JNICALL
    Java_org_medida_inhalerdetection_Tutorial1Activity_InhalerDetection(JNIEnv*, jobject, jlong imageAddr)
    {
        cv::Mat mat;
        bool detection = inhalerDetection(mat); //empty mat, isn't used

        return detection;
    }

    JNIEXPORT jboolean JNICALL
    Java_org_medida_inhalerdetection_ImageParser_InhalerDetection(JNIEnv* e, jobject o, jlong imageAddr)
    {
        return Java_org_medida_inhalerdetection_Tutorial1Activity_InhalerDetection(e, o , imageAddr);
    }

    JNIEXPORT void JNICALL
    Java_org_medida_inhalerdetection_ImageParser_ParseFrame(JNIEnv* e, jobject o, jlong imageAddr)
    {
        Mat* imagePtr = (Mat*) imageAddr;
        Mat bgrImage;

        //Mat cloneImage = imagePtr->clone(); //DEBUG

        cvtColor(*imagePtr, bgrImage, COLOR_RGBA2BGR);

        ParseFrame(bgrImage);
    }

    JNIEXPORT void JNICALL
    Java_org_medida_inhalerdetection_InhalerDetectionActivity_ParseFrame(JNIEnv* e, jobject o, jlong imageAddr)
    {
        Java_org_medida_inhalerdetection_ImageParser_ParseFrame(e, o, imageAddr);
    }

    JNIEXPORT jstring JNICALL
    Java_org_medida_inhalerdetection_Tutorial1Activity_InhalerDetectionStr(JNIEnv* env, jobject)
    {
        string output = InhalerDetectionStr();

        const char* buf = output.c_str();

        jstring jstrBuf = env->NewStringUTF(buf);

        return jstrBuf;
    }

    JNIEXPORT jstring JNICALL
    Java_org_medida_inhalerdetection_ImageParser_InhalerDetectionStr(JNIEnv* e, jobject o)
    {
        return Java_org_medida_inhalerdetection_Tutorial1Activity_InhalerDetectionStr(e, o);
    }
/*
    JNIEXPORT jstring JNICALL
    Java_org_medida_inhalerdetection_ImageParser_InhalerDetectionStr(JNIEnv *env, jobject instance,
                                                                      jlong imageAddr) {

        return Java_org_medida_inhalerdetection_Tutorial1Activity_InhalerDetectionStr(env, instance, imageAddr);
    }

    JNIEXPORT jstring JNICALL
    Java_org_medida_inhalerdetection_ImageParserRunnable_InhalerDetectionStr(JNIEnv *env, jobject instance,
                                                                      jlong imageAddr) {

        return Java_org_medida_inhalerdetection_Tutorial1Activity_InhalerDetectionStr(env, instance, imageAddr);
    }
*/

    JNIEXPORT void JNICALL
    Java_org_medida_inhalerdetection_InhalerDetectionActivity_UpdateOverlay(JNIEnv* env, jobject, jlong imageAddr, jboolean photoTaken, jboolean successfulDetection)
    {
        Mat* imagePtr = (Mat*) imageAddr;
        UpdateOverlay(*imagePtr, photoTaken, successfulDetection);
    }

    JNIEXPORT void JNICALL
    Java_org_medida_inhalerdetection_ImageParser_ResizeImage(JNIEnv* e, jclass o, jlong imageAddr)
    {
        Mat* imagePtr = (Mat*) imageAddr;
        ResizeImage(imagePtr);
    }

    JNIEXPORT void JNICALL
    Java_org_medida_inhalerdetection_OpenCVCameraListener_UpdateOverlay(JNIEnv *env, jobject instance, jlong imageAddr, jboolean photoTaken, jboolean successfulDetection)
    {
        Mat* imagePtr = (Mat*) imageAddr;
        UpdateOverlay(*imagePtr, photoTaken, successfulDetection);
    }

    JNIEXPORT void JNICALL
    Java_org_medida_inhalerdetection_OpenCVCameraListener_PrepareTemplate(JNIEnv *env, jobject, jlong imageAddr, jstring javaTemplateString, jlong template1Addr, jlong template2Addr, jlong template3Addr)
    {
        Mat* imagePtr = (Mat*) imageAddr;
        Mat* template1Ptr = (Mat*) template1Addr;
        Mat* template2Ptr = (Mat*) template2Addr;
        Mat* template3Ptr = (Mat*) template3Addr;

        const char *templateString = env->GetStringUTFChars(javaTemplateString, 0);

        PrepareTemplate(imagePtr, templateString, template1Ptr, template2Ptr, template3Ptr);
    }

    JNIEXPORT void JNICALL
    Java_org_medida_inhalerdetection_OpenCVCameraListener_ParseFrame(JNIEnv* e, jobject o, jlong imageAddr)
    {
        Java_org_medida_inhalerdetection_ImageParser_ParseFrame(e, o, imageAddr);
    }
}