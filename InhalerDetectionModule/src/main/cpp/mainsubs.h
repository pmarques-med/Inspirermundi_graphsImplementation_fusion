#ifndef _MAINSUBS_H_
#define _MAINSUBS_H_

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>


#include "templateProps.h"
#include "utils.h"

#define INHALDET_WIDTH_RES 640 // Height resolution for inhaler detection
#define TEMPLATE_MARGIN_H_PERC 0.7 //template relative height to margin  | shortest dimention from camera
#define TEMPLATE_MARGIN_V_PERC 0.8 //template relative width to margin  | shortest dimention from camera

#define PROC_WIDTH_RES 384
#define NUMBS_REL_SIZE 0.2188
#define NUMBS_REL_OCCUPIED_SIZE 0.8


//cv::Mat teplimgOver;                    ///These 2 vars stay here only for debug...must be passed by ref on "PrepareTemplate"
//std::vector <cv::Point>teplimgOverPts; //locations of non-zero pixels

void UpdateOverlay(cv::Mat& imagePtr, std::vector <cv::Point> tplOverPts, bool successfulDetection);
void UpdateOverlay(cv::Mat& imagePtr, bool photoTaken, bool successfulDetection);

int onCameraFrame(cv::VideoCapture cap);

double CalcFxInput(cv::Mat& image);
double CalcFxTempl(cv::Mat& image, cv::Size tplsize);
double CalcFxTemplDiskus(cv::Mat &image, cv::Size tplsize);
double CalcFxTemplNovolizer(cv::Mat &image, cv::Size tplsize);
double CalcFxTemplEllipta(cv::Mat &image, cv::Size tplsize);


void applyCentBias2PtList(cv::Size origS, cv::Size tplS, std::vector<cv::Point> &ptList);
void applyBias2PtList(cv::Point bias, std::vector<cv::Point> &ptList);

void ParseFrame(cv::Mat& imagePtr);

int PrepareTemplate(cv::Mat& origImgPtr); // resize templates to HQ size, saves data, resizes to LQ, saves data.
int PrepareTemplate(cv::Mat* origImgPtr, std::string templateType, cv::Mat* template1, cv::Mat* template2, cv::Mat* template3);
bool inhalerDetection(cv::Mat& origImgPtr); // returns state: detected or otherwise
std::string InhalerDetectionStr();     //DEBUG

void ResizeImage(cv::Mat *imgPtr);

#endif

