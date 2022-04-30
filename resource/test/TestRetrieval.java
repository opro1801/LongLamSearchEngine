package resource.test;

import java.io.IOException;
import resource.retrieval.Retrieval;
import org.rocksdb.RocksDBException;

public class testRetrieval {
    public static void main(String args[]) {
        try {
            //Retrieval.doQuery("entertain in \"Viet Nam\"");
            //Retrieval.doQuery("computer science");
            Retrieval.doQuery("computer science");
            // Retrieval.doQuery("food \"hong kong\"");
        } catch (RocksDBException e) {

        } catch (IOException e) {

        }
    }
}
