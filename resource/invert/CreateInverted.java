package resource.invert;
// Using this to create inverted file
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import resource.forward.ForwardData;
import resource.forward.WordData;

public class CreateInverted {
    //create the inverted file from the forward file
    // words is list of word after remove stop and stem
    public static HashMap<String,InvertedData> invertedFile(List<String> words,List<ForwardData> forwardIndex){
        HashMap<String,InvertedData> result = new HashMap<>();
        for(String word:words){
            result.put(word, new InvertedData(new ArrayList<SimplePageInfo>(), new ArrayList<SimplePageInfo>()));
        }
        for(ForwardData file:forwardIndex){
            for(WordData wordData:file.title){
                SimplePageInfo newWordData = new SimplePageInfo(file.pageId,wordData.count,wordData.position);
                result.get(wordData.data).addToTitle(newWordData);;
            }
            for(WordData wordData:file.content){
                SimplePageInfo newWordData = new SimplePageInfo(file.pageId,wordData.count,wordData.position);
                result.get(wordData.data).addToContent(newWordData);;
            }
        }
        return result;
    }
}
