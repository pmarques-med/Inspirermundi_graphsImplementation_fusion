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


double myDist(cv::Point a, cv::Point b ){
    a -= b;
    return sqrt( (a.x)*(a.x) + (a.y)*(a.y) );
}

//%#define SIGNOF(x) ( ( (x) > 0.0 ) ? 1 : -1)
double normTo2Pi(double x){
    x = fmod(x, 2*M_PI);
    if (x < 0)
        x += 2*M_PI;
    return x;
}

double normToPi(double x){
    x = normTo2Pi(x);
    if( x > M_PI )
        x -= 2*M_PI;
    return  x;
}

double mapToPi14Q( double x )
{
    x = normToPi(x);
    if( fabs(x) > M_PI/2.0)
        x -= SIGNOF(x)*2*M_PI;
    return  x;
}




void printMat(cv::Mat& m)
{
    //cv::Mat C = (cv::Mat_<double>(3,3) << 0, -1, 0, -1, 5, -1, 0, -1, 0);
/*
	int x=0, y=0;
	for(y=0; y < m.rows; y++)
	{
		for(x=0; x < m.cols; x++)
		{
			std::cerr << (double) m.data[  y*m.cols + x ] << " | ";
		}
		std::cerr << '\n';
	}
	*/
}


void printFloatMat(cv::Mat& m)
{
    /*
    int x=0, y=0;
    for(y=0; y < m.rows; y++)
    {
        for(x=0; x < m.cols; x++)
        {
            std::cerr << std::setw(8) << std::setprecision(6)<< (double) m.at<float>(y,x)<< " | ";
        }
        std::cerr << '\n';
    }
    std::cerr << '\n' << std::flush;
    */
}


template<typename T>
void printArray(T *arr, int N){

    /*
    std::cerr<< "Array:\n";
    for(int i=0; i<N; i++){
        std::cerr << arr[i] << " | ";
        if(!((i+1)%20))
            std::cerr << "\n";
    }
    std::cerr << "\n";
    */
}


void rot90(cv::Mat &matImage, int rotflag){
    //1=CW, 2=CCW, 3=180
    if (rotflag == 1){
        transpose(matImage, matImage);
        flip(matImage, matImage,1); //transpose+flip(1)=CW
    } else if (rotflag == 2) {
        transpose(matImage, matImage);
        flip(matImage, matImage,0); //transpose+flip(0)=CCW
    } else if (rotflag ==3){
        flip(matImage, matImage,-1);    //flip(-1)=180
    } else if (rotflag != 0){ //if not 0,1,2,3:
        //std::cout  << "Unknown rotation flag(" << rotflag << ")" << "\n";
    }
}



/*
#define _USE_MATH_DEFINES
#include <math.h>
#include <stdint.h>
*/
void myLoGKernel(cv::Mat& dst, double sigm)
{
    int x=0, y=0, hsize = ceil(sigm * 3);
    cv::Mat aux = cv::Mat_<float>(hsize, hsize);
    //cv::Mat aux = Mat::zeros(hsize, hsize, CV_32F);

    double const c0 = -1/(M_PI*(sigm*sigm)*(sigm*sigm));
    double const c1 = -1/(2*(sigm*sigm));
    double v0 = 0.0;

    double bias = hsize/2;
    double xa=0.0, ya=0.0;

    for(y=0; y < aux.rows; y++)
    {
        for(x=0; x < hsize; x++)
        {
            //if (y==0 || x==0)
            xa = (x-bias)*2;
            ya = (y-bias)*2;
            v0 = ( xa*xa + ya*ya )*c1;
            aux.at<double>(x, y) = ( c0*( 1 + v0 ) * exp( v0 ) );
        }
    }

    aux.copyTo(dst);
//	return aux;
}


/*
Assumes that sigma is a third of the hzise
*/
void replaceByLoGKernel(cv::Mat& aux, double hsize) //,
{
    float sigm = hsize/3.0;

    int x=0, y=0;

    double const c0 = -1/(M_PI*(sigm*sigm)*(sigm*sigm));
    double const c1 = -1/(2*(sigm*sigm));
    double v0 = 0.0;

    double bias = hsize/2.0;
    double xa=0.0, ya=0.0;

    for(y=0; y < aux.rows; y++)
    {
        for(x=0; x < hsize; x++)
        {
            //if (y==0 || x==0)
            xa = (x-bias)*2;
            ya = (y-bias)*2;
            v0 = ( xa*xa + ya*ya )*c1;
            //aux.at<float>(x, y) = ( c0*( 1 + v0 ) * exp( v0 ) );
            aux.at<float>(x, y) = ( c0*( 1 + v0 ) * exp( v0 ) );
        }
    }

}




void mynorm(cv::Mat& aux, float newmin, float newmax)
{
    float max=-9999999;
    float min= 9999999;
    int p=0;

    int mpos = aux.rows * aux.cols;
    for( p=0; p<mpos; p++)
    {
        max = ( aux.data[p] > max ) ? aux.data[p] : max;
        min = ( aux.data[p] < min ) ? aux.data[p] : min;
    }
    aux = (aux - min) / (max-min) * ( newmax-newmin) + newmin;

}




void drawCentSQR(cv::Mat &m, float perc, int channel, int val)
{

    int x = round(m.cols/2);
    int y = round(m.rows/2);

    int top = (int) MAX( (float)y - perc*m.rows, 0 );
    int bot = (int) MIN( (float)y + perc*m.rows, m.rows );
    int left = (int) MAX( (float)x - perc*m.cols, 0 );
    int right = (int) MIN( (float)x + perc*m.cols, m.cols );

    for(int i = top; i<bot; i++)
        for(int j=left; j<right; j++)
            m.at<cv::Vec3b>(j, i)[channel] = val;
}

void drawCentCross(cv::Mat &m, float perc, int channel, int val)
{

    int x = round(m.cols/2);
    int y = round(m.rows/2);

    int top = (int) MAX( (float)y - perc*m.rows, 0 );
    int bot = (int) MIN( (float)y + perc*m.rows, m.rows );
    int left = (int) MAX( (float)x - perc*m.cols, 0 );
    int right = (int) MIN( (float)x + perc*m.cols, m.cols );

    int i=0, j=0;

    j = x;
    for(i = top; i<bot; i++)
        m.at<cv::Vec3b>(j, i)[channel] = val;
    i = y;
    for(j=left; j<right; j++)
        m.at<cv::Vec3b>(j, i)[channel] = val;
}


void drawCenteredLine(cv::Mat &m, cv::Size posxy, float theta, int channel, int val)
{
    float x = posxy.width;
    float y = posxy.height;

    theta = normTo2Pi(theta);
    float theta2 = theta + M_PI/2.0;
    float a = -tan( mapToPi14Q(theta) ); //invert due to y being inverted
    float tempV = 0.0;

    int i=0, j=0;
    // X forward direction
    int itp = (theta2 <= M_PI)? 1 : -1 ;

    for(i = (int)x; (i<m.cols) && (i>=0); i+= itp)
    {

        tempV = (int) ( a*((float)i-x) + y ); 	// dealing with infinity values
        if( (tempV > (float)m.rows) || (tempV<0.0)  ) //Extremes of window
            break;

        if(theta <= M_PI){	//Beyond stating pos
            if( tempV > y  )
                break;
        }
        else{
            if( tempV < y  )
                break;
        }

        j = (int) tempV;
        m.at<cv::Vec3b>(j, i)[channel] = val;
    }


    // Reverse direction (use Y)
    itp = (theta <= M_PI)? 1 : -1 ;

    for(j = (int)y; (j<m.rows) && (j>=0); j+= itp)
    {
        tempV = (int) ( ((float)j-y)/a + x ); 	// dealing with infinity values
        if( (tempV > (float)m.cols) || (tempV<0.0)  ) //Extremes of window
            break;
        if(theta2 <= M_PI){	//Beyond stating pos
            if( tempV < x  )
                break;
        }
        else{
            if( tempV > x  )
                break;
        }
        i = (int) tempV;
        m.at<cv::Vec3b>(j, i)[channel] = val;
    }


}


void reDrawOvrlp(cv::Mat& overlp, cv::Mat& teplimg, cv::Mat& teplimg2, cv::Point max_loc, cv::Point max_loc2, int detected, double locD, double locDc, std::string win){


    cv::Size cbias = ( overlp.size()-teplimg.size() )/2;

    // Colour change for provisional display
    for(int i=0; i<teplimg.rows; ++i){
        const char* Mi = teplimg.ptr<char>(i);
        const char* Mi2 = teplimg2.ptr<char>(i);
        for(int j = 0; j < teplimg.cols; j++){

            if ( Mi[j] > 0)
            {
                //Expected position
                overlp.at<cv::Vec3b>(i+cbias.height, j+cbias.width)[2] = 32;
                overlp.at<cv::Vec3b>(i+cbias.height, j+cbias.width)[1] = 32;
                overlp.at<cv::Vec3b>(i+cbias.height, j+cbias.width)[0] = 255;

                //Detected Position Template1
                overlp.at<cv::Vec3b>(i+max_loc.y, j+max_loc.x)[2] = 128;
                overlp.at<cv::Vec3b>(i+max_loc.y, j+max_loc.x)[1] = 128;
            }
            if ( Mi2[j] > 0) //Detected Position Template2 (elipse)
                overlp.at<cv::Vec3b>(i+max_loc2.y, j+max_loc2.x)[2] = 255;

            if ( ( locD < MAX_PIX_DIST_MATCH_a ) && ( ( Mi2[j] > 0 ) || ( Mi[j] > 0 ) ) ) //5 pixels of distance threshold
            {
                if(detected)
                    overlp.at<cv::Vec3b>(i+max_loc2.y, j+max_loc2.x)[1] = 255;
                else
                    overlp.at<cv::Vec3b>(i+max_loc2.y, j+max_loc2.x)[1] = 128;
                overlp.at<cv::Vec3b>(i+max_loc2.y, j+max_loc2.x)[2] = 128;
                overlp.at<cv::Vec3b>(i+max_loc2.y, j+max_loc2.x)[0] = 128;
            }

        }
    }

    if( win=="" )
        cv::imshow("overlp", overlp);
    else
        cv::imshow(win, overlp);

}


//--------------------------------------------------------
/*
#include <iostream>
#include <fstream>
*/

template<typename T>
int exportArray(T arr, char* filename){

    /*
    std::ofstream myfile;
    myfile.open (filename);
    if(! myfile.is_open())
        return -1;

    for(int i=0; i<arr.size(); i++)
        myfile << arr[i] << "\n";
    myfile.close();
     */
    return 0;
}

/*
char st[] = "./textdata.dat", st2[] = "./deriv.dat";
exportArray(xprofCnt, st);
exportArray(xderiv, st2);
*/

//--------------------------------------------------------
/*
struct imgAssetInfo {
  cv::Size fullimgsize;
  cv::Size size;
  cv::Point bias;
  std::string path;
};

/*struct imgAssetInfo;/**/

void updateAssetSize(imgAssetInfo *ast, double fx, double fy, double bx, double by){

    ast->bias.x = (ast->bias.x) *fx + bx;
    ast->bias.y = (ast->bias.y) *fy + by;
    ast->size.width *= fx;
    ast->size.height *= fy;
    ast->fullimgsize.width *= fx;
    ast->fullimgsize.height *= fy;
}


void correctForBias(imgAssetInfo *ast, cv::Point *maxp, cv::Point *minp){

    maxp->x -= ast->bias.x;
    maxp->y -= ast->bias.y;
    minp->x -= ast->bias.x;
    minp->y -= ast->bias.y;
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
    //cv::flip(ovlDisp, ovlDisp, 1); //flipping just for my PC feedback, comment otherwise
    //cv::imshow("pic", ovlDisp);
}

void showColoredOverlayPts(cv::Mat* ovlDisp, std::vector<cv::Point> teplimgOverPts, char b, char g, char r){

    std::vector<cv::Point>::iterator it;// = teplimgOverPts.begin();
    for(it=teplimgOverPts.begin(); it != teplimgOverPts.end(); ++it){

        if ( ( (ovlDisp->cols) <= ((*it).x) ) || ( (ovlDisp->rows) <= ((*it).y) ) || ( 0 > ((*it).x) ) || ( 0 >  ((*it).y) ) )
            continue;
        else {
            ovlDisp->at<cv::Vec3b>((*it).y, (*it).x)[0] = b * 128;
            ovlDisp->at<cv::Vec3b>((*it).y, (*it).x)[1] = g * 128;
            ovlDisp->at<cv::Vec3b>((*it).y, (*it).x)[2] = r * 128;
        }
    }

    //cv::flip(ovlDisp, ovlDisp, 1); //flipping just for my PC feedback, comment otherwise
}


//-------------------------------------------------------

void loadAvgNumbers(std::string pathchars, cv::Mat& charsAvg){
    std::vector<cv::Mat> matVec;

    /*
    ///TODO::open fileList of chars
    DirListing_t dirtree, dirtree2;
    get1stLvlDir(pathchars, dirtree, dirtree2);
    if(dirtree2.size()==0){
        std::cerr << "\n\nError dirtree2 is empty!!\n";
        return;
    }

    for(int i=0; i<10; i++){
        DirListing_t dirfull(dirtree), dirshort(dirtree2);
        std::vector<cv::Mat> temp;

        //Remove paths of different number
        for(int n = dirshort.size()-1; n >=0 ; n--){
            //std::cerr << dirshort[n] << "\n";
            //std::cerr << dirfull[n] << "\n";

            if ( dirshort[n].at(0) != ('0'+i) ){
                dirfull.erase(dirfull.begin()+n); //full
                dirshort.erase(dirshort.begin()+n);   //short
            }
        }

        if(dirfull.size()==0){
            std::cerr << "\n\ndirfull is empty!!\n";
            return;
        }

        //For each number import images
        cv::Size sz;
        for(int j=0; j<dirfull.size(); j++){

            cv::Mat pic = cv::imread(dirfull[j], CV_LOAD_IMAGE_GRAYSCALE);
            temp.push_back(pic/dirshort.size());

            cv::Size sp = pic.size();
            sz.width = MAX(sz.width, sp.width);
            sz.height = MAX(sz.height, sp.height);
        }
        if(sz==cv::Size(0,0)){
            std::cerr << "\n\ncv::Size[" << i <<"] is empty!!\n";
            return;
        }



        //(save in vector) make avg of same number templates
        cv::Mat aux = cv::Mat::zeros(sz, CV_8U);

        for(int j=0; j<dirfull.size(); j++){
            cv::Point bias( (sz-temp[j].size() )/2 );
            cv::Rect r(bias, temp[j].size());
            aux(r) += temp[j];
        }
        /// TODO::display each MatAvg out of vector
        matVec.push_back(aux);
        //cv::imshow("aux", aux);
        //waitKey(0);
    }

    //Uniformize the templates' sizes
    cv::Size maxs(0,0);
    for(int i=0; i< matVec.size(); i++){
        cv::Size diff( matVec[i].size() - maxs );
        diff.width = (diff.width)>0? diff.width: 0;
        diff.height = (diff.height)>0? diff.height: 0;
        maxs = maxs + diff;
    }
    for(int i=0; i< matVec.size(); i++){
        cv::Size diff( maxs-matVec[i].size() );
        cv::copyMakeBorder(	matVec[i], matVec[i],\
              0,  diff.height, diff.width, 0, cv::BORDER_CONSTANT, cv::Scalar(0) );
        rot90(matVec[i], 2);
    }
    cv::merge(matVec, charsAvg);
    */
}

//---------------------------------
cv::Point oneObjCentroid(cv::Mat& m){

    double px=0.0, py=0.0, cnt=0.0;
    for(int i=0; i<m.rows; ++i){
        const char* Mi = m.ptr<char>(i);
        for(int j = 0; j < m.cols; j++)
            if ( Mi[j] > 0)
            {
                px+=j;
                py+=i;
                cnt+=1.0;
            }
    }
    px/=cnt;
    py/=cnt;
    cv::Point seedPoint((int)px, (int)py );
    return seedPoint;
}

//-------------------------------------------------------

void CalcMatAverage(cv::Mat *buffer[], int bufferSize, cv::Mat &average)
{
    if(buffer[0] == nullptr)    //due to entry being from right to left, this effectively checks that
        return;                 //the buffer is full before it starts calculating the average

    average = (buffer[0])->clone();

    for(int i = 0 ; i < bufferSize; i++)
    {
        average += (*(buffer[i]))/ bufferSize;
    }
}

void InsertMat(cv::Mat* buffer[], int bufferSize, cv::Mat* newMat)
{
    if(bufferSize <= 1)
        return;

    for(int i = 0; i < bufferSize - 1; i++)
    {
        buffer[i] = buffer[i+1];
    }

    buffer[bufferSize - 1] = newMat;
}

#endif
