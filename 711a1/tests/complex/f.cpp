#include <iostream>
#include "c.cpp"
#include "d.cpp"

std::string f()
{
    std::cout << c() << " within f.cpp" << std::endl;
    std::cout << d() << " within f.cpp" << std::endl;
    std::cout << "f from cpp" << std::endl;
    return "f";
}