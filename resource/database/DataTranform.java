package resource.database;
import java.io.*;
public class DataTranform<T extends Serializable> {
    byte[] DataToByte(T data)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Convert to Byte Array
        byte[] byteArray = baos.toByteArray();
        return byteArray;
    }
    T BytesToData(byte[] bytes){
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            @SuppressWarnings("unchecked")
            T data = (T) ois.readObject();
            return data;
        } catch(IOException e){
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}