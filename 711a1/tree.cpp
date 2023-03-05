#include "tree.hpp"
#include "acout.cpp"

tree::tree(target *n, vector<tree *> c) : node(n),
                                          children(c)
{
}

// search a tree for a subtree with a matching output
tree *tree::lookup(string o)
{
    queue<tree *> q;
    q.push(this);
    while (!q.empty())
    {
        tree *t = q.front();
        q.pop();
        for (tree *c : t->children)
        {
            if (c->node->output == o)
            {
                return t;
            }
            else
            {
                q.push(t);
            }
        }
    }
    return NULL;
}

void tree::print(tree *t)
{
    acout() << "node:" << endl
            << "\t" << *(t->node) << endl
            << "tree:" << endl;
    for (auto c : t->children)
    {
        print(c);
    }
}

ostream &operator<<(ostream &os, const tree &t)
{
    os << "node:" << endl;
    os << "\t" << t.node << endl;
    os << "tree:" << endl;
    for (auto c : t.children)
    {
        os << "\t" << c << endl;
    }
    return os;
}