#ifndef _TEMP_PROPS_CPP_
#define _TEMP_PROPS_CPP_

#include "templateProps.h"



int inhalStr2IID(std::string inhalStr){
    const char* args[] = {"flutiform", "turbohaler", "diskus", "novolizer", "spiromax", "ellipta", "easyhaler", "khaler", "mdi", "nexthaler", "seretide", "twisthaler"};
    std::vector<std::string> v(args, args + sizeof(args)/sizeof(args[0]));

    for(int i=0; i< v.size(); i++){
        if(inhalStr.compare(v[i])==0)
            return i;
    }
    return -1;
}



int getTemplateProps(std::string inhalStr, std::string path, imgAssetInfo *astDial, imgAssetInfo *astInhalCntr, imgAssetInfo *astDispInhal){

    //path = path+"template/"; //LEGACY

    switch( inhalStr2IID(inhalStr) ){
        case 0: //flutiform
            //path = path+"flutiformTpl/";
        //fullimgsize        //.size         //.bias     //path
            astDial->fullimgsize = cv::Size(735, 290);
            astDial->size = cv::Size(173, 206);
            astDial->bias = cv::Point(195, 33);
            //astDial->path = path+"templatePlusBDEdges2CropElipseBB11.jpg";

            astInhalCntr->fullimgsize = cv::Size(735, 290);
            astInhalCntr->size = cv::Size(530, 290); //bottom was cut out
            astInhalCntr->bias = cv::Point(0, 0);
            //astInhalCntr->path = path+"templatePlusBDEdges2CropNoMouthBB.jpg";

            astDispInhal->fullimgsize = cv::Size(735, 290);
            astDispInhal->size = cv::Size(735, 290);
            astDispInhal->bias = cv::Point(0, 0);
            //astDispInhal->path = path+"templatePlusBDEdges2Crop.jpg";

            break;

        case 1:
            //path = path+"turbohalerTpl/";

            astDial->fullimgsize = cv::Size(687, 278);
            astDial->size = cv::Size(335, 240);
            astDial->bias = cv::Point(188, 18);
            //astDial->path = path+"turbohalerTemplate1_Canny_cropBB.jpg";

            astInhalCntr->fullimgsize = cv::Size(687, 278);
            astInhalCntr->size = cv::Size(687, 278);
            astInhalCntr->bias = cv::Point(0, 0);
            //astInhalCntr->path = path+"turbohalerTemplate_Canny_crop2.jpg";

            astDispInhal->fullimgsize = cv::Size(687, 278);
            astDispInhal->size = cv::Size(687, 278);
            astDispInhal->bias = cv::Point(0, 0);
            //astDispInhal->path = path+"turbohalerTemplate_Canny_crop2.jpg";
            break;

        case 2: //diskus
            //path = path+"diskusTpl/";

            /*
            astDial->fullimgsize = cv::Size(735, 718);
            astDial->size = cv::Size(344, 718);
            astDial->bias = cv::Point(0, 0);
            //astDial->path = path+"diskus3_canny_cleanedCropCoarseB2.jpg";

            astInhalCntr->fullimgsize = cv::Size(735, 718);
            astInhalCntr->size = cv::Size(735, 718);
            astInhalCntr->bias = cv::Point(0, 0);
            //astInhalCntr->path = path+"diskus3_canny_cleanedCropCoarse.jpg";

            astDispInhal->fullimgsize = cv::Size(735, 718);
            astDispInhal->size = cv::Size(735, 718);
            astDispInhal->bias = cv::Point(0, 0);
            //astDispInhal->path = path+"diskus3_canny_cleanedCropCoarse.jpg";

             */
            //for new diskus template
            astDial->fullimgsize = cv::Size(290, 590);
            astDial->size = cv::Size(290, 590);
            astDial->bias = cv::Point(0, 0);
            //astDial->path = path+"diskus3_canny_cleanedCropCoarseB2.jpg";

            astInhalCntr->fullimgsize = cv::Size(290, 590);
            astInhalCntr->size = cv::Size(290, 290);
            astInhalCntr->bias = cv::Point(0, 0);
            //astInhalCntr->path = path+"diskus3_canny_cleanedCropCoarse.jpg";

            astDispInhal->fullimgsize = cv::Size(290, 590);
            astDispInhal->size = cv::Size(290, 590);
            astDispInhal->bias = cv::Point(0, 0);
            //astDispInhal->path = path+"diskus3_canny_cleanedCropCoarse.jpg";

            break;

        case 3: //novolizer
            //path = path+"novolizerTpl/";

            astDial->fullimgsize = cv::Size(651, 419);
            astDial->size = cv::Size(211, 419);
            astDial->bias = cv::Point(0, 0);
            //astDial->path = path+"novolizer3_canny_cleanedCropBB.jpg";

            astInhalCntr->fullimgsize = cv::Size(651, 419);
            astInhalCntr->size = cv::Size(651, 419);
            astInhalCntr->bias = cv::Point(0, 0);
            //astInhalCntr->path = path+"novolizer3_canny_cleanedCropCoarse.jpg";

            astDispInhal->fullimgsize = cv::Size(651, 419);
            astDispInhal->size = cv::Size(651, 419);
            astDispInhal->bias = cv::Point(0, 0);
            //astDispInhal->path = path+"novolizer3_canny_cleanedCropCoarse.jpg";
            break;

        case 4: //spiromax
            //path = path+"spiromaxTpl/";
            astDial->fullimgsize = cv::Size(641, 215);
            astDial->size = cv::Size(111, 215);
            astDial->bias = cv::Point(277, 0);
            //astDial->path = path+"spiromaxTemplateCrop_elipseBB11.jpg";

            astInhalCntr->fullimgsize = cv::Size(641, 215);
            astInhalCntr->size = cv::Size(641, 215);
            astInhalCntr->bias = cv::Point(0, 0);
            //astInhalCntr->path = path+"spiromaxTemplateCropRot.jpg";

            astDispInhal->fullimgsize = cv::Size(641, 215);
            astDispInhal->size = cv::Size(641, 215);
            astDispInhal->bias = cv::Point(0, 0);
            //astDispInhal->path = path+"spiromaxTemplateCropRot.jpg";
            break;

        case 5: //ellipta
            //path = path+"elliptaTpl/";
            astDial->fullimgsize = cv::Size(626, 492);
            astDial->size = cv::Size(279, 492);
            astDial->bias = cv::Point(347, 0);
            //astDial->path = path+"ellipta9_canny_cleanedRotCrop_DialBB13.jpg";

            astInhalCntr->fullimgsize = cv::Size(626, 492);
            astInhalCntr->size = cv::Size(626, 492);
            astInhalCntr->bias = cv::Point(0, 0);
            //astInhalCntr->path = path+"ellipta9_canny_cleanedRotCropCoarse.jpg";

            astDispInhal->fullimgsize = cv::Size(626, 492);
            astDispInhal->size = cv::Size(626, 492);
            astDispInhal->bias = cv::Point(0, 0);
            //astDispInhal->path = path+"ellipta9_canny_cleanedRotCrop.jpg";
            break;

        case 6: //easyhaler
            astDial->fullimgsize = cv::Size(800, 604);
            astDial->size = cv::Size(169, 604);
            astDial->bias = cv::Point(476, 0);

            astInhalCntr->fullimgsize = cv::Size(800, 604);
            astInhalCntr->size = cv::Size(800, 604);
            astInhalCntr->bias = cv::Point(0, 0);

            astDispInhal->fullimgsize = cv::Size(800, 604);
            astDispInhal->size = cv::Size(800, 604);
            astDispInhal->bias = cv::Point(0, 0);

            break;

        case 7: //khaler
            astDial->fullimgsize = cv::Size(707, 260);
            astDial->size = cv::Size(128, 260);
            astDial->bias = cv::Point(100, 0);

            astInhalCntr->fullimgsize = cv::Size(707, 260);
            astInhalCntr->size = cv::Size(707, 260);
            astInhalCntr->bias = cv::Point(0, 0);

            astDispInhal->fullimgsize = cv::Size(707, 260);
            astDispInhal->size = cv::Size(707, 260);
            astDispInhal->bias = cv::Point(0, 0);

            break;

        case 8: //mdi
            astDial->fullimgsize = cv::Size(800, 313);
            astDial->size = cv::Size(127, 313);
            astDial->bias = cv::Point(571, 0);

            astInhalCntr->fullimgsize = cv::Size(800, 313);
            astInhalCntr->size = cv::Size(800, 313);
            astInhalCntr->bias = cv::Point(0, 0);

            astDispInhal->fullimgsize = cv::Size(800, 313);
            astDispInhal->size = cv::Size(800, 313);
            astDispInhal->bias = cv::Point(0, 0);

            break;

        case 9: //nexthaler
            astDial->fullimgsize = cv::Size(800, 533);
            astDial->size = cv::Size(92, 533);
            astDial->bias = cv::Point(385, 0);

            astInhalCntr->fullimgsize = cv::Size(800, 533);
            astInhalCntr->size = cv::Size(800, 533);
            astInhalCntr->bias = cv::Point(0, 0);

            astDispInhal->fullimgsize = cv::Size(800, 533);
            astDispInhal->size = cv::Size(800, 533);
            astDispInhal->bias = cv::Point(0, 0);

            break;

        case 10: //seretide
            astDial->fullimgsize = cv::Size(800, 331);
            astDial->size = cv::Size(114, 331);
            astDial->bias = cv::Point(591, 0);

            astInhalCntr->fullimgsize = cv::Size(800, 331);
            astInhalCntr->size = cv::Size(800, 331);
            astInhalCntr->bias = cv::Point(0, 0);

            astDispInhal->fullimgsize = cv::Size(800, 331);
            astDispInhal->size = cv::Size(800, 331);
            astDispInhal->bias = cv::Point(0, 0);

            break;

        case 11: //twisthaler
            astDial->fullimgsize = cv::Size(800, 317);
            astDial->size = cv::Size(131, 317);
            astDial->bias = cv::Point(598, 0);

            astInhalCntr->fullimgsize = cv::Size(800, 317);
            astInhalCntr->size = cv::Size(800, 317);
            astInhalCntr->bias = cv::Point(0, 0);

            astDispInhal->fullimgsize = cv::Size(800, 317);
            astDispInhal->size = cv::Size(800, 317);
            astDispInhal->bias = cv::Point(0, 0);

            break;

        default:
            fprintf(stderr, "inhalerStr not recognized.\n");
            return -1;
    }
    return 0;
}




/* Backup code


 astInhalCntr = imgAssetInfo {
    cv::Size(735, 290),         //fullimgsize
    cv::Size(530, 290),         //.size
    cv::Point(0, 0),         //.bias
    path+"template/templatePlusBDEdges2CropNoMouthBB.jpg", //path
};
*astDispInhal = imgAssetInfo{
    cv::Size(735, 290),         //fullimgsize
    cv::Size(735, 290),         //.size
    cv::Point(0, 0),         //.bias
    path+"template/templatePlusBDEdges2Crop.jpg", //path
};
*/


#endif
