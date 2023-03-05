a.out : b.o c.o d.o e.o f.o g.o a.cpp
	g++ -o a.out a.cpp
b.o : c.o
	g++ -c b.cpp
c.o : d.o
	g++ -c c.cpp
d.o : e.o
	g++ -c d.cpp
e.o : f.o
	g++ -c e.cpp
f.o : g.o
	g++ -c f.cpp
g.o :
	g++ -c g.cpp
clean :
	rm *.o *.out *.gch || exit 0
test : clean
	cd ../../ && make clean && make main.out 	
	cp ../../main.out ./main.out
	./main.out -f ./Makefile -t a.out			# alec make2 test
	rm main.out
	make clean