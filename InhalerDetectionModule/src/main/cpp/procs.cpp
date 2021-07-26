#ifndef _PROCS_
#define _PROCS_

#include "procs.h"

cv::Point colorExpectedInterval(double highH, double lowH, int* dualColor){

    int dualC = (fabs(highH-lowH)>10);
    if( dualColor!= NULL)
        (*dualColor) = dualC;

    cv::Point interv(0,0);

    if( (highH > HUE_GRN_MAX+10) || (lowH > HUE_GRN_MAX+10) || (highH == 0) || (lowH == 0) ){
        std::cerr << std::setprecision(6)<< "Unexpected Hue values for interval: " << highH << " | " << lowH << "\n";
        return interv;
    }

    if(dualC){
        if(highH >= HUE_GRN_MIN){   //YLW + GRN
            if(lowH < HUE_RED_MAX){
                std::cerr << std::setprecision(6)<< "Unexpected Hue values for interval: " << highH << " | " << lowH << "\n";
                return interv;
            }
            interv = cv::Point(ITVL_YLW_MAX, ITVL_GRN_MIN);
        }
        else if (highH >= HUE_YlW_MIN) {    //RED + YLW
            interv = cv::Point(ITVL_RED_MAX, ITVL_YLW_MIN);
        }
        else{
            std::cerr << std::setprecision(6)<< "Unexpected Hue values for interval: " << highH << " | " << lowH << "\n";
        return interv;
        }
    }
    else{
        if(highH >= HUE_GRN_MIN)   //GRN
            interv = cv::Point(ITVL_GRN_MIN, ITVL_GRN_MAX);
        else if(highH >= HUE_YlW_MIN)  //YLW
            interv = cv::Point(ITVL_YLW_MIN, ITVL_YLW_MAX);
        else
            interv = cv::Point(ITVL_RED_MIN, ITVL_RED_MAX);
    }

    return interv;
}

//------------------------------------------------------------------------------

std::vector<cv::Point> botPositionExpectedInterval(double percBot, bool ab){

    char binEdge[] = {120, 110, 100, 90, 80, 70, 60, 55, 50, 45, 40, 35, 30, 25, 20, 15, 10, 5, 0};
    float botEdgeDist[] = {0, 0.0664, 0.2815, 0.4228, 0.4193, 0.41, 0.3626, 0.4465, 0.5786, 0.7178, 0.7902, 0.8034, 0.8113, 0.8102, 0.796, 0.7859, 0.7772, 0.7579, 0.7418};
    //float topEdgeDist[] = {0.2579, 0.3243, 0.5393, 0.6807, 0.6771, 0.6679, 0.6205, 0.7043, 0.8365, 0.9757, 1.048, 1.061, 1.069, 1.068, 1.054, 1.044, 1.035, 1.016, 0.9997};
    std::vector<double> p(19, 0);
    std::vector<cv::Point> pairOut;
    for(int i=0; i<19; i++){
        p[i] = percBot-botEdgeDist[i];
        if(ab) p[i] = fabs(p[i]);


        cv::Point pt(binEdge[i], p[i]);
        pairOut.push_back(pt);
        std::cerr << (int)binEdge[i] << " | " << p[i] << "\n";
    }
    return pairOut;

/*
120|      0 | 0.2579
110| 0.0664 | 0.3243
100| 0.2815 | 0.5393
 90| 0.4228 | 0.6807
 80| 0.4193 | 0.6771
 70|   0.41 | 0.6679
 60| 0.3626 | 0.6205
 55| 0.4465 | 0.7043
 50| 0.5786 | 0.8365
 45| 0.7178 | 0.9757
 40| 0.7902 | 1.048
 35| 0.8034 | 1.061
 30| 0.8113 | 1.069
 25| 0.8102 | 1.068
 20|  0.796 | 1.054
 15| 0.7859 | 1.044
 10| 0.7772 | 1.035
  5| 0.7579 | 1.016
  0| 0.7418 | 0.9997
  */
}

//------------------------------------------------------------------------------
void getWheelDots(const cv::Mat& input, cv::Mat& output){

    cv::Mat tempM;
    input.copyTo(tempM);
    cv::Mat stats, cents;
    std::vector<int>::iterator it;

    int numels = connectedComponentsWithStats(input, tempM, stats, cents);

    //Get centroids and maximum, mean and minimum elongation (distance to center) for all labels

    fprintf(stderr, "all ok till here\n");

    cv::Mat rprops = cv::Mat::zeros( cv::Size(1, numels-1), CV_32F );//6


    for(int lab=1; lab<numels; lab++)
    {
        std::vector<int> xp, yp;
        int left = stats.at<int>(lab,0);
        int top = stats.at<int>(lab,1);
        int width = stats.at<int>(lab,2);
        int height = stats.at<int>(lab,3);

        double cx = 0.0;
        double cy = 0.0;

        for(int y=top; y<(top+height); y++)
            for(int x=left; x<(left+width); x++)
                if( tempM.at<int>(y,x) == lab)
                {
                    cx+= (double)x;
                    cy+= (double)y;
                    xp.push_back(x);
                    yp.push_back(y);
                }
        cx = cx/(float)xp.size();
        cy = cy/(float)yp.size();
        double mx = 0.0, mn = 999999.0, me=0;

        std::vector<int>::iterator it2 = yp.begin();
        for(it=xp.begin(); it != xp.end(); ++it, ++it2)
        {
            double a = (*it) - cx;
            double b = (*it2) - cy;
            double d = sqrt( a*a + b*b );
            //me += d;
            mx = MAX(mx, d);
            mn = MIN(mn, d);
        }
        //me = me/xp.size();
        float curArea = ((float)mx)*mx*M_PI;
        float aux = fabs( curArea-(float)xp.size() )/(float)xp.size();
        aux = ( (float)xp.size() >=5)? (( aux < 0.3 ) ? 1.0 : 0.0) : 0.0;
        rprops.at<float>(lab,0) = aux;
    }
    //printFloatcv::Mat(rprops);


    std::vector<int> radIdx;
    std::vector<float> dc;
    for(int i=0; i< stats.rows; i++)
        if (rprops.at<float>(i, 0) )
            radIdx.push_back( i );

    //Get object centroid distance to central point
    double cx=0.0, cy=0.0, d=0.0;
    //std::cerr << " cents rows: " << cents.rows << " | cols:"<< cents.cols << "\ncents type:" << cents.type() <<"\n";
    for(it=radIdx.begin(); it!=radIdx.end(); ++it)
    {
        cx = cents.at<double>( (*it), 0);
        cy = cents.at<double>( (*it), 1);

        //cx = cx-((double)input.cols)/2.0;
        //cy = cy-((double)input.rows)/2.0;
        cx = cx-272;
        cy = cy-350;

        d = sqrt(cx*cx+cy*cy);
        std::cerr << d <<"  | \n";
        dc.push_back(d);
    }

    //Sorting the distances and keeping the index
    std::vector<float> idxv, idxvO(dc.size());
    std::vector<float> dcCpy = dc;

    for(int j=0; j< dc.size(); ++j)
        idxvO[j] = j;
    sort( dcCpy.begin(), dcCpy.end());

    /* // gives natural order
    for(int k=0; k< dc.size(); ++k)
        for(int j=0; j< dc.size(); ++j)
            if( dcCpy[k]==dc[j] ){
                idxv.push_back(j);
                break;
            }
            */
    //gives calling order
    for(int j=0; j< dc.size(); ++j)
        for(int k=0; k< dc.size(); ++k)
            if( dcCpy[k]==dc[j] ){
                idxv.push_back(k);
                break;
            }


    std::cerr << "dc | idxvO  | dcCpy |  idxv\n";
    for(int j=0; j<dc.size(); ++j){
        std::cerr << dc[j] << " | " << idxvO[j] << " | "\
             << dcCpy[j] <<  " | " << idxv[j] << "\n";
    }

    //Retrieving only Dots & Relabeling-------------------------
    cv::Mat dots = cv::Mat::zeros( tempM.size(), CV_8UC1 );
    for(int y=0; y<tempM.rows; y++)
        for(int x=0; x<tempM.cols; x++)
        {
            if( tempM.at<int>(x,y) == 0)
                continue;
            int i;
            for(i=0, it = radIdx.begin(); it != radIdx.end(); ++it, ++i)
                if( (*it) == tempM.at<int>(x,y) )
                {

                    dots.at<char>(x,y) = 255-idxv[i]*8;//255
                    break;
                }
        }

    //Final attribution (can't be sure which is correct)
    //output = cv::Mat::zeros( dots.size(), CV_8UC1 );
    //dots.copyTo(output);
    output = dots.clone();

}


//------------------------------------------------------------------------------

int filterContoursWindow( cv::Mat& src, cv::Mat& out){

    out = cv::Mat::zeros( src.size(), CV_8UC1 );
    cv::Mat labM, stats, cents;

    int numels = connectedComponentsWithStats(src, labM, stats, cents);

    //Get area(pxcount stat4), bbArea, cvxhullArea

    cv::Mat rprops = cv::Mat::zeros( cv::Size(6, numels-1), CV_32F );//6
    cv::Mat kernel = getStructuringElement(cv::MORPH_ELLIPSE, cv::Size(80, 80));

    for(int lab=0; lab<numels-1; lab++)
    {
        int width = stats.at<int>(lab+1,2);
        int height = stats.at<int>(lab+1,3);
        int area = stats.at<int>(lab+1,4);

        double bbArea = width*(float)height;
        double xyratio = width/(float)height;

        rprops.at<float>(lab,0) = (float)area;
        rprops.at<float>(lab,1) = (float)bbArea;
        rprops.at<float>(lab,4) = (float)xyratio;

        //Calc closedObjs
        cv::Mat obj;
        obj = labM==(lab+1);
        morphologyEx(obj, obj, cv::MORPH_CLOSE, kernel);

        cv::Mat trash, st;
        connectedComponentsWithStats(obj, trash, st, cents);
        float closedArea = (float)st.at<int>(1,4);                      //label 0 is bckgrnd
        float areaRatio = (closedArea) ? fabs(bbArea/closedArea-1) : 0;  // avoid infinite values

        rprops.at<float>(lab,2) = closedArea;
        rprops.at<float>(lab,3) = areaRatio;

        int dec =(area>20) && (areaRatio < 0.2) && (xyratio>1.5);
        rprops.at<float>(lab,5)  = (float)dec;

        if(dec)
            out = out + obj;
    }
    //printFloatcv::Mat(rprops);


    cv::Mat verif (rprops, cv::Rect(5, 0, 1, numels-1));
    //printFloatcv::Mat(verif);

    //std::cerr<< "Size Verif:" << verif.size() << "\n";
    cv::Scalar hasObj = sum(verif);
    //std::cerr<< "hasObj:" << hasObj << "| hasObj[0]=" << hasObj[0]<<"\n";
/*
    if(!hasObj[0])
    {
        //reshow every obj
        for(int lab=0; lab<numels-1; lab++)
        {
            cv::Mat obj, obj2;
            obj = labM==(lab+1);
            morphologyEx(obj, obj2, MORPH_CLOSE, kernel);
            std::cerr<<"obj:"<< lab <<"\n";
            imshow("obj",obj);
            imshow("obj2",obj2);
            moveWindow("obj2", 700, 700);
            waitKey(0);
        }

    }

    imshow("out", out);
    waitKey(500);
*/

    return (int)hasObj[0];
}



//-------------------------------------------------------
int filterContoursWindow2( cv::Mat& src, cv::RotatedRect& outRect){

    cv::Mat hierarchy;
    std::vector< std::vector<cv::Point> > contours;
    findContours( src, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, cv::Point(0, 0) );

    std::vector<cv::RotatedRect> minRect( contours.size() );
    int found = -1;
    for( int i = 0; i < contours.size(); i++ ){
        minRect[i] = minAreaRect( cv::Mat(contours[i]) );

        if( contours[i].size() <= 5 )
            continue;
        //std::cerr << "minRect["<<i<<"]: S:" << minRect[i].size << " | ang:" << minRect[i].angle;
        int rotated = minRect[i].size.width < minRect[i].size.height;
        float mx = rotated? minRect[i].size.height : minRect[i].size.width;
        float mn = rotated? minRect[i].size.width : minRect[i].size.height;

        float ang = rotated?  ( (minRect[i].angle > 0)? (minRect[i].angle-90) : (minRect[i].angle+90)) : minRect[i].angle ;
        if( fabs(ang) > 4 ){
        //    std::cerr << " - Nope Angle\n";
            continue;
        }
        if( fabs(mx -100) > 20 ){
        //    std::cerr << " - Nope Width\n";
            continue;
        }
        if( fabs(mn -70) > 20 ){
        //    std::cerr << " - Nope height\n";
            continue;
        }/**/
        //std::cerr << " - Seems OK\n";
        found=i;
        break;
     }

    /*
    RNG rng;
    cv::Mat drawing = cv::Mat::zeros( src.size(), CV_8UC3 );
    for( int i = 0; i< contours.size(); i++ )
       {
        if( contours[i].size() <= 5 )
            continue;
        std::cerr << "minRect["<<i<<"]: S:" << minRect[i].size << " | ang:" << minRect[i].angle;

        int rotated = minRect[i].size.width < minRect[i].size.height;
        float mx = rotated? minRect[i].size.height : minRect[i].size.width;
        float mn = rotated? minRect[i].size.width : minRect[i].size.height;

        float ang = rotated?  ( (minRect[i].angle > 0)? (minRect[i].angle-90) : (minRect[i].angle+90)) : minRect[i].angle ;
        if( (fabs(ang) > 4) || (fabs(mx -100) > 15) || (fabs(mn -70) > 15) )
            continue;
        std::cerr << " - Seems OK\n";

         //Scalar color = Scalar( 100, 000, 220 );
         Scalar color = Scalar( rng.uniform(0,255), rng.uniform(0,255), rng.uniform(0,255) );

         drawContours( drawing, contours, i, color, 1, 8, std::vector<Vec4i>(), 0, Point() );
         // rotated rectangle
         Point2f rect_points[4]; minRect[i].points( rect_points );
         for( int j = 0; j < 4; j++ )
            line( drawing, rect_points[j], rect_points[(j+1)%4], color, 1, 8 );
       }

    imshow("drawrects", drawing);
    moveWindow("drawrects", 800, 1500 );
    cv::waitKey(0);
*/

    if(found!=-1){
        outRect = minRect[found];
        return 1;
    }
    else
        return 0;
}

//-------------------------------------------------------

template<typename T>
void myHistogram(T* hist, cv::Mat& m){

    for(int i=0; i<m.rows; ++i){
        const char* Mi = m.ptr<char>(i);
        for(int j = 0; j < m.cols; j++){
            if ( Mi[j] > 0)
                ++hist[Mi[j]];
        }
    }
}
//int hist[256] = {0};
//myHistogram(hist, satChan);

template<typename T>
double calcOtsu(T* hist){

    int u0[256] = {0}, u1[256] = {0};//mean
    int w0[256] = {0}, w1[256] = {0};//prob weight
    std::vector<float> s2; // intra class variance
    for(int i=0; i<256; i++){

        for(int j=0; j<i; j++){
            w0[i]+=hist[j];
            u0[i]+=j*hist[j];
        }
        if(w0[i])
            u0[i] /= w0[i];

        for(int j=i+1; j<256; j++){
            w1[i]+=hist[j];
            u1[i]+=j*hist[j];
        }
        if(w1[i])
            u1[i] /= w1[i];
        float udif = (float)(u0[i]-u1[i]);
        s2.push_back(w0[i]*udif*w1[i]*udif);
    }
    //Find best separation
    cv::Point thspt;
    cv::Mat intravar = cv::Mat(1, s2.size(), CV_32FC1, &s2[0]);
    minMaxLoc(intravar, NULL,  NULL, NULL, &thspt);
    double ths = thspt.x;
    return ths;
}

double getOtsuThs( cv::Mat & m){

    int hist[256] = {0}, aux=0;
    myHistogram(hist, m);

    return calcOtsu(hist);
}

double thsmiddle( cv::Mat& m){
    int hist[256] = {0};
    myHistogram(hist, m);

    double cnt =0.0, sum=0.0;
    for(int i=0; i<256; i++){
        sum += hist[i]*i;
        cnt += hist[i];
    }
    return sum/cnt;
}


//------------------------------------------------------------------------------


// (first) Description: Get the White background and decide the relative position of the crossing to color
// based on 1st-masking, 2nd-summing vertical the mask values, 3rd-decide the good separation with
// maximum of negative highpass filter of the mask horizontal profile
cv::Rect getColoredWindow( cv::Rect windowR, cv::Mat& satChan, cv::Mat&top ){

    double ths = getOtsuThs(satChan);
    int pL=-1, pR=-1;
    for(int tries=0; tries<MAX_TRIES; tries++){

        top = satChan.clone(); //Top Otsu label (ideally non-white parts)

        multiply(top, (top>ths)/255, top);

        if(tries>0){
            cv::Mat progKern = getStructuringElement( cv::MORPH_ELLIPSE, cv::Size(tries*4*MIXEDSCALE_MULT, tries*4*MIXEDSCALE_MULT) );
            erode(top, top, progKern);
            dilate(top, top, progKern);
        }
        //imshow("top", top);

        //Top "Label" X Saturation accumulated profile

        std::vector<double> xprofCnt, xderiv;
        xprofCnt.resize(top.cols, 0);
        xderiv.resize(top.cols, 0);

        for(int i=0; i<top.rows; ++i){
            const char* Mi = top.ptr<char>(i);
            for(int j = 0; j < top.cols; j++){

                xprofCnt[j]+= Mi[j];
                if(j>0)
                    xderiv[j] = xprofCnt[j]-xprofCnt[j-1];
            }
        }

        ///--------------
        char st[] = "./textdata.dat", st2[] = "./deriv.dat";
        exportArray(xprofCnt, st);
        exportArray(xderiv, st2);

        //std::cerr << "Check the plots\n";   /// NOT ENOUGH
        //while(waitKey(0)!='q');
        ///---------------

        cv::Mat deriv = cv::Mat(1, satChan.cols, CV_32FC1, &xderiv[0]);
        double vpder, vnder;
        cv::Point vnderL, vpderL;
        minMaxLoc(deriv, &vnder, &vpder, &vnderL, &vpderL);
        std::cerr << "pos: " << vpderL.x << " | neg: " << vnderL.x << "\n";

    //// Start with:....if distance is too small or large
    /// successive Morphological Open with increasingly large kernels
    // !!! if all this not enough, need to filter and for closest rise/fall with minimum and maximum distance expected

        double relDist = (vnderL.x-vpderL.x)/(double)windowR.width ;
        std::cerr << "width: " << windowR.width << " | Dist: " << (vnderL.x-vpderL.x) << " | Rel Dist: " << relDist << "\n";
        if(  (relDist>RDSTMIN) && (relDist < RDSTMAX) ){
            tries = -1;
            pL = (int)vpderL.x;
            pR = (int)vnderL.x;
            break;
        }

        std::cerr << "Press any key to advance\n";
        cv::waitKey(0);
    }


    if( pL==-1 ){
        std::cerr << "Dial not found. Re-aquiring\n";
        cv::waitKey(0);
        cv::Rect output(0,0,0,0);
        return output;
        //continue;
    }
    std::cerr << "FOUND THE COLORED DIAL!!!\n";

    // Calc BBox of Top and use top and bottom to adjust along with

    cv::Rect output = cv::Rect(windowR);
    output.x = pL;
    output.width = pR-pL;
    return output;
}


//------------------------------------------------------------------------------



int filterObjs4Dial( cv::Mat& objs, cv::Mat& grey, cv::Mat& out){

    out = cv::Mat::zeros( grey.size(), CV_8UC1 );
    cv::Mat labM, stats, cents;

    int numels = connectedComponentsWithStats(objs, labM, stats, cents);

    //Get area

    cv::Mat rprops = cv::Mat::zeros( cv::Size(5, numels-1), CV_32F );// can become 3 shift props after area & bbArea
    //cv::Mat kernel = getStructuringElement(MORPH_ELLIPSE, Size(80, 80));

    for(int lab=0; lab<numels-1; lab++)
    {
        int area = stats.at<int>(lab+1,4);
        //double bbArea = width*(float)height;
        //rprops.at<float>(lab,0) = (float)area;
        //rprops.at<float>(lab,1) = (float)bbArea;

        //Calc closedObjs
        cv::Mat obj;
        obj = labM==(lab+1);
        //morphologyEx(obj, obj, MORPH_CLOSE, kernel);

        if( area <=10 )
            continue;

        //Get Average masked intensity of label
        double sum=0.0, cnt=0.0;
        for(int i=0; i<obj.rows; ++i){
            const char* Mi = obj.ptr<char>(i);
            for(int j = 0; j < obj.cols; j++){
                if( Mi[j] ){
                    int auxi = grey.at<char>(i,j);
                    sum += (auxi<0)? auxi+256: auxi; //hard removing the 2's complement issue
                    ++cnt;
                }
            }
        }
        rprops.at<float>(lab,2) = (float)( (cnt>0) ? round(sum/cnt) : 0.0);
    }

    ////Get Otsu for selecting objs based on avg intensity
    //char st[] = "./textdata.dat";
    cv::Rect r(2, 0, 1, rprops.rows );
    cv::Mat mus;
    rprops(r).copyTo(mus);
    mus = mus.t();
    mus.convertTo(mus, CV_8UC1, 1, 0);

    double hist[256];
    memset(hist, 0, 256*sizeof(double));
    myHistogram(hist, mus);

    //std::vector<double> h2(hist, hist + sizeof(hist) / sizeof(double));
    //exportArray(h2, st);

    double ths = calcOtsu(hist);
    //std::cerr << "OTSU ths = " << ths << "\n\n";

    //// 1st Selection
    int cnt=0;
    for(int lab=0; lab<numels-1; lab++){
        rprops.at<float>(lab, 4) = lab;
        rprops.at<float>(lab, 3) = (rprops.at<float>(lab, 2) > 0) && (rprops.at<float>(lab, 2) <= ths);
        cnt += rprops.at<float>(lab, 3);
    }

    ////Double checking 1st Ths was good enough (too many objects)
    if(cnt > 6){ // Too many objects, even if fractured

        memset(&hist[(int)round(ths)], 0, (256-(int)round(ths))*sizeof(double));
        //h2.clear();
        //h2.insert(h2.end(), &hist[0], &hist[256]);
        //exportArray(h2, st);

        ths = calcOtsu(hist);
        //std::cerr << "OTSU2 ths = " << ths << "\n\n";

        for(int lab=0; lab<numels-1; lab++){
            rprops.at<float>(lab, 3) = (rprops.at<float>(lab, 2) > 0) && (rprops.at<float>(lab, 2) <= ths);
        }
    }

    //// Copying them accepted Objects
    cnt = 0;
    for(int lab=0; lab<numels-1; lab++){

        if(rprops.at<float>(lab, 3)){
            cnt++;
            cv::Mat obj;
            obj = labM==(lab+1);

            //std::cerr << "Lab: " << lab << ": Mu: "<< rprops.at<float>(lab, 2) << "\n";
            //imshow("DetObj", obj);


            for(int i=stats.at<int>(lab+1,1); i<( stats.at<int>(lab+1,3)+stats.at<int>(lab+1,1)); ++i){
                const char* Mi = obj.ptr<char>(i);
                for(int j = stats.at<int>(lab+1,0); j < ( stats.at<int>(lab+1,2) + stats.at<int>(lab+1,0) ); ++j){
                    if( Mi[j] )
                        out.at<char>(i,j) = cnt; //255
                }
            }

            //waitKey(0);
        }
    }
    //printFloatcv::Mat(rprops);

    return cnt; //(int)hasObj[0];
}

//------------------------------------------------------------------------------

cv::RotatedRect rmvTheDot( cv::Mat& labeledObjs){

    /// Find contours
    cv::Mat hierarchy;
    std::vector< std::vector<cv::Point> > contours;
    cv::Mat threshold_output = labeledObjs.clone(); //only reference, I guess
    findContours( threshold_output, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, cv::Point(0, 0) );

    /// Find the rotated rectangles and ellipses for each contour
    std::vector<cv::RotatedRect> minRect( contours.size() );
    std::vector<cv::RotatedRect> minEllipse( contours.size() );
    std::vector<double> dotRat( contours.size(), 0.0 );
    std::vector<int> dotDet( contours.size(), 0 );

    int cntDet=0;
    for( int i = 0; i < contours.size(); i++ ){
        minRect[i] = minAreaRect( cv::Mat(contours[i]) );
        if( contours[i].size() > 5 ){
            minEllipse[i] = fitEllipse( cv::Mat(contours[i]) );

            cv::Size s(minEllipse[i].size);
            float sratio = (s.width/(float)s.height);
            dotRat[i] = sratio;
            int mxs = (s.width>s.height) ? s.width : s.height;

            dotDet[i] = ( (sratio>=DOT_SIZE_RATIO) && (sratio<=1/DOT_SIZE_RATIO ) &&\
                          ((mxs)>DOT_SIZE_MINOMAX) && ((mxs)<DOT_SIZE_MAXPMAX)  );
            cntDet += dotDet[i];
            std::cerr << "s["<<i<<"]: " << s << " | ratio: " << sratio << " | det: " << (int)dotDet[i] << "\n";
        }
     }


    ////In case not found
    if(cntDet==0){
        cv::RotatedRect empt;
        //(cv::Point(0,0), cv:Size(0,0), 0);

        return empt;
    }

    ////Selecting the dot
    int idet=-1;
    double mn=99999;
    if(cntDet>1){ //multiple

        std::cerr << "dotRat.size=" << dotRat.size() <<"\n";

        for(int i=0; i<dotRat.size(); i++ ){
            if(dotDet[i]){
                dotRat[i] = fabs(1.0-dotRat[i]);
                std::cerr << "new[" << i<<"]=" << dotRat[i] << " ";
                if( mn > dotRat[i] ){
                    mn = dotRat[i];
                    idet = i;
                    std::cerr << "new idet";
                }
                std::cerr << "\n";
            }
        }
    }else{
        for(int i=0; i<dotDet.size(); i++ ){
            if(dotDet[i]){
                idet = i;
                break;
            }
        }

    }

    ///Removing the dot from objects mask
    labeledObjs(minRect[idet].boundingRect()) = 0;

    return minRect[idet];

    /*
            /// Draw contours + rotated rects + ellipses
            RNG rng;
            cv::Mat drawing = cv::Mat::zeros( threshold_output.size(), CV_8UC3 );
            for( int i = 0; i< contours.size(); i++ )
               {
                 //Scalar color = Scalar( 100, 000, 220 );
                 Scalar color = Scalar( rng.uniform(0,255), rng.uniform(0,255), rng.uniform(0,255) );

                 // contour
                 drawContours( drawing, contours, i, color, 1, 8, std::vector<Vec4i>(), 0, Point() );
                 // ellipse
                 ellipse( drawing, minEllipse[i], color, 2, 8 );
                 // rotated rectangle
                 Point2f rect_points[4]; minRect[i].points( rect_points );
                 for( int j = 0; j < 4; j++ )
                    line( drawing, rect_points[j], rect_points[(j+1)%4], color, 1, 8 );
               }

            imshow("drawcircs", drawing);
            moveWindow("drawcircs", 800, 1500 );
            cv::waitKey(0);
    */
}




//------------------------------------------------------------------------------


void performNumberDetection( cv::Mat& labeledObjs, std::vector<cv::Mat>& charsAvg,
                             std::vector<int>& MatchVals, std::vector<cv::Point>& MatchLocs){ //,

    int Match_method = CV_TM_CCORR_NORMED;
    std::vector<cv::Mat> MatchV;

    std::vector<int> vi;
    int cMaxi[3] = {-1,-1,-1};
    float cMax[3] = {-1.0,-1.0,-1.0};
    std::vector<cv::Point> pMax (3, cv::Point(0,0));
    for(int i=0; i<10;i++){

        int result_cols =  labeledObjs.cols - charsAvg[i].cols + 1;
        int result_rows = labeledObjs.rows - charsAvg[i].rows + 1;
        cv::Mat mtchRes;
        mtchRes.create( result_rows, result_cols, CV_32FC1 );
        matchTemplate( labeledObjs, charsAvg[i], mtchRes, Match_method );

        MatchV.push_back(mtchRes);

        double min, max;
        cv::Point min_loc, max_loc;
        minMaxLoc(mtchRes, &min, &max, &min_loc, &max_loc);

        if(max>cMax[0]){
            cMax[0] = max;
            cMaxi[0] = i;
            pMax[0] = max_loc;
        }
        std::cerr << "i:" << i << " | cMaxci:"<< cMaxi[0] << " | cMax: " << cMax[0] << " vs "<< max << " | pMax: " << pMax[0] << "\n";
    }

    if(cMax[0] < 0.6){ // Detected at least one number
        std::cerr << "No number detected\n";
        //waitKey(0);
        //continue;
        //return 0;
    }
    else{

        for(int i=0; i<10; i++){
            MatchV[i].convertTo( MatchV[i], CV_8UC1, 255/cMax[0], 0);
        }

        double rad = charsAvg[0].rows/3.5;

        for(int k=1; k<3;k++){

            cv::Point tempP(pMax[k-1]);
            tempP = tempP- cv::Point(tempP.x,rad);
            tempP.y = (tempP.y>0) ? tempP.y : 0;
            cv::Size  circext( MatchV[0].cols, rad*2  );
            circext.height = ( (circext.height+tempP.y) > MatchV[0].rows) ? ( MatchV[0].rows-tempP.y) : circext.height ;

            circext.width = ( (circext.width+tempP.x) > MatchV[0].cols) ? ( MatchV[0].cols-tempP.x) : circext.width ;
            cv::Rect rcirc(tempP , circext );

            //waitKey(0);
            for(int i=0; i<10; i++){
                MatchV[i](rcirc) = 0;

                double min, max;
                cv::Point min_loc, max_loc;
                minMaxLoc( MatchV[i], &min, &max, &min_loc, &max_loc);


                if( max > cMax[k] ){
                    cMax[k] = max;
                    cMaxi[k] = i;
                    pMax[k] = max_loc;
                }
                std::cerr << "i:" << i << " | cMaxi2:"<< cMaxi[k] << " | cMax2: " << cMax[k] << " vs "<< max << " | pMax2: " << pMax[k] << "\n";
            }
            std::cerr << "\n";


            if( (cMax[k] < NUMBS_REL_DETMIN*255) || \
                    ( abs(pMax[k].x - pMax[k-1].x)>(NUMBS_REL_MATCH_MAX_XDIST*MatchV[0].cols) )   ){
                // Detected 2nd number ?// Match is correctly positioned?

                std::cerr << "No " << k << "th number detected\n";

                pMax.resize( k ); //remove instead of nullifying values
                cMaxi[k] = -1;
                cMax[k] =-1;
                break;
            }
        }
        ////sort Numbers by Y position, (keep position to compare with Dots
        /// and get esticv::Mation
        std::vector<cv::Point> pMaxTemp(pMax);
        std::vector<int> aux;

        for(int i=0; i<pMaxTemp.size(); i++){
            aux.push_back(pMaxTemp[i].y +1 );
        }
        pMax.resize( 0 );
        int s=1;
        while(s > 0){

            int pi=0, mx=0;

            for(int i=0; i<aux.size();i++ ){
                if( aux[i] > mx ){
                    pi = i;
                    mx = aux[i];
                }
            }

            aux[pi] = 0;
            pMax.push_back(pMaxTemp[pi]);
            vi.push_back(cMaxi[pi]);

            s=0;
            for(int i=0; i<aux.size();i++ )
                s+=aux[i];
        }

        /*
        for(int i=0; i<pMax.size(); i++ ){
            std::cerr << "Char: " << vi[i] << " at: " << pMax[i] << "\n";
        }

        waitKey(0);
        */
    }
    /*
    for(int i=0; i<10;i++){
        imshow("Matches", MatchV[i]);
        waitKey(0);
    }*/

    MatchLocs = pMax;
    MatchVals = vi;
    //return MatchVals.size();
}


//------------------------------------------------------------------------------

/*
std::vector<std::vector<Point> > contours;
std::vector<Vec4i> hierarchy;
// Find contours
findContours( obj, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0) );

drawContours(obj, contours, -1, Scalar(128), -1, LINE_8);

imshow("obj", obj);
waitKey(0);*/



/*
void getWheelDots( const cv::Mat& input, cv::Mat& output){

    cv::Mat tempM;
    input.copyTo(tempM);
    cv::Mat stats, cents;
    std::vector<int>::iterator it;

    int numels = connectedComponentsWithStats(input, tempM, stats, cents);


    //tempM.convertTo(tempM, CV_8UC1, 255.0/numels, 0);
    //imshow("LabelPic", tempM);



    //Get centroids and maximum, mean and minimum elongation (distance to center) for all labels

    fprintf(stderr, "all ok till here\n");

    cv::Mat rprops = cv::Mat::zeros( Size(6, numels-1), CV_32F );


    for(int lab=1; lab<numels; lab++)
    {
        std::vector<int> xp, yp;
        int left = stats.at<int>(lab,0);
        int top = stats.at<int>(lab,1);
        int width = stats.at<int>(lab,2);
        int height = stats.at<int>(lab,3);

        double cx = 0.0;
        double cy = 0.0;

        for(int y=top; y<(top+height); y++)
            for(int x=left; x<(left+width); x++)
                if( tempM.at<int>(y,x) == lab)
                {
                    cx+= (double)x;
                    cy+= (double)y;
                    xp.push_back(x);
                    yp.push_back(y);
                }

        cx = cx/(float)xp.size();
        cy = cy/(float)yp.size();
        double mx = 0.0, mn = 999999.0, me=0;

        std::vector<int>::iterator it2 = yp.begin();
        for(it=xp.begin(); it != xp.end(); ++it, ++it2)
        {
            double a = (*it) - cx;
            double b = (*it2) - cy;
            double d = sqrt( a*a + b*b );
            me += d;
            mx = MAX(mx, d);
            mn = MIN(mn, d);
        }
        me = me/xp.size();

        rprops.at<float>(lab,0) = (float)xp.size();
        rprops.at<float>(lab,1) = (float)mn;
        rprops.at<float>(lab,2) = (float)me;
        rprops.at<float>(lab,3) = (float)mx;
        float curArea = ((float)mx)*mx*M_PI;
        rprops.at<float>(lab,4) = curArea;
        float aux = fabs( curArea-(float)xp.size() )/(float)xp.size();
        aux = ( (float)xp.size() >=5)? (( aux < 0.3 ) ? 1.0 : 0.0) : 0.0;
        rprops.at<float>(lab,5) = aux;



    }

    //printFloatcv::Mat(rprops);


    std::vector<int> radIdx;

    for(int i=0; i< stats.rows; i++)
    {

        if( rprops.at<float>(i,0) < 5.0) //minimal acceptable area, otherwise is noise
            continue;

        float temparea = rprops.at<float>(i,3)*rprops.at<float>(i,3)*M_PI;
        float pixnum = rprops.at<float>(i,0);

        if(  fabs(temparea-pixnum) < 0.3*pixnum ) // 0.3
            radIdx.push_back( i );
    }

    cv::Mat dots = cv::Mat::zeros( tempM.size(), CV_8UC1 );
    for(int y=0; y<tempM.rows; y++)
        for(int x=0; x<tempM.cols; x++)
        {
            if( tempM.at<int>(x,y) == 0)
                continue;

            for(it = radIdx.begin(); it != radIdx.end(); ++it)
                if( (*it) == tempM.at<int>(x,y) )
                    dots.at<char>(x,y) = 255;
        }

    //Final attribution (can't be sure which is correct)
    //output = cv::Mat::zeros( dots.size(), CV_8UC1 );
    //dots.copyTo(output);
    output = dots.clone();

}
*/











// ----------------Function parts to not forget: -----------------------------

//std::cerr << "procp: rows: " << procp.rows << " | cols: " << procp.rows << " | Type: " << procp.type() << '\n';


//cv::moments	(	InputArray 	array, bool 	binaryImage = false  )
//void cv::HuMoments(const Moments & 	moments, double 	hu[7] )


/*
cv::Mat binclose, binopen;
//Image Closing
diamdisk = Size(3, 3);
kernDisk = getStructuringElement(MORPH_ELLIPSE, diamdisk);
temp = binOtsu.clone();
binclose = binOtsu.clone();
dilate(temp, binclose, kernDisk);
erode(binOtsu, temp, kernDisk);


//Image Opening
diamdisk = Size(4, 4);
kernDisk = getStructuringElement(MORPH_ELLIPSE, diamdisk);

binopen = binclose.clone();
erode(binclose, temp, kernDisk);
dilate(temp, binopen, kernDisk);

imshow("bin2", binopen );
moveWindow("bin2", image.cols+9, image.rows+9);
*/



/*
blur( grey, grey, Size(3,3) ); 		/// Reduce noise with a kernel 3x3
Canny(grey, edges, 0, 30, 3);
*/


/*
//cv::Mat kern; // = cv::Mat_<float>(3, 3);
cv::Mat kern = cv::Mat_<float>(3, 3);
myLoGKernel(kern, 3.0);
std::cerr << "kern: rows: " << kern.rows << " | cols: " << kern.rows << '\n';
std::cerr << "\n\nDST FInal values:\n";
printcv::Mat(kern);
*/


/*
    std::cerr << "GREY: rows: " << grey.rows << " | cols: " << grey.rows << " | Type: " << grey.type() << '\n';
//	std::cerr << "kern2: rows: " << kern2.rows << " | cols: " << kern2.rows << '\n';
    std::cerr << "kern: rows: " << kern.rows << " | cols: " << kern.rows << '\n';
    std::cerr << "edges: rows: " << edges.rows << " | cols: " << edges.rows << " | Type: " << edges.type() << '\n';
*/


//cv::Mat dst = cv::Mat_<float>(grey.rows, grey.cols);
//normalize(edges, dst, 0, 255, cv::NORM_MINMAX);


//mynorm(dst, 0, 1);



/*
cv::Mat temp = procp.clone();
double min, max;
minMaxLoc(procp, &min, &max);
temp -= (float)min;
temp = temp/((float)(max-min));
temp.convertTo(temp, CV_8UC1, 255, 0);

imshow("rescale", temp );
moveWindow("rescale", 0, image.rows+9);
*/

//cv::Mat binOtsu = temp.clone();
//threshold(	temp, binOtsu, 0, 255, CV_THRESH_BINARY | CV_THRESH_OTSU);


//int *areaIdx = (int*)malloc( sizeof(int) * stats.rows );
//free(areaIdx)


/*
 * Local Extrema and some filtering
std::vector<float> lm, lmn;
std::vector<int> lml, lmnl;

findLocExtrema1D(xderiv, satChan.cols, lm, lmn, lml, lmnl);

double globmx, globmn;
minMaxLoc(deriv, &globmn, &globmx, NULL, NULL);

for(int i=lm.size()-1; i>=0; i--)   //remove tiny peaks
    if(globmx*0.01>= (double)lm[i]){
        lm.erase (lm.begin()+i);
        lml.erase (lml.begin()+i);
    }


for(int i=0; i<lm.size(); i++)
    std::cerr << lml[i] << " | " << lm[i] << "\n";
std::cerr << "\n";

*/




/*
int blockSize = 3;  //dunno what these do...opencv site is down
int apertureSize = 5;
double k = 0.04;
cornerHarris( elipGrey, ccornsHornsH, blockSize, apertureSize, k, BORDER_DEFAULT );
*/


/* ///Overlap a template, altering displayed image (reduce intensity)
 *
cv::Mat ovlDisp = image.clone(), ovlpTemp = teplimgOver.clone();
cv::cvtColor(ovlpTemp, ovlpTemp, COLOR_GRAY2BGR);
cv::multiply(ovlDisp, ovlpTemp/255, ovlpTemp);
ovlDisp += ovlpTemp/4;
cv::imshow("pic", ovlDisp);
*/


/* /// Invalidating a Match with a rect
        cv::Point tempP(pMax);
        double tdoub = tempP.y -0.05*MatchV[0].rows;
        tempP.y = (tdoub>0) ? tdoub : 0;
        tdoub = tempP.x -0.05*MatchV[0].cols;
        tempP.x = (tdoub>0) ? tdoub : 0;
        Rect r( tempP, cv::Size(   charsAvg[0].cols,  \
                (charsAvg[0].rows+tempP.y)>MatchV[0].rows ? ( MatchV[0].rows-tempP.y) : charsAvg[0].rows) );
        Rect topR( cv::Point(0, 0), cv::Size(  0.95*pMax.x, MatchV[0].rows ) );


        double rad = charsAvg[0].rows/3.5;
        cv::Point tempP(pMax[0]);
        tempP = tempP- cv::Point(tempP.x,rad);
        tempP.y = (tempP.y>0) ? tempP.y : 0;
        //tempP.x = (tempP.x>0) ? tempP.x : 0;
        //cv::Size  circext( rad*2 , rad*2 );
        cv::Size  circext( MatchV[0].cols, rad*2  );
        //std::cerr << "circext.height: " << circext.height;
        circext.height = ( (circext.height+tempP.y) > MatchV[0].rows) ? ( MatchV[0].rows-tempP.y) : circext.height ;
        //std::cerr << " | MatchV[0].rows: " << MatchV[0].rows << " | tempP.y: " << tempP.y <<\
        //        "\n= circext.height: " << circext.height << "\n";
        //std::cerr << "circext.width: " << circext.width;
        circext.width = ( (circext.width+tempP.x) > MatchV[0].cols) ? ( MatchV[0].cols-tempP.x) : circext.width ;
        //std::cerr << " | MatchV[0].cols: " << MatchV[0].cols << " | tempP.y: " << tempP.y << \
        //       "\n= circext.width: " << circext.width << "\n";
        Rect rcirc(tempP , circext );
        //std::cerr << "rcirc: " << rcirc << "\n";

*/

#endif
