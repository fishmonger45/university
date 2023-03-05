a.out : b.o c.o d.o e.o f.o g.o a.cpp
	g++ -o a.out a.cpp
b.o : e.o f.o g.o
	g++ -o b.o -c b.cpp
c.o : e.o f.o g.o
	g++ -o c.o -c c.cpp
d.o : e.o f.o g.o
	g++ -o d.o -c d.cpp
e.o :
	g++ -o e.o -c e.cpp
f.o :
	g++ -o f.o -c f.cpp
g.o :
	g++ -o g.o -c g.cpp
clean :
	rm *.o *.out || exit 0
test : clean
	cd ../../ && make clean && make main.out 	
	cp ../../main.out ./main.out
	./main.out -f ./Makefile -t a.out			# alec test
	make clean