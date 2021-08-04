#ifndef __UTILS_H__
#define __UTILS_H__


#include <stdio.h>
#include <stdlib.h>
#include <opencv2/opencv.hpp>

#include "gdirfiles.h"

#include <math.h>
#include <stdint.h>

#include <string.h>
#include <iostream>
#include <fstream>
#include <iomanip>

#define SIGNOF(x) ( ( (x) > 0.0 ) ? 1 : -1)

#define MAX_PIX_DIST_MATCH_a  300  //(ANTES ERA 16)
#define MAX_PIX_DIST_MATCH_b  300  //(ANTES ERA 16)

typedef std::vector <std::string> DirListing_t;

std::string mType(int v);
double myDist(cv::Point a, cv::Point b );
double normTo2Pi(double x);
double normToPi(double x);
double mapToPi14Q( double x );


void printMat(cv::Mat& m);
void printFloatMat(cv::Mat& m);


template<typename T>
void printArray(T *arr, int N);
void rot90(cv::Mat &matImage, int rotflag);


void myLoGKernel(cv::Mat& dst, double sigm);
void replaceByLoGKernel(cv::Mat& aux, double hsize);
void mynorm(cv::Mat& aux, float newmin, float newmax);
void drawCentSQR(cv::Mat &m, float perc, int channel=2, int val=255);
void drawCentCross(cv::Mat &m, float perc, int channel=2, int val=255);
void drawCenteredLine(cv::Mat &m, cv::Size posxy, float theta=M_PI/2, int channel=2, int val=255);
void reDrawOvrlp(cv::Mat& overlp, cv::Mat& teplimg, cv::Mat& teplimg2, cv::Point max_loc, cv::Point max_loc2, int detected, double locD, 
    double locDc, std::string win="");

template<typename T>
int exportArray(T arr, char* filename);


struct imgAssetInfo {
  cv::Size fullimgsize;
  cv::Size size;
  cv::Point bias;
  std::string path;
  //cv::Mat image;
};

void updateAssetSize(struct imgAssetInfo *ast, double fx, double fy, double bx, double by);
void correctForBias(struct imgAssetInfo *ast, cv::Point *maxp, cv::Point *minp);

void findLocExtrema1D(std::vector<float> arr, int size, std::vector<float> &lm, std::vector<float> &lmn, std::vector<int> &lml, std::vector<int> &lmnl);

void showColoredOverlay(cv::Mat& image, cv::Mat& teplimgOver, char b, char g, char r);

void showColoredOverlayPts(cv::Mat* image, std::vector<cv::Point> teplimgOverPts, char b, char g, char r);
void loadAvgNumbers(std::string pathchars, cv::Mat& charsAvg);
cv::Point oneObjCentroid(cv::Mat& m);

void CalcMatAverage(cv::Mat* buffer[], int bufferSize, cv::Mat& output);
void InsertMat(cv::Mat* buffer[], int bufferSize, cv::Mat* newMat);

#endif 
