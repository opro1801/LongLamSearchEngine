package resource.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import resource.forward.CreateForward;
import resource.forward.ForwardData;
import resource.invert.CreateInverted;
import resource.invert.InvertedData;
import resource.processing.StopStem;

public class TestInvertData {
    public static void main(String[] arg){
        List<String> title = new ArrayList<>();
        List<String> content = new ArrayList<>();
        title.add("Hello world play play with me");
        content.add("Hello will you play entertainment");
        List<String> allData = new ArrayList<>();
        allData.addAll(title);
        allData.addAll(content);
        
        // try {
        //     List<String> words = StopStem.wordProcess().getListWords(allData);
        //     HashMap<Integer,ForwardData> test = CreateForward.forwardIndex(title, content);
        //     List<ForwardData> temp = new ArrayList<>();
        //     for(int i=0;i<title.size();i++){
        //         temp.add(test.get(i));
        //     }
        //     HashMap<String,InvertedData> testInvert = CreateInverted.invertedFile(words, temp);
        //     for(String word:words){
        //         System.out.println(word);
        //         InvertedData temps = testInvert.get(word);
        //         temps.print();
        //     }
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
    }
}
