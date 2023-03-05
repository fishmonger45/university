c.out : a.o b.o c.cpp
	g++ -o c.out c.cpp
	echo "c done"
a.o : a.cpp
	g++ -c a.cpp -o a.o
	echo "a done"
b.o : b.cpp
	g++ -c b.cpp -o b.o
	echo "b done"
clean :
	rm *.o *.out || exit 0
test : clean
	cd ../../ && make clean && make main.out 	
	cp ../../main.out ./main.out
	./main.out -f ./Makefile -t c.out			# test 1 (can we build this complex tree)
	touch -c a.cpp 								# test 2 (rebuild node distance 1 away)
	./main.out -f ./Makefile -t c.out
	touch -c a.cpp 								# test 3 (rebuild node when two different files chnaged)
	touch -c b.cpp 								# test 3 (rebuild node when two different files changed)
	./main.out -f ./Makefile -t c.out
	rm a.o
	./main.out -f ./Makefile -t a.o				# test 4 make non .out target
	make clean