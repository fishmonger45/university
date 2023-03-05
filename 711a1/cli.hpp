
#include <iostream>
#include <vector>

using namespace std;

#ifndef __CLI_HPP__
#define __CLI_HPP__

// default makefile file and makefile target
const string DEFAULT_MAKE_FILE_LOCATION = "./Makefile";
const string DEFAULT_MAKE_TARGET = "main.out";

// command line interface for parsing arguments and usage
class cli
{
public:
    // file location of makefile
    string filename;
    // target within makefile to execute
    string make_target;
    cli();
    static cli get_arguments(int argc, char *argv[]);
    static void usage(string name);
};

#endif