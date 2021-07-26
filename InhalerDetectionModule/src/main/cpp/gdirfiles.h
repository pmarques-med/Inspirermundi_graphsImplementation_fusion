#ifndef __GDIRF_H__
#define __GDIRF_H__

#include <iostream>
#include <string>
#include <vector>

#include <sys/types.h>
#include <dirent.h>
#include <sys/stat.h>
#include <unistd.h>


typedef std::vector <std::string> DirListing_t;

void GetDirListing( DirListing_t& result, const std::string& dirpath );
void get1stLvlDir(std::string path, DirListing_t& dirfull, DirListing_t& dirshort);

#endif