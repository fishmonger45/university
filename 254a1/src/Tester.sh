for jar in `ls [bg]*.jar `
do
    printf "_________ $jar _________\n\n"
    javac -d "./build" -cp "./hamcrest-core-1.3.jar:./junit-4.13.jar:./$jar" "./text/TestFlushLeft.java"
    java -cp "./hamcrest-core-1.3.jar:./junit-4.13.jar:./build:./$jar" org.junit.runner.JUnitCore text.TestFlushLeft
    printf "\n"
done
