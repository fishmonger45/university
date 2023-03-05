g.out : e.o f.o h.o g.cpp
	g++ -o g.out g.cpp
	echo "g done"
a.o : a.cpp
	g++ -c a.cpp -o a.o
	echo "a done"
b.o : b.cpp
	g++ -c b.cpp -o b.o
	echo "b done"
c.o : c.cpp
	g++ -c c.cpp -o c.o
	echo "c done"
d.o : d.cpp
	g++ -c d.cpp -o d.o
	echo "d done"
e.o : a.o b.o e.cpp
	g++ -c e.cpp -o e.o
	echo "e done"
f.o : c.o d.o f.cpp
	g++ -c f.cpp -o f.o
	echo "e done"
h.o : h.cpp
	g++ -c h.cpp -o h.o
	echo "h done"
i.o : i.cpp
	g++ -c i.cpp -o i.o
	echo "i done"
j.o : j.cpp
	g++ -c j.cpp -o j.o
	echo "j done"
k.out : i.o j.o k.cpp
	g++ k.cpp -o k.out
	echo "k done"
clean :
	rm *.o *.out || exit 0
test : clean
	cd ../../ && make clean && make main.out 	
	cp ../../main.out ./main.out
	make g.out
	./main.out -f ./Makefile -t g.out			# test 1 (can we build this complex tree)
	touch -c e.cpp 								# test 2 (rebuild node distance 1 away)
	./main.out -f ./Makefile -t g.out
	touch -c a.cpp 								# test 3 (rebuild node distance 2 away)
	./main.out -f ./Makefile -t g.out
	./main.out -f ./Makefile -t k.out			# test 4 (build independent tree)
	make k.out
	touch -c i.cpp 								# test 5 (rebuild node for independent tree distance 1 away)
	./main.out -f ./Makefile -t k.out
	./main.out -f ./Makefile -t clean			# test 6 (non file target)