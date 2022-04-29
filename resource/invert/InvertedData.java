package resource.invert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InvertedData implements Serializable{
    // List of inveted file for the word in the title of the page
    public List<SimplePageInfo> title;
    // List of inverted file for the word in the content of the page
    public List<SimplePageInfo> content;
    public InvertedData(List<SimplePageInfo> title,List<SimplePageInfo> content){
        this.title=title;
        this.content=content;
    }
    public void addToContent(SimplePageInfo other){
        this.content.add(other);
    }
    public void addToTitle(SimplePageInfo other){
        this.title.add(other);
    }
    public int df_word(){
        List<Integer> listPage = new ArrayList<>();
        for(SimplePageInfo page:title){
            if(!listPage.contains(page.pageNumber)){
                listPage.add(page.pageNumber);
            }
        }
        for(SimplePageInfo page:content){
            if(!listPage.contains(page.pageNumber)){
                listPage.add(page.pageNumber);
            }
        }
        return listPage.size();
    }
    // Debug
    public void print(){
        System.out.println("Begin description");
        System.out.println("Begin title");
        for(SimplePageInfo data:title){
            data.print();
        }
        System.out.println("Begin content");
        for(SimplePageInfo data:content){
            data.print();
        }
        System.out.println("End description");
    }
}
