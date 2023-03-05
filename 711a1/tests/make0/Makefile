a.out : f.o g.o a.cpp
	g++ -o a.out a.cpp
	echo "a done"
f.o : f.cpp
	g++ -c f.cpp -o f.o
	echo "f done"
g.o : g.cpp
	g++ -c g.cpp -o g.o
	echo "g done"
clean :
	rm *.o *.out || exit 0
test : clean
	cd ../../ && make clean && make main.out 	
	cp ../../main.out ./main.out
	./main.out -f ./Makefile -t a.out			# alec test
	make clean