## File Structure

---

-SearchEngine.html
-SearchEngine.jsp
-SearchEngine.css
-Makefile
-WEB-INF
| -lib
| | -rocksdbini-6.0.0-linux64.jar
| | -jsoup-1.8.1.jar
| | -servlet-api.jar
| -classes
| | -Makefile
| | -lib
| | | -rocksdbini-6.0.0-linux64.jar
| | | -jsoup-1.8.1.jar
| | | -servlet-api.jar
| | -resource
| | | -database
| | | | -DataTranform.java
| | | | -PageInfo.java
| | | | -ReadData.java
| | | | -Rockdb.java
| | | | -WriteData.java
| | | -forward
| | | | -CreateForward.java
| | | | -DataPair.java
| | | | -ForwardData.java
| | | | -WordData.java
| | | -invert
| | | | -CreateInverted.java
| | | | -InvertedData.java
| | | | -SimplePageInfo.java
| | | -processing
| | | | -Porter.java
| | | | -StopStem.java
| | | | -stopwords.txt
| | | -retrieval
| | | | -DfWord.java
| | | | -MaxTf.java
| | | | -QueryHandle.java
| | | | -QueryResScore.java
| | | | -Retrieval.java
| | | -server
| | | | -SearchEngineServer.java
| | | -spider
| | | | -Crawler.java
| | | | -HelperData.java
| | | -test
| | | | -TestForwardData.java
| | | | -TestInvertData.java
| | | | -TestJsp.java
| | | | -TestQueryHandle.java
| | | | -TestRead.java
| | | | -TestReadGlobal.java
| | | | -TestReadInvert.java
| | | | -TestRetrieval.java
| | | | -TestRocksdb.java

# Installation

1. Put below files in /apache-tomcat-10.0.20/webapps/ROOT/
   -SearchEngine.html
   -SearchEngine.jsp
   -SearchEngine.css
2. Put below folder in /apache-tomcat-10.0.20/webapps/ROOT/WEB-INF/classes
   resource/
   lib/
3. Put lib/ folder in /apache-tomcat-10.0.20/webapps/ROOT/

4. In directory /apache-tomcat-10.0.20/webapps/ROOT/WEB-INF/classes
   Run command:

```
make all
```

    to crawl webpages
    Run command:

```
make server
```

    to start the Search Engine

5. visit VM_location:8080/SearchEngine.html to use the search engine
