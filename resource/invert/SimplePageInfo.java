package resource.invert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class SimplePageInfo implements Serializable{
    // The index of the page
    public int pageNumber;
    // Number of time that this word appear in this document
    public int count;
    // The list of position that the word appear in the document
    public List<Integer> position;
    // Constructor
    public SimplePageInfo(int pageNumber,int count,List<Integer> position){
        this.pageNumber = pageNumber;
        this.count = count;
        this.position = position;
    }
    // Debug
    public void print(){
        System.out.println("Description of file is "+pageNumber);
        System.out.println("List of position is "+position);
    }
    public byte[] toByteArray(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }
    static SimplePageInfo byteToPage(byte[] bytes){
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            SimplePageInfo page = (SimplePageInfo) ois.readObject();
            return page;
        } catch(IOException e){
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
