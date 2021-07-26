//
// Created by backup on 31/05/2017.
//

#ifndef TUTORIAL_1_CAMERAPREVIEW_VIDEOACQUISITIONSCRIPTOPTIMIZED_H
#define TUTORIAL_1_CAMERAPREVIEW_VIDEOACQUISITIONSCRIPTOPTIMIZED_H

#include <stdlib.h>
#include <stdio.h>
#include <opencv2/opencv.hpp>
#include <numeric>

using namespace cv;
using namespace std;

#include "newutils.h"

int PrepareTemplate(Mat* origImgPtr, Mat* template1, Mat* template2, Mat* template3);
void ParseFrame(Mat* imagePtr);
bool InhalerDetection(Mat* imagePtr);
string InhalerDetectionStr(Mat *imagePtr);
void UpdateOverlay(Mat* imagePtr, bool successfulDetection);


#endif //TUTORIAL_1_CAMERAPREVIEW_VIDEOACQUISITIONSCRIPTOPTIMIZED_H
