#include "mainsubs.h"


short frameCounter = -1;
cv::Mat hqImage;
cv::Mat lqImage;
cv::Mat edgesMeanLQ;

cv::Mat  template1HQ, template2HQ, template3HQ;//teplimg (dial), teplimg2 (larger), teplimgFull;
cv::Mat  template1LQ, template2LQ, template3LQ;
std::vector <cv::Point> tplPts1hq, tplPts2hq, tplPts3hq, templimgPtsDispHQ;
std::vector <cv::Point> tplPts1lq, tplPts2lq, tplPts3lq, templimgPtsDispLQ;
std::vector <cv::Point> overlayPtsHQ; //cv::Mat& teplimgOver;


cv::String path = "/home/texas/Desktop/Dropbox/0_Spiro/MAIAapp/inaladores/";
cv::String inhalStr = "flutiform"; //"flutiform", "turbohaler", "spiromax", "ellipta"
imgAssetInfo astDialI, astInhalCntrI, astDispInhalI; //chekc PrepareTemplate if ast in HQ or LQ mode

//------------------------------------------------------------------------------

void UpdateOverlay(cv::Mat& imagePtr, std::vector <cv::Point> tplOverPts, bool successfulDetection)
{
    showColoredOverlayPts(imagePtr, tplOverPts, 0, successfulDetection, !successfulDetection); //red vs green overlay
}

void UpdateOverlay(cv::Mat& imagePtr, bool photoTaken, bool successfulDetection)
{
    if(photoTaken)
    {
        if(successfulDetection)
            showColoredOverlayPts(imagePtr, templimgPtsDispHQ, 0, 1, 0); //green overlay to note success
        else
            showColoredOverlayPts(imagePtr, templimgPtsDispHQ, 0, 0, 1); //red overlay to note failure
    }
    else
        showColoredOverlayPts(imagePtr, templimgPtsDispHQ, 255, 210, 0); //yellow overlay to note attempt

    //edgesMeanLQ.copyTo(imagePtr);
    //showColoredOverlayPts(imagePtr, overlayPtsHQ, 0, 1, 1); //red vs green overlay
}


//--------------------------------------------------------------------------------

double CalcFxInput(cv::Mat& image)
{
    double fx = INHALDET_WIDTH_RES/(double)(image.size()).height;
    //std::cerr<< "fx: " << fx << "\n";
    //std::cerr<< "s0 W: " << (image.size()).width << "s0 H: " << (image.size()).height << "\n";
    return fx;
}
//--------------------------------------------------------------------------------
double CalcFxTempl(cv::Mat& image, cv::Size tplsize)
{
    //TEMPLATE_MARGIN_H_PERC          //<<<adjust accordingly (size of template in screen)
    cv::Size s0 = image.size();

    double fx2 = ( (double) TEMPLATE_MARGIN_H_PERC )*(s0.width)/tplsize.width;
    return fx2;
}

double CalcFxTemplDiskus(cv::Mat &image, cv::Size tplsize)
{
    //TEMPLATE_MARGIN_H_PERC          //<<<adjust accordingly (size of template in screen)
    cv::Size s0 = image.size();

    double fx2 = ( (double) TEMPLATE_MARGIN_V_PERC )*(s0.height)/tplsize.height;
    return fx2;
}

double CalcFxTemplNovolizer(cv::Mat &image, cv::Size tplsize)
{
    //TEMPLATE_MARGIN_H_PERC          //<<<adjust accordingly (size of template in screen)
    cv::Size s0 = image.size();

    double fx2 = ( (double) 0.65 )*(s0.width)/tplsize.width;
    return fx2;
}

double CalcFxTemplEllipta(cv::Mat &image, cv::Size tplsize)
{
    //TEMPLATE_MARGIN_H_PERC          //<<<adjust accordingly (size of template in screen)
    cv::Size s0 = image.size();

    double fx2 = ( (double) 0.65 )*(s0.width)/tplsize.width;
    return fx2;
}
//--------------------------------------------------------------------------------
void applyCentBias2PtList(cv::Size origS, cv::Size tplS, std::vector <cv::Point> &ptList)
{
    cv::Point cBias( (origS - tplS)/2 );
    std::cerr<< cBias << "\n";

    std::vector<cv::Point>::iterator it;
    for(it=ptList.begin(); it != ptList.end(); ++it)
        (*it) += cBias;
}
//--------------------------------------------------------------------------------
void applyBias2PtList(cv::Point bias, std::vector<cv::Point> &ptList)
{
    std::vector<cv::Point>::iterator it;
    for(it=ptList.begin(); it != ptList.end(); ++it)
        (*it) += bias;
}
//--------------------------------------------------------------------------------
int onCameraFrame(cv::VideoCapture cap)
{
    int eoStream=0;
    cv::Mat colorMasked;

    if( eoStream )
    {
        std::cerr <<  "Probably EOF, or camera disconnected\n";
        return -1;
    }
    eoStream = !cap.read(hqImage);   // get a new frame from camera (BGR uchar)
    if( eoStream )
    {
        std::cerr <<  "\nEOStream found - Probably EOF, or camera disconnected.\n";
        return -1;
    }



    if (frameCounter==-1)
    {
        if( getTemplateProps(inhalStr, path, &astDialI, &astInhalCntrI, &astDispInhalI) !=0 ){
            return -1;
        }

        template1HQ = cv::imread( astDialI.path, CV_LOAD_IMAGE_GRAYSCALE);
        template2HQ = cv::imread( astInhalCntrI.path, CV_LOAD_IMAGE_GRAYSCALE);
        template3HQ = cv::imread( astDispInhalI.path, CV_LOAD_IMAGE_GRAYSCALE);

        /*
        try {
            template1 = Utils.loadResource(this, R.drawable.templateplusbdedges2cropelipsebb, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            template2 = Utils.loadResource(this, R.drawable.templateplusbdedges2cropnomouthbb, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            template3 = Utils.loadResource(this, R.drawable.templateplusbdedges2crop, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        PrepareTemplate(hqImage);//, template1, template2, template3

        printf("Went to framecounter -1 value\n");
    }
    else
    {
        /*long currentFrameTime = System.currentTimeMillis();
        if(currentFrameTime - lastFrameTime >= 1000) */
        {
            ParseFrame(hqImage);
        }
    }
    UpdateOverlay(hqImage, templimgPtsDispHQ, true);
    frameCounter = (frameCounter+1)%4;

    return 0;
}


//------------------------------------------------------------------------------
int PrepareTemplate(cv::Mat* origImgPtr, std::string templateType, cv::Mat* template1, cv::Mat* template2, cv::Mat* template3)
{
    template1HQ = *template1;
    template2HQ = *template2;
    template3HQ = *template3;

    inhalStr = templateType;
    path = "";

    cv::Mat image = origImgPtr->clone();

    return PrepareTemplate(image);
}

int PrepareTemplate(cv::Mat& origImgPtr)
{

    if( (!template1HQ.data) || (!template2HQ.data) || (!template3HQ.data) ) // Check for invalid input
    {
        std::cout <<  "\nlog::Could not open or find the image.\n" << std::endl;

        if(!template1HQ.data)
            std::cout << "nlog::TemplateDial Failed to load\n\n";
        if(!template2HQ.data)
            std::cout << "nlog::TemplateCntr Failed to load\n\n";
        if(!template3HQ.data)
            std::cout << "nlog::TemplateDisplay Failed to load\n\n";
        return -1;
    }

    getTemplateProps(inhalStr, path, &astDialI, &astInhalCntrI, &astDispInhalI);

    // Resize all templates to have 70% of origImg width
    // Convert all templates to point list HQ

    double fxHQ;

    if(inhalStr == "diskus")
        fxHQ = CalcFxTemplDiskus(origImgPtr, template3HQ.size());
    else if(inhalStr == "novolizer")
        fxHQ = CalcFxTemplNovolizer(origImgPtr, template3HQ.size());
    else if(inhalStr == "ellipta")
        fxHQ = CalcFxTemplEllipta(origImgPtr, template3HQ.size());
    else
        fxHQ =  CalcFxTempl(origImgPtr, template3HQ.size());


    cv::resize(template1HQ, template1HQ, cv::Size(), fxHQ, fxHQ, cv::INTER_LINEAR);
    cv::resize(template2HQ, template2HQ, cv::Size(), fxHQ, fxHQ, cv::INTER_LINEAR);
    cv::resize(template3HQ, template3HQ, cv::Size(), fxHQ, fxHQ, cv::INTER_LINEAR);

    {
        cv::dilate(template3HQ, template3HQ, \
                   getStructuringElement(cv::MORPH_ELLIPSE, cv::Size(4, 4))); //orig 3,3
        cv::erode(template3HQ, template3HQ, \
                   getStructuringElement(cv::MORPH_ELLIPSE, cv::Size(6, 6)));
        cv::dilate(template3HQ, template3HQ, \
                   getStructuringElement(cv::MORPH_ELLIPSE, cv::Size(6, 6))); //orig 3,3
        // just for show, Comment if refined contour needed



        cv::dilate(template1HQ, template1HQ, \
                   getStructuringElement(cv::MORPH_ELLIPSE, cv::Size(4, 4)));
        cv::dilate(template2HQ, template2HQ, \
                   getStructuringElement(cv::MORPH_ELLIPSE, cv::Size(4, 4)));

    }

    cv::findNonZero(template1HQ, tplPts1hq); //for calculation and overlay of match
    cv::findNonZero(template2HQ, tplPts2hq);
    cv::findNonZero(template3HQ, tplPts3hq);

    //For display @ center
    templimgPtsDispHQ = tplPts3hq;
    applyCentBias2PtList(origImgPtr.size(), template3HQ.size(), templimgPtsDispHQ);


    // From here: resize again all templates for processing minimum size factor (LQ)
    // Convert all templates to point list LQ

    double fx2LQ =  CalcFxInput(origImgPtr);

    std::cerr << fx2LQ << "\n"; //DBG

    cv::resize(origImgPtr, lqImage, cv::Size(), fx2LQ, fx2LQ, cv::INTER_LINEAR);
    cv::resize(template1HQ, template1LQ, cv::Size(), fx2LQ, fx2LQ, cv::INTER_LINEAR);
    cv::resize(template2HQ, template2LQ, cv::Size(), fx2LQ, fx2LQ, cv::INTER_LINEAR);
    cv::resize(template3HQ, template3LQ, cv::Size(), fx2LQ, fx2LQ, cv::INTER_LINEAR);

    cv::findNonZero(template1LQ, tplPts1lq); //for calculation and overlay of match
    cv::findNonZero(template2LQ, tplPts2lq);
    cv::findNonZero(template3LQ, tplPts3lq);

    templimgPtsDispLQ = tplPts3lq; //for display @ center
    applyCentBias2PtList(lqImage.size(), template3LQ.size(), templimgPtsDispLQ);

    ///showColoredOverlayPts(lqImage, templimgPtsDispLQ, 0, 1, 0); //DBG here
    ///cv::waitKey(0);

    // Update asset Infos with respective factor
    updateAssetSize(&astDialI, fxHQ, fxHQ, 0,0);   //For display HQ
    updateAssetSize(&astInhalCntrI, fxHQ, fxHQ, 0, 0);
    updateAssetSize(&astDispInhalI, fxHQ, fxHQ, 0, 0);

    updateAssetSize(&astDialI, fx2LQ, fx2LQ, 0,0);   //For display LQ (and overwrite HQ disp info)
    updateAssetSize(&astInhalCntrI, fx2LQ, fx2LQ, 0, 0);
    updateAssetSize(&astDispInhalI, fx2LQ, fx2LQ, 0, 0);


    /*** When finally sure which matrices to never use again do Release
     *  template1LQ.release();
    ***/

    frameCounter = 0;
    origImgPtr.release();

    return 0;
}



//------------------------------------------------------------------------------
/**/
void ParseFrame(cv::Mat& imagePtr){

    //Pre-Setup
    imagePtr.copyTo(lqImage);

    if(lqImage.type() >= CV_8UC4 )    // SOS code for RGBA camera input (test in situ)
    {
        std::vector<cv::Mat1b> channels_rgba;
        cv::split(lqImage, channels_rgba);

        // create matrix with first three channels only
        std::vector<cv::Mat1b> channels_rgb(channels_rgba.begin(),
                                            channels_rgba.begin()+3);
        cv::Mat3b rgb;
        cv::merge(channels_rgb, rgb);
        rgb.copyTo(lqImage);
    }


    cv::Size diamdisk = cv::Size(7, 7);
    cv::Mat kernDisk = cv::getStructuringElement(cv::MORPH_ELLIPSE, diamdisk);


    //Reduce resolution
    double fx2LQ = CalcFxInput(lqImage);
    cv::resize(lqImage, lqImage, cv::Size(), fx2LQ, fx2LQ, cv::INTER_LINEAR);

    cv::Mat grey, grey2, edges, hsvimg;
    cv::Mat colorMask, colorMasked; //<< masked supposed to be global 4 nxt operations


    // Mild color (hue) thresholding
    cv::cvtColor(lqImage, hsvimg, cv::COLOR_BGR2HSV);
    cv::inRange(hsvimg, cv::Scalar(0, 0, 0), cv::Scalar(180, 138, 255), colorMask); //baseline values: Scalar(0, 0, 32)-Scalar(180, 64, 255)
    cv::dilate(colorMask, colorMask, kernDisk);
    cv::erode(colorMask, colorMask, kernDisk);

    cv::cvtColor(colorMask, colorMask, cv::COLOR_GRAY2BGR);
    cv::multiply(lqImage, colorMask/255, colorMasked);

    cvtColor(colorMasked, grey, cv::COLOR_BGR2GRAY);

    // Mild processing and Edge detection
    blur( grey, grey2, cv::Size(3,3) ); 		/// Reduce noise with a kernel 3x3
    Canny(grey2, edges, 0, 30, 3);

    if(frameCounter == 0)
        edgesMeanLQ = edges.clone()/4;
        //edgesMeanLQ = edges.clone();  //alternate version B
    else
        edgesMeanLQ = edgesMeanLQ + edges/4;
        //edgesMeanLQ = edgesMeanLQ/4 + edges*3/4; //alternate version B

    frameCounter = (frameCounter+1)%4;
}



//------------------------------------------------------------------------------

double locDiffExptd= 0,locDiffTpl= 0;
cv::Mat result0, result1;

bool inhalerDetection(cv::Mat& origImgPtr){

    int match_method = CV_TM_CCORR;
    int result_cols =  edgesMeanLQ.cols - template3LQ.cols + 1;
    int result_rows = edgesMeanLQ.rows - template3LQ.rows + 1;

    result0.create( result_rows, result_cols, CV_32FC1 );
    result1.create( result_rows, result_cols, CV_32FC1 );
    cv::matchTemplate( edgesMeanLQ, template1LQ, result0, match_method );
    cv::matchTemplate( edgesMeanLQ, template2LQ, result1, match_method );

    double sig = 1.5;
    int hs = (int)ceil(sig*4);
    hs += !(hs%2);
    cv::Size ksize = cv::Size(hs, hs);

    cv::GaussianBlur(result0, result0, ksize, sig, sig, cv::BORDER_REPLICATE );
    cv::GaussianBlur(result1, result1, ksize, sig, sig, cv::BORDER_REPLICATE );


    double min, max, min2, max2;
    cv::Point min_loc, min_loc2, max_loc, max_loc2;
    cv::minMaxLoc(result0, &min, &max, &min_loc, &max_loc);
    cv::minMaxLoc(result1, &min2, &max2, &min_loc2, &max_loc2);


    ///////Calculating detection:
    ///Setup
    cv::Point cbias( ( edgesMeanLQ.size() - template3LQ.size() )/2 );

    ///Find distance and position between templates and expected place

    cv::Point transpDial(astDialI.bias.x, astDialI.bias.y);
    cv::Point transpInhal(astInhalCntrI.bias.x, astInhalCntrI.bias.y);

    cv::Point tplDiff = (max_loc - transpDial) - (max_loc2 - transpInhal);
    //double locDiffTpl = sqrt( (tplDiff.x)*(tplDiff.x) + (tplDiff.y)*(tplDiff.y) );
    locDiffTpl = sqrt( (tplDiff.x)*(tplDiff.x) + (tplDiff.y)*(tplDiff.y) );     //GLOBAL FOR DEBUG

    /*  DBG
     * std::cerr << "loc1: " << max_loc << " | astDial: " << astDialI.bias <<"\nloc2: "
              << max_loc2 << " | astInh: " << astInhalCntrI.bias << "\n";
    std::cerr << "tplDiff: " << tplDiff << " | Dist: " << locDiffTpl << "\n";
    */

    cv::Point expcDevi = cbias - (max_loc2 - transpInhal);
    //double locDiffExptd = sqrt( (expcDevi.x)*(expcDevi.x) + (expcDevi.y)*(expcDevi.y) );
    locDiffExptd = sqrt( (expcDevi.x)*(expcDevi.x) + (expcDevi.y)*(expcDevi.y) );   //GLOBAL FOR DEBUG
    /*  DBG
     * std::cerr << "expcDevi: " << expcDevi << " | Dist: " << locDiffExptd << "\n";
     * std::cerr << "----------------------------------------------\n";
    */

    /*  For visual DBG, should not be needed */
     double fx2LQ = CalcFxInput(lqImage);
     overlayPtsHQ = tplPts1hq;
     applyBias2PtList(max_loc/fx2LQ, overlayPtsHQ);


    return (locDiffTpl <= MAX_PIX_DIST_MATCH) && (locDiffExptd <= MAX_PIX_DIST_MATCH); //detected
}

// Bernardo's legacy code
std::string InhalerDetectionStr()
{
    cv::Mat mat;
    bool detected = inhalerDetection(mat);

    std::ostringstream oss;
    oss << (detected ? "1" : "0") << "\r\n\r\n";
    oss << "locDiffExptd: " <<  locDiffExptd << "\tlocDiffTpl: " << locDiffTpl << "\r\n\r\n";
    //oss << "result0 size: " << result0.size << "\t cenas bias: " << astInhalCntrI.fullimgsize << "\r\n\r\n";
    return oss.str();
}

void ResizeImage(cv::Mat *imgPtr)
{
    cv::resize(*imgPtr, *imgPtr, cv::Size(640, 360));
}
