#ifndef __GDIRF_CPP__
#define __GDIRF_CPP__

#include "gdirfiles.h"

void GetDirListing( DirListing_t& result, const std::string& dirpath )
{
    DIR* dir = opendir( dirpath.c_str() );
    if (dir){
        struct dirent* entry;
        while ((entry = readdir( dir ))){
            struct stat entryinfo;
            std::string entryname = entry->d_name;
            std::string entrypath = dirpath + "/" + entryname;
            if (!stat( entrypath.c_str(), &entryinfo )){
                if (S_ISDIR( entryinfo.st_mode )){
                  if      (entryname == "..");
                  else if (entryname == "." ) result.push_back( dirpath + "/" );
                  else                        GetDirListing( result, entrypath );
                }
                else{
                  result.push_back( entrypath );
                }
            }
        }
        closedir( dir );
    }
}

//-------------------------------------------
/// Exports only cur dir files
void get1stLvlDir(std::string path, DirListing_t& dirfull, DirListing_t& dirshort){

    GetDirListing( dirfull, path );
    for(int i=0; i<dirfull.size(); i++)
        dirshort.push_back(dirfull[i]);

    //Removing path untill directory
    for(unsigned n = 0; n < dirshort.size(); n++){
        dirshort[n] = dirshort[n].substr(path.size()+1, dirshort[n].size());
    }
    //Invalidate Folders
    for(unsigned n = 0; n < dirfull.size(); n++){
        size_t found = dirshort[n].find("/");
        if (found!=std::string::npos) //is folder - remove
            dirshort[n] = "";
    }
    //Remove Invalid paths
    for(int n = dirshort.size()-1; n >=0 ; n--){
        if ( dirshort[n] == std::string("") ){
            dirshort.erase(dirshort.begin()+n); //full
            dirfull.erase(dirfull.begin()+n);   //short
        }
    }

}



#endif
