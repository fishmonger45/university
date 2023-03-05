#include <iostream>
#include "e.cpp"
#include "f.cpp"
#include "h.cpp"

void g()
{
    std::cout << e() << " within g.cpp" << std::endl;
    std::cout << f() << " within g.cpp" << std::endl;
    std::cout << h() << " within g.cpp" << std::endl;
    std::cout << "g from cpp" << std::endl;
}

int main(void)
{
    g();
    return 0;
}