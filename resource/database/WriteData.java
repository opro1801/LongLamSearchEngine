package resource.database;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;  
import java.io.Serializable;
public class WriteData<T extends Serializable> {
    public void writeToDb(String key,T data,RocksDB db) throws RocksDBException{
        byte[] keys = key.getBytes();
        DataTranform<T> writes = new DataTranform<T>();
        byte[] datas = writes.DataToByte(data);
        db.put(keys,datas);
    }
}