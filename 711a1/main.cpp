#include <iostream>

#include "submake.hpp"
#include "tree.hpp"
#include "target.hpp"
#include "cli.hpp"

using namespace std;

int main(int argc, char *argv[])
{


    cli args = cli::get_arguments(argc, argv);
    tree *t = submake::parse(args.filename, args.make_target);
    // tree::print(t);
    submake::build(t);
    return 0;
}