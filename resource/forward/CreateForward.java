package resource.forward;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import resource.processing.StopStem;

public class CreateForward {
    //create forward index from the list of title and list of content
    public static HashMap<Integer,ForwardData> forwardIndex(
        List<String> urls,List<Integer> sizes,ArrayList<String> dateModifieds,ArrayList<String> titles,
        ArrayList<ArrayList<String> > parent,ArrayList<ArrayList<String> > children,List<String> title,List<String> content
        ) throws IOException{
        HashMap<Integer,ForwardData> forward = new HashMap<>();
        for(int i=0;i<title.size();i++){
            List<WordData> titleData = StopStem.wordProcess().process(title.get(i));
            List<WordData> contentData = StopStem.wordProcess().process(content.get(i));
            forward.put(i, new ForwardData(i,urls.get(i),titles.get(i),dateModifieds.get(i),
                                           parent.get(i),children.get(i),sizes.get(i), titleData, contentData));
        }
        return forward;
    }
    public static ForwardData Index(int index,String url,int size, String dateModified,String wordTitle,
                                    ArrayList<String> parent,ArrayList<String> children,String title,String content) 
                                    throws IOException{
        List<WordData> titleData = StopStem.wordProcess().process(title);
        List<WordData> contentData = StopStem.wordProcess().process(content);
        return new ForwardData(index,url,wordTitle,dateModified,parent,children,size,titleData,contentData);
    }
}
