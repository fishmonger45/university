# submake
submake is a a simplified make tool which parses and builds targets in parallel from a syntactic subset of a Makefile (refered to as the submake syntax).
submake was created for the COMPSCI711 'parallel programming using threads' assignment.
### don't want to read this?
- just run `make test` from the project root
- not tested on windows as I don't have a license, tested on ubuntu 20.04
### example run
[![asciicast](https://asciinema.org/a/E3d4TYpnD4XYIhMG3VaY9ivkh.svg)](https://asciinema.org/a/E3d4TYpnD4XYIhMG3VaY9ivkh)
## building
Run the default make target:
```bash
make 
```
## running
By default the application will parse its own makefile in the project root, although this behavior can be overridden with flags. First build the project, then from within the project root:
```
usage: ./main.out [options]
options:
        -h,--help       show this help message
        -f,--file       specify target makefile, defaults to root makefile in project
        -t,--target     specify makefile target to run, defaults to first target in makefile
example:
        ./main -f ./Makefile -t all
```
To simply run the program using the project root makefile, just run the built binary:
```bash
./main.out
```
## tests
In order to run all tests, enter the project root and run:
```
make test
```
If you would like to run an individual test you can enter one of the following directories: 
- `tests/simple/`
- `tests/complex/`
and run `make test` from that directory
If you would like to build a custom make target within the test you may run the following:
```
../../main.out -f ./Makefile -t <test_make_target>
```
## tests/simple
- A simple dependency with some parallelism of tasks `a` and `b`, where in order for `c` to compile, both `a` and `b` must be compiled
- Sanity checks the system, acts as an "it works" test
```
a ====>|
       =====> c (final target)
b ====>|
```
- test 1 checks that `c` is built in parllel
- test 2 modifies `a` and then rebuilds `c`, verifying that `c` is rebuilt when `a` is modified
- test 3 modifies `a` and `b` then rebuilds `c`, verifying that `c` is rebuilt multiple targets have changed
- test 4 tests that the program can run targets that does not have an output filename as a target
## tests/complex
- Tests some complex dependencies, such as multiple levels of depenedencies
- Multiple different independent dependency graphs working 
```
a ====>|
       =====> e ====>|
b ====>|             |
                     =====> g (final target)
c ====>|             |      |
       =====> f ====>|      |
d ====>|                    |
                            |
h =========================>|


i ====>|
       =====> k (final target)
j ====>|
```
- test 1 tests that the whole complex tree can be built
- test 2 tests that the tree final target `g` is rebuilt when a file distance one away from the root is modified (`e`)
- test 3 tests that the tree final target `g` is rebuilt when a file distance two away from the root is modified (`a`)
- test 4 builds the independent tree within the makefile (`k`)
- test 5 tests the rebuild of the independent tree (`k`) when `i` is modified
- test 6 tests the build of a make target output that does not have an output filename as a make target
## report
### implementation decisions
The grammar of the submake syntax is a simplified subset of the makefile syntax, it holds the following grammar:
```
string   ::= ^[A-Za-z0-9_.]+$
out      ::= <string>
in       ::= <string>
header   ::= <out> : ...<in> 
command  ::= <string>
makefile ::= ...<<header> \n \t ...<command>>
```
- where `\n` and `\t` are literal newline and hard tab characters respectively
where in informal terms:
```
...<a> = zero or more of whitespace seperated symbol <a>
    eg: ...<string> = "foo" "bar "baz"
```
- ie a string is just any normal string that you would find in other grammars
- symbols can be separated by one or more whitespaces 
- make target headers are identified by the characters " : " existing in the line
- make target bodies are identified by the character "\t" existing in the line
