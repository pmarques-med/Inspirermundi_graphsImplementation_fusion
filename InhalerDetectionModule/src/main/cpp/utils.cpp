#ifndef _MY_UTILS_
#define _MY_UTILS_

#include "utils.h"

std::string mType(int v){

    std::string s = "";

    switch( v%8 ){
        case 0:
            s += "CV_8U";
            break;
        case 1:
            s += "CV_8S";
            break;
        case 2:
            s += "CV_16U";
            break;
        case 3:
            s += "CV_16S";
            break;
        case 4:
            s += "CV_32S";
            break;
        case 5:
            s += "CV_32F";
            break;
        case 6:
            s += "CV_64F";
            break;
        default:
            s += "unkn";
            break;
    }

    switch( v - (v%8) ){
        case 0:
            s += "C1";
            break;
        case 8:
            s += "C2";
            break;
        case 16:
            s += "C3";
            break;
        case 24:
            s += "C4";
            break;
    }
    return s;
}


//--------------------------------------------------------


void updateAssetSize(imgAssetInfo *ast, double fx, double fy, double bx, double by){
// For adjusting the internal properties of the templates ( do once, after resizing to LQ )

    ast->bias.x = (ast->bias.x) *fx + bx;
    ast->bias.y = (ast->bias.y) *fy + by;
    ast->size.width  *= fx;
    ast->size.height *= fy;
    ast->fullimgsize.width *= fx;
    ast->fullimgsize.height *= fy;
}

//--------------------------------------------------------

//template<typename T>
void findLocExtrema1D(std::vector<float> arr, int size, std::vector<float> &lm, std::vector<float> &lmn, std::vector<int> &lml, std::vector<int> &lmnl){

    float val = arr[0];
    int ind = 0;
    int dir = (int) (2*( (arr[1]-arr[0])>=0.0)-1);
    for(int i=1; i<size; i++){
        if( dir>0 ){
            //for the maximums
            if( val < arr[i]  ){
                //continue rising
                val = arr[i];
                ind = i;
            }
            else if( val > arr[i]  ){
                //stoped rising->found peak, inverting dir
                lm.push_back(val);
                lml.push_back(ind);

                val = arr[i];
                ind = i;
                dir = -1;
            }
        }
        else{
            //for the minimums
            if( val > arr[i]  ){
                //continue falling
                val = arr[i];
                ind = i;
            }
            else if( val < arr[i]  ){
                //stoped falling->found valley, inverting dir
                lmn.push_back(val);
                lmnl.push_back(ind);

                val = arr[i];
                ind = i;
                dir = 1;
            }
        }

    }
    //Last value
    if(dir>0){
        lm.push_back(val);
        lml.push_back(ind);
    }
    else if (dir<0){
        lmn.push_back(val);
        lmnl.push_back(ind);
    }

}


//-------------------------------------------------------

void showColoredOverlay(cv::Mat& image, cv::Mat& teplimgOver, char b, char g, char r){

    char bgr[3] = {b,g,r};

    cv::Mat ovlDisp = image.clone();
    cv::Mat ovlpTemp = teplimgOver.clone();
    cv::cvtColor(ovlpTemp, ovlpTemp, cv::COLOR_GRAY2BGR);
    multiply(ovlDisp, (ovlpTemp>128)/255, ovlpTemp);
    ovlDisp -= ovlpTemp;

    std::vector<cv::Mat> chans;
    for(int i=0; i< 3; i++){
        if(bgr[i])
            chans.push_back(teplimgOver.clone()>128);
        else
            chans.push_back(cv::Mat::zeros(image.size(), CV_8U));
    }
    merge(chans, ovlpTemp);

    ovlDisp += ovlpTemp;
    cv::flip(ovlDisp, ovlDisp, 1); //flipping just for my PC feedback, comment otherwise
    cv::imshow("pic", ovlDisp);
}

void showColoredOverlayPts(cv::Mat& image, std::vector<cv::Point> teplimgOverPts, char b, char g, char r){

    //cv::Mat ovlDisp = image.clone();

    std::vector<cv::Point>::iterator it;// = teplimgOverPts.begin();
    for(it=teplimgOverPts.begin(); it != teplimgOverPts.end(); ++it){

        if(((*it).y < image.rows) && ((*it).x < image.cols))
        {
            image.at<cv::Vec4b>((*it).y, (*it).x)[0] = b*128;
            image.at<cv::Vec4b>((*it).y, (*it).x)[1] = g*128;
            image.at<cv::Vec4b>((*it).y, (*it).x)[2] = r*128;
        }

    }

    //cv::flip(ovlDisp, ovlDisp, 1); //flipping just for my PC feedback, comment otherwise
    //cv::imshow("pic", ovlDisp);

}

void showColoredOverlayPts(cv::Mat& image, std::vector<cv::Point> teplimgOverPts, int b, int g, int r){

    //cv::Mat ovlDisp = image.clone();

    std::vector<cv::Point>::iterator it;// = teplimgOverPts.begin();
    for(it=teplimgOverPts.begin(); it != teplimgOverPts.end(); ++it){

        if((*it).y < 0 || (*it).x < 0)
            continue;

        if(((*it).y < image.rows) && ((*it).x < image.cols))
        {
            image.at<cv::Vec4b>((*it).y, (*it).x)[0] = b;
            image.at<cv::Vec4b>((*it).y, (*it).x)[1] = g;
            image.at<cv::Vec4b>((*it).y, (*it).x)[2] = r;
        }

    }

    //cv::flip(ovlDisp, ovlDisp, 1); //flipping just for my PC feedback, comment otherwise
    //cv::imshow("pic", ovlDisp);

}


#endif
