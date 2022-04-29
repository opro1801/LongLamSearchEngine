package resource.database;

import java.io.Serializable;
import java.util.List;

import resource.forward.WordData;
import resource.forward.*;

import java.io.*;
public class PageInfo implements Serializable{
    public List<String> title; //change later
    public String url;
    public String lastModified;
    public int size;
    public int max_frequency;
    public List<String> links;
    public List<WordData> wordInfo;
    public List<DataPair> wordFrequency;

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
    static PageInfo byteToPage(byte[] bytes){
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            @SuppressWarnings("unchecked")
            PageInfo page = (PageInfo) ois.readObject();
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