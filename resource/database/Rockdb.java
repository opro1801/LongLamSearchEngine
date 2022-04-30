package resource.database;
import org.rocksdb.RocksDB;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;  
import resource.spider.Crawler;

public class Rockdb{
    public static void main(String[] args){
        String dbPath = "/root/apache-tomcat-10.0.20/webapps/ROOT/WEB-INF/classes/resource/db";
        RocksDB.loadLibrary();
        try {
            // The Options class contains a set of configurable DB options
            // that determines the behaviour of the database.
            Options options = new Options();
            options.setCreateIfMissing(true);

            // Creat and open the database
            RocksDB db;
            db = RocksDB.open(options, dbPath);
            String url = "http://cse.hkust.edu.hk";
            //String url = "https://seng.hkust.edu.hk/";
			Crawler crawler = new Crawler(url);
			crawler.crawlLoop(db);
            
        } catch (RocksDBException e) {
                System.err.println(e.toString());
        } 
    }
}




