#ifndef __UTILS_H__
#define __UTILS_H__


#include <stdio.h>
#include <stdlib.h>
#include <opencv2/opencv.hpp>

//#include "gdirfiles.h"

#define _USE_MATH_DEFINES
#include <math.h>
#include <stdint.h>

#include <string.h>
#include <iostream>
#include <fstream>
#include <iomanip>

#include "templateProps.h"

#define SIGNOF(x) ( ( (x) > 0.0 ) ? 1 : -1)

#define MAX_PIX_DIST_MATCH  100

//typedef std::vector <std::string> DirListing_t;

std::string mType(int v);



void updateAssetSize(struct imgAssetInfo *ast, double fx, double fy, double bx, double by);

void findLocExtrema1D(std::vector<float> arr, int size, std::vector<float> &lm, std::vector<float> &lmn, std::vector<int> &lml, std::vector<int> &lmnl);


void showColoredOverlay(cv::Mat& image, cv::Mat& teplimgOver, char b, char g, char r);
void showColoredOverlayPts(cv::Mat& image, std::vector<cv::Point> teplimgOverPts, char b, char g, char r);
void showColoredOverlayPts(cv::Mat& image, std::vector<cv::Point> teplimgOverPts, int b, int g, int r);

void rot90(cv::Mat &matImage, int rotflag);

#endif 
