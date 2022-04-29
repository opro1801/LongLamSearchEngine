all:
	javac resource/*/*.java resource/database/Rockdb.java -cp lib/jsoup-1.14.3.jar:lib/rocksdbjni-6.19.0-linux64.jar:lib/servlet-api.jar
	java -cp lib/jsoup-1.14.3.jar:.:lib/rocksdbjni-6.19.0-linux64.jar:.:lib/servlet-api.jar:. resource.database.Rockdb
test:
	javac -cp lib/rocksdbjni-6.19.0-linux64.jar TestRocksdb.java
	java -cp lib/rocksdbjni-6.19.0-linux64.jar:. TestRocksdb
server:
	javac resource/*/*.java resource/database/Rockdb.java -cp lib/jsoup-1.14.3.jar:lib/rocksdbjni-6.19.0-linux64.jar:lib/servlet-api.jar
	../../../../bin/./shutdown.sh
	../../../../bin/./startup.sh
play:
	javac resource/spider/*.java resource/forward/*.java resource/database/*.java resource/processing/*.java resource/test/TestForwardData.java -cp lib/jsoup-1.14.3.jar:lib/rocksdbjni-6.19.0-linux64.jar 
hello:
	javac -cp lib/jsoup-1.14.3.jar:lib/rocksdbjni-6.19.0-linux64.jar Hello.java
	java -cp lib/jsoup-1.14.3.jar:.:lib/rocksdbjni-6.19.0-linux64.jar:. Hello
testF:
	javac resource/*/*.java -cp lib/jsoup-1.14.3.jar:lib/rocksdbjni-6.19.0-linux64.jar:lib/servlet-api.jar
	java -cp lib/jsoup-1.14.3.jar:.:lib/rocksdbjni-6.19.0-linux64.jar:. resource.test.TestRead
testI:
	javac resource/*/*.java -cp lib/jsoup-1.14.3.jar:lib/rocksdbjni-6.19.0-linux64.jar:lib/servlet-api.jar
	java -cp lib/jsoup-1.14.3.jar:.:lib/rocksdbjni-6.19.0-linux64.jar:. resource.test.TestReadInvert
testG:
	javac resource/*/*.java -cp lib/jsoup-1.14.3.jar:lib/rocksdbjni-6.19.0-linux64.jar:lib/servlet-api.jar
	java -cp lib/jsoup-1.14.3.jar:.:lib/rocksdbjni-6.19.0-linux64.jar:. resource.test.TestReadGlobal
testR:
	javac resource/*/*.java -cp lib/jsoup-1.14.3.jar:lib/rocksdbjni-6.19.0-linux64.jar:lib/servlet-api.jar
	java -cp lib/jsoup-1.14.3.jar:.:lib/rocksdbjni-6.19.0-linux64.jar:. resource.test.testRetrieval
compile:
	javac resource/*/*.java -cp lib/jsoup-1.14.3.jar:lib/rocksdbjni-6.19.0-linux64.jar:lib/servlet-api.jar
cleanClass:
	rm -rf resource/*/*.class
clean:
	rm -rf resource/db
	rm resource/*/*.class
	rm spider_result.txt