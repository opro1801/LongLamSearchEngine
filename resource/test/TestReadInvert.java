package resource.test;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import resource.database.ReadData;
import resource.forward.ForwardData;
import resource.invert.*;
public class TestReadInvert {
    public static void main(String[] args){
        String dbPath = "resource/db";
        RocksDB.loadLibrary();
		String prefix = "Word@@";
        try {
            // The Options class contains a set of configurable DB options
            // that determines the behaviour of the database.
            Options options = new Options();
            options.setCreateIfMissing(true);
            // Creat and open the database
            RocksDB db;
            db = RocksDB.open(options, dbPath);
            PrintStream originalOut = System.out;
			PrintStream fileOut = new PrintStream("./new_result.txt");
			System.setOut(fileOut);
            ReadData<InvertedData> reads = new ReadData<InvertedData>();
            String key = prefix+"studi";
            InvertedData temp = reads.readData(key,db);
            System.out.println("Debug");
            System.out.println("Df is"+temp.df_word());
            temp.print();
			System.setOut(originalOut);
            
        } catch (RocksDBException e) {
                System.err.println(e.toString());
        } catch (FileNotFoundException e){

        }
    }
}