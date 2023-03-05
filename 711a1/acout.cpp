#include <iostream>
#include <mutex>
#include <thread>
#include <vector>
#include <chrono>

static std::mutex mtx_cout;

// mutex locked thread safe cout
struct acout
{
    std::unique_lock<std::mutex> lk;
    acout()
        : lk(std::unique_lock<std::mutex>(mtx_cout))
    {
    }

    template <typename T>
    acout &operator<<(const T &_t)
    {
        std::cout << _t;
        return *this;
    }

    acout &operator<<(std::ostream &(*fp)(std::ostream &))
    {
        std::cout << fp;
        return *this;
    }
};