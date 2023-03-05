main.out : main.o submake.o tree.o target.o cli.o 
	g++ -pthread main.o target.o tree.o cli.o submake.o -o main.out
main.o : main.cpp target.hpp tree.hpp submake.hpp cli.hpp 
	g++ -c main.cpp -o main.o
submake.o : submake.cpp submake.hpp tree.hpp target.hpp acout.cpp
	g++ -c submake.cpp -o submake.o
tree.o : tree.cpp tree.hpp target.hpp acout.cpp
	g++ -c tree.cpp -o tree.o
target.o : target.cpp target.hpp
	g++ -c target.cpp -o target.o
cli.o : cli.cpp cli.hpp acout.cpp
	g++ -c cli.cpp -o cli.o
acout.o : acout.cpp 
	g++ -c acout.cpp -o acout.o
test :
	cd ./tests/simple && make test
	cd ./tests/complex && make test
	cd ./tests/make0 && make test
	cd ./tests/make1 && make test
	cd ./tests/make2 && make test
	make superclean
clean :
	rm *.o main.out || exit 0
superclean : clean
	rm tests/simple/*.o tests/simple/*.out || exit 0
	rm tests/complex/*.o tests/complex/*.out || exit 0