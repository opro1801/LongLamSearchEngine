package resource.test;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import resource.database.ReadData;
public class TestReadGlobal {
    public static void main(String arg[]){
        String dbPath = "resource/db";
        RocksDB.loadLibrary();
		String prefixWordList = "Word@List@";
		String prefixTitleTf = "Title@maxTf";
		String prefixContentTf = "Content@maxTf";
		String prefixDf = "Word@Df";
        try {
            // The Options class contains a set of configurable DB options
            // that determines the behaviour of the database.
            Options options = new Options();
            options.setCreateIfMissing(true);
            // Creat and open the database
            RocksDB db;
            db = RocksDB.open(options, dbPath);
            PrintStream originalOut = System.out;
			PrintStream fileOut = new PrintStream("./global1_result.txt");
			System.setOut(fileOut);
            //ReadData< ArrayList<String> > reads = new ReadData< ArrayList<String> >();
            ReadData< ArrayList<Integer> > reads = new ReadData< ArrayList<Integer> >();
            ReadData< HashMap<String,Integer> > reads2 = new ReadData< HashMap<String,Integer>>();
            String key1 = prefixTitleTf;
            String key2 = prefixContentTf;
            String key3 = prefixDf;
            ArrayList<Integer> temp1 = reads.readData(key1,db);
            System.out.println("Debug1");
            System.out.println(temp1);
            ArrayList<Integer> temp2 = reads.readData(key2,db);
            System.out.println("Debug2");
            System.out.println(temp2);
            HashMap<String,Integer> temp3 = reads2.readData(key3,db);
            System.out.println("Debug3");
            System.out.println(temp3);
			System.setOut(originalOut);
            
        } catch (RocksDBException e) {
                System.err.println(e.toString());
        } catch (FileNotFoundException e){

        }
    }
}
