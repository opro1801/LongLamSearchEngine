## File Structure

-SearchEngine.html <br/>
-SearchEngine.jsp <br/>
-SearchEngine.css <br/>
-WEB-INF <br/>
| -lib <br/>
| | -rocksdbini-6.0.0-linux64.jar <br/>
| | -jsoup-1.8.1.jar <br/>
| | -servlet-api.jar <br/>
| -classes <br/>
| | -Makefile <br/>
| | -lib <br/>
| | | -rocksdbini-6.0.0-linux64.jar <br/>
| | | -jsoup-1.8.1.jar <br/>
| | | -servlet-api.jar <br/>
| | -resource <br/>
| | | -database <br/>
| | | | -DataTranform.java <br/>
| | | | -PageInfo.java <br/>
| | | | -ReadData.java <br/>
| | | | -Rockdb.java <br/>
| | | | -WriteData.java <br/>
| | | -forward <br/>
| | | | -CreateForward.java <br/>
| | | | -DataPair.java <br/>
| | | | -ForwardData.java <br/>
| | | | -WordData.java <br/>
| | | -invert <br/>
| | | | -CreateInverted.java <br/>
| | | | -InvertedData.java <br/>
| | | | -SimplePageInfo.java <br/>
| | | -processing <br/>
| | | | -Porter.java <br/>
| | | | -StopStem.java <br/>
| | | | -stopwords.txt <br/>
| | | -retrieval <br/>
| | | | -DfWord.java <br/>
| | | | -MaxTf.java <br/>
| | | | -QueryHandle.java <br/>
| | | | -QueryResScore.java <br/>
| | | | -Retrieval.java <br/>
| | | -server <br/>
| | | | -SearchEngineServer.java <br/>
| | | -spider <br/>
| | | | -Crawler.java <br/>
| | | | -HelperData.java <br/>
| | | -test <br/>
| | | | -TestForwardData.java <br/>
| | | | -TestInvertData.java <br/>
| | | | -TestJsp.java <br/>
| | | | -TestQueryHandle.java <br/>
| | | | -TestRead.java <br/>
| | | | -TestReadGlobal.java <br/>
| | | | -TestReadInvert.java <br/>
| | | | -TestRetrieval.java <br/>
| | | | -TestRocksdb.java <br/>

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
