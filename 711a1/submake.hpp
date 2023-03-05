
#include <iostream>
#include <vector>
#include <pthread.h>
#include <queue>
#include <fstream>
#include <sstream>
#include <algorithm>
#include <iterator>
#include <sys/types.h>
#include <sys/stat.h>

// windows 
#ifndef WIN32
#include <unistd.h>
#endif

#ifdef WIN32
#define stat _stat
#endif

#include "tree.hpp"
#include "target.hpp"

#ifndef __SUBMAKE_HPP__
#define __SUBMAKE_HPP__

class submake
{
public:
    static void *build(void *tree_vp);
    static bool requires_build(tree* t);
    static string strip(string s);
    static tree *parse(string filename, string make_target);
};

#endif