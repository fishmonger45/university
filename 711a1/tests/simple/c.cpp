#include <iostream>
#include "a.cpp"
#include "b.cpp"

void c()
{
    std::cout << a() << " within c.cpp" << std::endl;
    std::cout << b() << " within c.cpp" << std::endl;
    std::cout << "c from cpp" << std::endl;
}

int main(void)
{
    c();
    return 0;
}