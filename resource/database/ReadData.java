package resource.database;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;  
import java.io.Serializable;

public class ReadData<T extends Serializable> {
    public T readData(String key,RocksDB db) throws RocksDBException{
        byte[] keys = key.getBytes();
        DataTranform<T> transform = new DataTranform<>();
        T data = transform.BytesToData(db.get(keys));
        return data;
    }
}
