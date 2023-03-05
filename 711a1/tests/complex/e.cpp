#include <iostream>
#include "a.cpp"
#include "b.cpp"

std::string e()
{
    std::cout << a() << " within e.cpp" << std::endl;
    std::cout << b() << " within e.cpp" << std::endl;
    std::cout << "e from cpp" << std::endl;
    return "e";
}