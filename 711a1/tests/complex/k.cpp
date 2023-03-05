#include <iostream>
#include "i.cpp"
#include "j.cpp"

std::string k()
{
    std::cout << i() << " within k.cpp" << std::endl;
    std::cout << j() << " within k.cpp" << std::endl;
    std::cout << "k from k.cpp" << std::endl;
    return "k";
}

int main() {
    k();
    return 0;
}