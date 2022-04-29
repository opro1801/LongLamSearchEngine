package resource.retrieval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import resource.forward.WordData;
import resource.processing.StopStem;


public class QueryHandle{
    String query;
    public QueryHandle(String query){
        this.query = query;
    }
    // what is this
    public List<WordData> listQuery() throws IOException{
        return StopStem.wordProcess().process(this.query);
    }
    // Convert the query into the list of word that in the wordList ( remove "" of phrase and remove word that not in wordList)
    public List<String> preProcess(List<String> wordList) throws IOException {
        //TODO
        List<String> result = new ArrayList<>();
        List<String> tmp = new ArrayList<>();
        //remove "" then convert the query into the list of words
        String newQuery = this.query.replaceAll("\"", "");
        String[] splittedQuery = newQuery.split(" ");
        for(String word : splittedQuery) {
            if(word.length()>0){
                tmp.add(word);
            }
        }
        //System.out.println("Debug");
        //System.out.println(tmp);
        //remove stopwords and stem the list of words
        tmp = StopStem.wordProcess().getListWords(tmp);
        //System.out.println(tmp);
        //if word is in wordList, then add to final results
        for(String word: wordList) {
            if(tmp.contains(word) && !result.contains(word)) {
                result.add(word);
            }
        }
        return result;
        // return null;
    }
    // Assume that phrase will be in the quota "" etract all phrase into a list;
    // Also need to preProcess (remove word that not in the database wordlist)
    public List<String> phrase(){
        //TODO
        //match all the phrase and add to result
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(query);
        while(matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
        // return null;
    }
    public List<Integer> vectorSpace() throws IOException{
        List<Integer> temp = new ArrayList<>();
        listQuery().forEach((e)->temp.add(e.count));
        return temp;
    }
}
