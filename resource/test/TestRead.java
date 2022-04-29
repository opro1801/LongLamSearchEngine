package resource.test;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import resource.database.ReadData;
import resource.forward.ForwardData;

public class TestRead {
    public static void main(String[] args){
        String dbPath = "resource/db";
        RocksDB.loadLibrary();
		String prefixForward = "Page@URL";
		String prefixInvert = "Word@@";
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
			PrintStream fileOut = new PrintStream("./spider_result.txt");
			System.setOut(fileOut);
            ReadData<ForwardData> reads = new ReadData<ForwardData>();
			for(int i=0;i<=2;i++){
                String key = prefixForward+String.valueOf(i);
                ForwardData temp = reads.readData(key, db);
                if(temp!=null){
                    temp.print();
                }
			}
			System.setOut(originalOut);
            
        } catch (RocksDBException e) {
                System.err.println(e.toString());
        } catch (FileNotFoundException e){

        }
    }
}