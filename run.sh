
arg1="$1"
arg2="$2"
if [ ! -n "$arg1" ] || [ ! -n "$arg2" ]
then
arg1="./resources/BlackFriday.txt"
arg2="./resources/test_BlackFriday.txt"
fi

javac -d ./bin ./src/com/bloomfilter/BloomFilter.java
javac -d ./bin ./src/com/bloomfilter/BigDataStore.java -cp "./bin:./ext/*"
javac -d ./bin ./test/com/bloomfilter/*.java -cp "./bin:./ext/*"
#java  -cp "./bin:./ext/*"  com.bloomfilter.BloomFilterTest
java -cp "./bin:./ext/*" com.bloomfilter.BigDataStoreTest $arg1 $arg2
