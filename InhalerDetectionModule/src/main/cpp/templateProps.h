#ifndef _TEMP_PROPS_H_
#define _TEMP_PROPS_H_

#include <stdlib.h>
#include <stdio.h>
#include <string>

#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>

struct imgAssetInfo {
  cv::Size fullimgsize;
  cv::Size size;
  cv::Point bias;
  std::string path;
};

int inhalStr2IID(std::string inhalStr);
int getTemplateProps(std::string inhalStr, std::string path, imgAssetInfo *astDial, imgAssetInfo *astInhalCntr, imgAssetInfo *astDispInhal);


#endif
