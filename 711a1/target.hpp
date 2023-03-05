#include <iostream>
#include <vector>

using namespace std;

#ifndef __TARGET_HPP__
#define __TARGET_HPP__

// target is a makefile target with a set of commands to execute
class target
{
public:
    string output;
    vector<string> commands;
    vector<string> input;
    target();
    target(string o, vector<string> c, vector<string> i);
    friend ostream &operator<<(ostream &os, const target &t);
};

#endif