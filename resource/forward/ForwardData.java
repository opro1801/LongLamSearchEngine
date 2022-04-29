package resource.forward;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class ForwardData implements Serializable {
    public int pageId;
    public String url;
    // ..................
    public ArrayList<String> parentLink;
    public ArrayList<String> childrenLink;
    public String wordTitle;
    public String lastModified;
    public int size;
    public List<WordData> title;
    public List<WordData> content;

    public ForwardData(int pageId, String url, String wordTitle, String lastModified, ArrayList<String> parentLink,
            ArrayList<String> childrenLink, int size, List<WordData> title, List<WordData> content) {
        this.pageId = pageId;
        this.url = url;
        this.wordTitle = wordTitle;
        this.lastModified = lastModified;
        this.parentLink = parentLink;
        this.childrenLink = childrenLink;
        this.size = size;
        this.title = title;
        this.content = content;
    }

    public int MaxTfContent() {
        int max = 0;
        for (WordData data : content) {
            if (data.count > max) {
                max = data.count;
            }
        }
        return max;
    }

    public void updateParentLink(ArrayList<String> parentLink) {
        this.parentLink = parentLink;
    }

    public int MaxTfTitle() {
        int max = 0;
        for (WordData data : title) {
            if (data.count > max) {
                max = data.count;
            }
        }
        return max;
    }
    public void printinfo(){
        System.out.println(url);
        System.out.println(pageId);
    }
    // debug
    public void print() {
        System.out.println("Begin description");
        System.out.println(url);
        System.out.println(pageId);
        if (lastModified.equals("")) {
            System.out.println("No last modified");
        } else {
            System.out.println(lastModified);
        }
        System.out.println("Begin title");
        System.out.println("Title is " + wordTitle);
        int i;
        i = 0;
        for (WordData data : title) {
            data.print();
            i++;
            if (i > 10) {
                break;
            }
        }
        System.out.println("Begin content");
        i = 0;
        for (WordData data : content) {
            data.print();
            i++;
            if (i > 10) {
                break;
            }
        }
        System.out.println("Parent");
        System.out.println(parentLink);
        System.out.println("Children");
        System.out.println(childrenLink);
        System.out.println("End description");
    }

    // test print to html
    // public void print(PrintWriter out){
    // out.println("Begin description");
    // out.println(pageId);
    // out.println("Begin title");
    // for(WordData data:title){
    // data.print(out);
    // }
    // out.println("Begin content");
    // for(WordData data:content){
    // data.print(out);
    // }
    // out.println("End description");
    // }
    public byte[] toByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    static ForwardData byteToPage(byte[] bytes) {
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            ForwardData page = (ForwardData) ois.readObject();
            return page;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
