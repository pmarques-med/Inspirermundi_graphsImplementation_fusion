#include "videoAcquisitionScriptOptimized.h"

#define BUFFER_SIZE 3
Mat colorMasked, ovlDisp, ovlpTemp;
Mat edgesMean;
Mat *edgesBuffer[] = {nullptr, nullptr, nullptr};
cv::Size cbias;
Mat teplimg, teplimg2, teplimgFull;
vector <cv::Point>teplimgOverPts; //locations of non-zero pixels

int state = 0;
bool templateDebugFlag = false;


String path = "/home/texas/Desktop/Dropbox/0_Spiro/MAIAapp/inaladores/template/";

imgAssetInfo astElipse = {
        cv::Size(735, 290),         //fullimgsize
        cv::Size(150, 184),         //.size
        cv::Point(222, 51),         //.bias
        //path+"flutiformTpl/templateplusbdedges2cropelipsebb.jpg", //path
};

imgAssetInfo astTopInhal = {
        cv::Size(735, 290),         //fullimgsize
        cv::Size(530, 290),         //.size
        cv::Point(0, 0),         //.bias
        //path+"flutiformTpl/templateplusbdedges2cropnomouthbb.jpg", //path
};

imgAssetInfo astDispInhal = {
        cv::Size(735, 290),         //fullimgsize
        cv::Size(735, 290),         //.size
        cv::Point(0, 0),         //.bias
        //path+"flutiformTpl/templateplusbdedges2crop.jpg", //path
};

double CalcFx(Mat* image)
{
    double fx = 640/(double)(image->size()).height;
    return fx;
}



int PrepareTemplate(Mat* origImgPtr, Mat* template1, Mat* template2, Mat* template3)
{
    //**--------------------Dealing with template images -------------------**//
    //----------------------Importing Template images-------------------------//

    if(teplimgOverPts.size() > 0)
        return 0;

    cv::Mat image;

    //teplimg = imread( astTopInhal.path, CV_LOAD_IMAGE_GRAYSCALE); //use image as template
    //teplimg2 = imread( astElipse.path, CV_LOAD_IMAGE_GRAYSCALE); //use image as template
    //teplimgFull = imread( astDispInhal.path, CV_LOAD_IMAGE_GRAYSCALE); //use image as template
    teplimg = *template1;
    teplimg2 = *template2;
    teplimgFull = *template3;

    if( (!teplimg.data) || (!teplimg2.data) || (!teplimgFull.data) ) // Check for invalid input
    {
        //cout <<  "\nlog::Could not open or find the image.\n\n" << std::endl;
        return -1;
    }

    //----------------------Adjust Template size-----------------------//

    image = origImgPtr->clone();

    cv::Size s0 = image.size();
    double fx = CalcFx(&image);
    resize(image, image, cv::Size(), fx, fx, INTER_LINEAR);

    double marginHp = 0.8;          //<<<adjust accordingly (size of template in screen)
    s0 = image.size();
    cv::Size s1 = astTopInhal.fullimgsize;
    double fx2 = marginHp*s0.width/s1.width;

    Mat teplimgR, teplimg2R, teplimgFullR;
    resize(teplimg, teplimgR, cv::Size(), fx2, fx2, INTER_LINEAR);
    resize(teplimg2, teplimg2R, cv::Size(), fx2, fx2, INTER_LINEAR);
    resize(teplimgFull, teplimgFullR, cv::Size(), fx2, fx2, INTER_LINEAR);

    Mat teplimgOver = Mat::zeros(image.size(), CV_8U);
    s0 = ( image.size()-teplimgFullR.size() )/2;
    Point bias0( s0 );
    cv::Rect r(bias0, teplimgFullR.size() );


    //cerr << "Bias Top:" << astTopInhal.bias << " | Bias Elipse: " << astElipse.bias << "\n";
    updateAssetSize(&astElipse, fx2, fx2, 0,0);
    updateAssetSize(&astTopInhal, fx2, fx2, 0, 0);
    updateAssetSize(&astDispInhal, fx2, fx2, 0, 0);

    //cerr << "Bias Top:" << astTopInhal.bias << " | Bias Elipse: " << astElipse.bias << " --Post\n";

    teplimg2R.copyTo(teplimg2);      // << resized template 1
    teplimgR.copyTo(teplimg);        // << resized template 2
    teplimgFullR.copyTo(teplimgOver(r)); // << overlapping the display

    {
        cv::dilate(teplimgOver, teplimgOver,\
                   getStructuringElement( MORPH_ELLIPSE, Size(4,4) ) ) ; //orig 3,3
        cv::erode(teplimgOver, teplimgOver,\
                   getStructuringElement( MORPH_ELLIPSE, Size(6, 6) ) ) ;
        cv::dilate(teplimgOver, teplimgOver,\
                   getStructuringElement( MORPH_ELLIPSE, Size(6, 6) ) ) ; //orig 3,3
        // just for show, Comment if refined contour needed

        cv::dilate(teplimg, teplimg,\
                   getStructuringElement( MORPH_ELLIPSE, Size(4,4) ) ) ;
        cv::dilate(teplimg2, teplimg2,\
                   getStructuringElement( MORPH_ELLIPSE, Size(4,4) ) ) ;

    }

    teplimg2R.release();
    teplimgR.release();
    teplimgFullR.release();
    teplimgFull.release();

    cv::findNonZero(teplimgOver, teplimgOverPts);

    //**--------------------Template setup done ---------------------**//

    return 0;
}

void ParseFrame(Mat* imagePtr)
{
    //Pre-Setup
    cv::Size diamdisk = cv::Size(7, 7);
    cv::Mat kernDisk = getStructuringElement(MORPH_ELLIPSE, diamdisk);

    Mat image = *imagePtr;

    //Reduce resolution
    cv::Size s = image.size();
    double fx = CalcFx(imagePtr);
    cv::resize(image, image, cv::Size(), fx, fx, INTER_LINEAR);

    cbias = ( image.size()-astTopInhal.fullimgsize )/2;

    cv::Mat grey, grey2, edges, hsvimg;

    cv::cvtColor(image, hsvimg, COLOR_BGR2HSV);

    cv::Mat colorMask;
    cv::inRange(hsvimg, cv::Scalar(0, 0, 0), cv::Scalar(180, 138, 255), colorMask); //baseline values: Scalar(0, 0, 32)-Scalar(180, 64, 255)
    cv::dilate(colorMask, colorMask, kernDisk);
    cv::erode(colorMask, colorMask, kernDisk);

    cv::cvtColor(colorMask, colorMask, COLOR_GRAY2BGR);
    cv::multiply(image, colorMask/255, colorMasked);

    cvtColor(colorMasked, grey, COLOR_BGR2GRAY);

    blur( grey, grey2, Size(3,3) ); 		/// Reduce noise with a kernel 3x3
    Canny(grey2, edges, 0, 30, 3);

    int size = BUFFER_SIZE;
    //InsertMat(edgesBuffer, size, &edges);
    //CalcMatAverage(edgesBuffer, size, edgesMean);

    if(state == 0)
    {
        edgesMean = edges.clone();
        state = 1;
    } else
    {
        edgesMean = edgesMean/4 + edges*3/4;
        state = 0;
    }
}

vector <cv::Point> teplimg1OverPts; //locations of non-zero pixels
vector <cv::Point> teplimg2OverPts; //locations of non-zero pixels
double locD, locDc, locDPost, locDcPost;
bool InhalerDetection(Mat *imagePtr)
{
    //if(edgesMean.empty())
    //    return false;


    ///Coarse Matching of full template and elipse

    int match_method = CV_TM_CCORR;
    int result_cols =  edgesMean.cols - teplimg.cols + 1;
    int result_rows = edgesMean.rows - teplimg.rows + 1;
    Mat result0, result1;
    result0.create( result_rows, result_cols, CV_32FC1 );
    result1.create( result_rows, result_cols, CV_32FC1 );
    matchTemplate( edgesMean, teplimg, result0, match_method );
    matchTemplate( edgesMean, teplimg2, result1, match_method );

    double sig = 1.5;
    int hs = (int)ceil(sig*4);
    hs += !(hs%2);
    Size ksize = Size(hs, hs);

    GaussianBlur(result0, result0, ksize, sig, sig, cv::BORDER_REPLICATE );
    GaussianBlur(result1, result1, ksize, sig, sig, cv::BORDER_REPLICATE );


    cv::Point min_loc, max_loc, min_loc2, max_loc2;
    minMaxLoc(result0, NULL, NULL, &min_loc, &max_loc);
    minMaxLoc(result1, NULL, NULL, &min_loc2, &max_loc2);

    double dy = max_loc.y-max_loc2.y,
            dx = max_loc.x-max_loc2.x;
    locD = sqrt( dx*dx + dy*dy );

    dy = max_loc.y-cbias.height, dx = max_loc.x-cbias.width;
    locDc = sqrt( dx*dx + dy*dy );


    if(templateDebugFlag)
    {
        //for new disp of dbgging

        cv::findNonZero(teplimg, teplimg1OverPts);
        cv::findNonZero(teplimg2, teplimg2OverPts);

        std::vector<cv::Point>::iterator it;
        for(it=teplimg1OverPts.begin(); it != teplimg1OverPts.end(); ++it) {
            (*it).y += (int) max_loc.y;
            (*it).x += (int) max_loc.x;

            //if((*it).y < 0 || (*it).x < 0)
            //    break;
        }
        for(it=teplimg2OverPts.begin(); it != teplimg2OverPts.end(); ++it) {
            (*it).y += (int) max_loc2.y;
            (*it).x += (int) max_loc2.x;

            //if((*it).y < 0 || (*it).x < 0)
            //     break;
        }

        //showColoredOverlayPts(imagePtr, teplimg1OverPts, 1, 0, 0); //red vs green overlay
        //showColoredOverlayPts(imagePtr, teplimg2OverPts, 1, 0, 1); //red vs green overlay
    }


    cv::Point min_loc00 = min_loc, min_loc20 = min_loc2;
    cv::Point max_loc00 = max_loc, max_loc20 = max_loc2;

    correctForBias(&astTopInhal, &max_loc00, &min_loc00);
    correctForBias(&astElipse, &max_loc20, &min_loc20);

    ///Find distance and position between templates and expected place
    dy = max_loc00.y-max_loc20.y;
    dx = max_loc00.x-max_loc20.x;

    locDPost = sqrt( dx*dx + dy*dy );

    dy = max_loc00.y-cbias.height, dx = max_loc00.x-cbias.width;
    locDcPost = sqrt( dx*dx + dy*dy );

    if(cbias.area() == 0 || cbias.empty())
        return 0;


    int detectionInt = (locDPost <= MAX_PIX_DIST_MATCH_a) && (locDcPost <= MAX_PIX_DIST_MATCH_b);

    bool detection = detectionInt;

    return detection;
}

void UpdateOverlay(Mat* imagePtr, bool successfulDetection)
{
    showColoredOverlayPts(imagePtr, teplimgOverPts, 0, successfulDetection, !successfulDetection); //red vs green overlay

    if(templateDebugFlag)
    {
        showColoredOverlayPts(imagePtr, teplimg1OverPts, 1, 0, 0); //red vs green overlay
        showColoredOverlayPts(imagePtr, teplimg2OverPts, 1, 0, 1); //red vs green overlay
    }

}

string InhalerDetectionStr(Mat *imagePtr)
{
    int result = InhalerDetection(imagePtr);

    ostringstream oss;
    oss << result << "\r\n";
    oss << "locBetweenTemps: " <<  locD << "\tlocDisplay: " << locDc << "\tout of: "<<  MAX_PIX_DIST_MATCH_a << "\r\n";
    oss << "locBetweenTemps: " <<  locDPost << "\tlocDisplay: " << locDcPost << "\tout of: "<<  MAX_PIX_DIST_MATCH_b << "\r\n\r\n";
    return oss.str();
}
