#include "target.hpp"

target::target() : output(""),
                   commands(vector<string>{}),
                   input(vector<string>{})
{
}

target::target(string o, vector<string> c, vector<string> i) : output(o),
                                                               commands(c),
                                                               input(i)
{
}

ostream &operator<<(ostream &os, const target &t)
{
    os << "output:" << endl;
    os << "\t" << t.output << endl;
    os << "commands:" << endl;
    for (auto c : t.commands)
    {
        os << "\t" << c << endl;
    }
    os << "input:" << endl;
    for (auto i : t.input)
    {
        os << "\t" << i << endl;
    }
    return os;
}