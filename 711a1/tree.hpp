#include <vector>
#include <iostream>
#include <queue>
#include "target.hpp"

using namespace std;

#ifndef __TREE_HPP__
#define __TREE_HPP__

// directed dependency tree of targets
class tree
{
public:
    target *node;
    vector<tree *> children;
    tree(target *n, vector<tree *> c);
    tree *lookup(string o);
    static void print(tree *t);
    friend ostream &operator<<(ostream &os, const tree &t);
};

#endif