#include "cli.hpp"
#include "acout.cpp"

cli::cli() : filename(DEFAULT_MAKE_FILE_LOCATION),
             make_target(DEFAULT_MAKE_TARGET)
{
}

// print help information to stdout
void cli::usage(string name)
{
    acout() << "usage: " << name << " [options]\n"
         << "options:\n"
         << "\t-h,--help\tshow this help message\n"
         << "\t-f,--file\tspecify target makefile, defaults to root makefile in project\n"
         << "\t-t,--target\tspecify makefile target to run, defaults to first target in makefile\n"
         << "example:\n"
         << "\t./main -f ./tests/simple/Makefile -t all\n"
         << endl;
}

// fetch program arguments from stdin
cli cli::get_arguments(int argc, char *argv[])
{
    cli cli_args = cli();
    for (int i = 1; i < argc; ++i)
    {
        string arg = argv[i];
        if (arg.compare("-h") == 0 || arg.compare("--help") == 0) // help
        {
            cli::usage(argv[0]);
            exit(0);
        }
        else if (arg.compare("-f") == 0 || arg.compare("--file") == 0) // file
        {
            cli_args.filename = argv[i + 1];
            i++;
        }
        else if (arg.compare("-t") == 0 || arg.compare("--target") == 0) // make target
        {
            cli_args.make_target = argv[i + 1];
            i++;
        }
        else // unknown argument
        {
            cerr << "unknown argument \'" << argv[i] << "\'\n";
            cli::usage(argv[0]);
            exit(1);
        }
    }

    return cli_args;
}