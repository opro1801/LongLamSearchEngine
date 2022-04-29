package resource.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import resource.forward.WordData;
import resource.processing.StopStem;
import resource.retrieval.QueryHandle;

public class TestQueryHandle{
        public static void main(String arg[]){
                List<String> wordList = new ArrayList<>();
                wordList.add("good");
                wordList.add("food");
                wordList.add("hong");
                wordList.add("kong");
                wordList.add("find");
                wordList.add("road");
                QueryHandle query = new QueryHandle("good food in \"hong kong\" and also find the road to \" Tsun Kwan \" ");
                //System.out.println(query.phrase());
                try {
                    System.out.println(query.preProcess(wordList));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                        List<WordData> lquery = query.listQuery();
                        for(WordData l:lquery){
                                l.print();
                        }
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }
}       