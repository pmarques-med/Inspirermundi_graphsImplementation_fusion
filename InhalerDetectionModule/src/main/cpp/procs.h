#ifndef _PROCS_H_
#define _PROCS_H_

#include "utils.h"

#include <iomanip>


///dots
#define DOT_SIZE_RATIO 0.7
#define DOT_SIZE_MINOMAX 18 //Considering PROC_WIDTH_RES of 384
#define DOT_SIZE_MAXPMAX 30

///Numbers
#define NUMBS_REL_DETMIN 0.75 //minimum match intensity to be Detected number
#define NUMBS_REL_MATCH_MAX_XDIST 0.04


#define HUE_RED_MIN 01
#define HUE_RED_MAX 15
#define HUE_YlW_MIN 16
#define HUE_YLW_MAX 26
#define HUE_GRN_MIN 27
#define HUE_GRN_MAX 48

#define ITVL_RED_MIN    00
#define ITVL_RED_MAX    18
#define ITVL_YLW_MIN    30
#define ITVL_YLW_MAX    40
#define ITVL_GRN_MIN    50
#define ITVL_GRN_MAX    120

#define MIXEDSCALE_MULT 3
#define MAX_TRIES 15
#define RDSTMAX 0.38 //0.36
#define RDSTMIN 0.25 //0.28

cv::Point colorExpectedInterval(double highH, double lowH, int* dualColor = NULL);

//------------------------------------------------------------------------------

std::vector<cv::Point> botPositionExpectedInterval(double percBot, bool ab=false);

//------------------------------------------------------------------------------
void getWheelDots(const cv::Mat& input, cv::Mat& output);


//------------------------------------------------------------------------------

int filterContoursWindow(cv::Mat& src, cv::Mat& out);


//-------------------------------------------------------
int filterContoursWindow2(cv::Mat& src, cv::RotatedRect& outRect);

//-------------------------------------------------------

template<typename T>
void myHistogram(T* hist, cv::Mat& m);

template<typename T>
double calcOtsu(T* hist);

double getOtsuThs(cv::Mat & m);

double thsmiddle(cv::Mat& m);


//------------------------------------------------------------------------------


// (first) Description: Get the White background and decide the relative position of the crossing to color
// based on 1st-masking, 2nd-summing vertical the mask values, 3rd-decide the good separation with
// maximum of negative highpass filter of the mask horizontal profile
cv::Rect getColoredWindow( cv::Rect windowR, cv::Mat& satChan, cv::Mat&top );


//------------------------------------------------------------------------------



int filterObjs4Dial(cv::Mat& objs, cv::Mat& grey, cv::Mat& out);

//------------------------------------------------------------------------------

cv::RotatedRect rmvTheDot(cv::Mat& labeledObjs);




//------------------------------------------------------------------------------


void performNumberDetection(cv::Mat& labeledObjs, std::vector<cv::Mat>& charsAvg, std::vector<int>& matchVals, std::vector<cv::Point>& matchLocs);

#endif
