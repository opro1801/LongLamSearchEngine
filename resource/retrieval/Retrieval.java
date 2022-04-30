package resource.retrieval;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import resource.database.ReadData;
import resource.forward.ForwardData;
import resource.invert.InvertedData;
import resource.invert.SimplePageInfo;
import resource.processing.StopStem;

public class Retrieval {
    static String prefixForward = "Page@URL";
    static String prefixInvert = "Word@@";
    static String prefixWordList = "Word@List@";
    static String prefixTitleTf = "Title@maxTf";
    static String prefixContentTf = "Content@maxTf";
    static String prefixDf = "Word@Df";
    static String prefixNumFocs = "Number@Document";

    static double weight(int tf, int max_frequency, int doc_frequency, int num_docs) {
        // System.out.println("Information "+tf+" "+max_frequency+" "+doc_frequency+"
        // "+num_docs);

        double x = (double) tf / (double) max_frequency * Math.log((double) num_docs / (double) doc_frequency)
                / Math.log(2.0);
        // System.out.println(x);
        return x;
    }

    static private boolean isFinish(List<Boolean> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i)) {
                return false;
            }
        }
        return true;
    }
    // Doclist is the current list of document ( Because we need to filter many time
    // so need keep the current docList)
    // wordList is the list of word in the phrase example "hong kong" -> wordList =
    // ['hong','kong']
    // invertedFile is the list of inverted file for the word in the wordList ( in
    // order )
    // each inverted file is list of SimplePageInfo
    // Thuat toan la
    // co ban la cai inverted file se sort theo docId nen la giu 1 dong cacs array
    // 1,2,3
    // moi lan neu cac so dau tien cua day bang nhau thi xet vi tri
    // neu khong thi nhich array len 1 don vi :v

    /*
     * params: wordList is the list of a phrase "hong kong" => ["hong", "kong"]
     * invertedFile is the list of invertedData of "hong" and "kong"
     */

    static List<Integer> filter(
            List<String> wordList, List<List<SimplePageInfo>> invertedData) {
        System.out.println("WordList: " + wordList);
        int num_word = wordList.size();
        int firstWordId = 0;
        for (int i = 0; i < num_word; i++) {
            if (wordList.get(i).length() > 0) {
                firstWordId = i;
                break;
            }
        }
        // System.out.println("First word length" + wordList.get(0).length());
        // System.out.println("firstWordId: " + firstWordId);
        List<Boolean> finish = new ArrayList<>();
        List<Integer> finalDoc = new ArrayList<>();
        // System.out.println("num_word: " + num_word);
        // System.out.println("invertedData size: " + invertedData.size());
        if (num_word != invertedData.size())
            return finalDoc;
        for (int i = 0; i < num_word; i++) {
            if (wordList.get(i).length() > 0 && invertedData.get(i).size() == 0) {
                return finalDoc;
            }
        }
        // keep track of current index of the inverted content of each word
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < num_word; i++) {
            index.add(0);
            finish.add(false);
        }
        // choose the start document is the max docId among all first document of each
        // word
        int curDocId = -1;
        for (int i = 0; i < num_word; i++) {
            if (wordList.get(i).length() > 0 && invertedData.get(i).size() > 0
                    && curDocId < invertedData.get(i).get(index.get(i)).pageNumber) {
                curDocId = invertedData.get(i).get(index.get(i)).pageNumber;
            }
        }

        // System.out.println("startDocId: " + curDocId);
        // check if all the processing inverted data of each word are matched with
        // current docId
        // if yes, check if the position indicates a presence of phrase, if no,
        // increment docId by 1
        boolean isFinish = false;
        while (true) {

            for (int i = 0; i < num_word; i++) {
                // if the current doc Id is greater than the current inverted doc of current
                // word
                // then increment the index of the current inverted document of the word by 1
                if (wordList.get(i).length() == 0) {
                    continue;
                }
                while (index.get(i) < invertedData.get(i).size()
                        && invertedData.get(i).get(index.get(i)).pageNumber < curDocId) {
                    // System.out.println("index: " + index);
                    // for (int j = 0; j < num_word; j++) {
                    // if (wordList.get(j).length() > 0)
                    // System.out.println("pageId: " +
                    // invertedData.get(j).get(index.get(j)).pageNumber);
                    // }
                    index.set(i, index.get(i) + 1);
                    if (index.get(i) == invertedData.get(i).size()) {
                        isFinish = true;
                        break;
                    }
                    // after increment index, if the curDocId is less than the docId of the word
                    // then this curDocId won't be a match and we update the curDocId
                }
                if (isFinish)
                    break;
                if (invertedData.get(i).get(index.get(i)).pageNumber > curDocId) {
                    curDocId = invertedData.get(i).get(index.get(i)).pageNumber;
                }
                // System.out.println("curDocId: " + curDocId);
            }
            if (isFinish)
                break;

            // check if the curDocId is matched with all the curDocId of each word
            boolean isSamePage = true;
            for (int i = 0; i < num_word; i++) {
                if (wordList.get(i).length() == 0) {
                    // System.out.println("word length 0");
                    continue;
                }
                if (curDocId != invertedData.get(i).get(index.get(i)).pageNumber) {
                    isSamePage = false;
                    break;
                }
            }
            // if the curDocId is matched
            if (isSamePage) {
                // System.out.println("isSamePage");
                // check position
                // list of the possible starting position of phrase
                // List<Integer> firstWordTitlePosition =
                // invertedFile.get(0).title.get(index.get(0)).position;
                List<Integer> firstWordPosition = invertedData.get(firstWordId).get(index.get(firstWordId)).position;
                // firstWordPosition.addAll(firstWordTitlePosition);
                boolean isPhrase = true;
                for (int startPos = 0; startPos < firstWordPosition.size(); startPos++) {
                    // the position of the i word in the phrase should be the starting position +
                    // index of current word
                    for (int curWord = 1; curWord < num_word; curWord++) {
                        if (wordList.get(curWord).length() == 0)
                            continue;
                        if (!invertedData.get(curWord).get(index.get(curWord)).position
                                .contains(firstWordPosition.get(startPos) + curWord - firstWordId)) {
                            isPhrase = false;
                        }
                    }
                }
                if (isPhrase && !finalDoc.contains(curDocId)) {
                    finalDoc.add(curDocId);
                }
            }
            curDocId++;
            // if(isFinish) break;
        }
        System.out.println("Document contaning phrases: \n" + finalDoc);
        return finalDoc;
    }

    // TODO will add later
    static List<Double> similarity(HashMap<Integer, Integer> mapping, List<Integer> wordDocs, List<String> words,
            List<InvertedData> invertedFile, QueryHandle query, int numDocs,
            MaxTf titleMaxTf, MaxTf contentMaxTf, DfWord df)
            throws IOException {
        List<Double> contentResults = new ArrayList<>();
        List<Double> contentNorm = new ArrayList<>();
        List<Double> titleResults = new ArrayList<>();
        List<Double> titleNorm = new ArrayList<>();
        List<Integer> vectorSpace = query.vectorSpace();
        for (int i = 0; i < wordDocs.size(); i++) {
            contentResults.add(0.0);
            contentNorm.add(0.0);
            titleResults.add(0.0);
            titleNorm.add(0.0);
        }
        // System.out.println("Start df");
        // for(String word:words){
        // System.out.println(df.df.get(word));
        // }
        // System.out.println("End df");
        // System.out.println("Max title tf");
        // System.out.println(titleMaxTf.max_tf);
        // System.out.println("Max title tf");
        // System.out.println("Max content tf");
        // System.out.println(contentMaxTf.max_tf);
        // System.out.println("Max content tf");
        for (int i = 0; i < invertedFile.size(); i++) {
            InvertedData current = invertedFile.get(i);
            List<SimplePageInfo> tit = current.title;
            List<SimplePageInfo> cont = current.content;
            for (SimplePageInfo page : tit) {
                int id = page.pageNumber;
                // System.out.println("Page id is"+id);
                double termWeight = Retrieval.weight(page.count, titleMaxTf.max_tf.get(id), df.df.get(words.get(i)),
                        numDocs);
                // System.out.println("Weight is"+termWeight);
                titleResults.set(id, titleResults.get(id) + vectorSpace.get(i) * termWeight);
                titleNorm.set(id, titleNorm.get(id) + termWeight * termWeight);
            }
            for (SimplePageInfo page : cont) {
                int id = page.pageNumber;
                // System.out.println("Page id is"+id);
                double termWeight = Retrieval.weight(page.count, contentMaxTf.max_tf.get(id), df.df.get(words.get(i)),
                        numDocs);
                // System.out.println("Weight is"+termWeight);
                contentResults.set(id, contentResults.get(id) + vectorSpace.get(i) * termWeight);
                contentNorm.set(id, contentNorm.get(id) + termWeight * termWeight);
            }
        }
        // System.out.println("result is"+contentResults);
        // System.out.println(" norm is"+contentNorm);
        double queryNorm = 0.0;
        for (int i = 0; i < invertedFile.size(); i++) {
            queryNorm += vectorSpace.get(i) * vectorSpace.get(i);
        }
        // System.out.println("Query norm is"+queryNorm);
        queryNorm = Math.sqrt(queryNorm);
        // for(InvertedData data:invertedFile){
        // data.print();
        // }
        for (int i = 0; i < wordDocs.size(); i++) {
            if (titleNorm.get(i) > 0) {
                titleResults.set(i, titleResults.get(i) / (Math.sqrt(titleNorm.get(i)) * queryNorm));
            }
            if (contentNorm.get(i) > 0) {
                contentResults.set(i, contentResults.get(i) / (Math.sqrt(contentNorm.get(i)) * queryNorm));
            }
        }
        List<Double> results = new ArrayList<Double>();
        for (int i = 0; i < wordDocs.size(); i++) {
            results.add(5 * titleResults.get(i) + contentResults.get(i));
        }
        return results;
    }

    public static QueryResScore doQuery(String query) throws RocksDBException, IOException {
        // TODO
        // Variable
        String dbPath = "/root/apache-tomcat-10.0.20/webapps/ROOT/WEB-INF/classes/resource/db";
        QueryHandle handler = new QueryHandle(query);
        ReadData<ForwardData> readForward = new ReadData<>();
        ReadData<ArrayList<String>> readWordList = new ReadData<>();
        ReadData<InvertedData> readInvert = new ReadData<>();
        ReadData<ArrayList<Integer>> readTf = new ReadData<>();
        ReadData<HashMap<String, Integer>> readDf = new ReadData<>();
        ReadData<Integer> readNumDocs = new ReadData<>();
        ArrayList<InvertedData> invertList = new ArrayList<>();
        // Database set up
        Options options = new Options();
        options.setCreateIfMissing(true);
        // Creat and open the database
        RocksDB db = RocksDB.openReadOnly(options, dbPath);

        // Start handle
        // get database
        ArrayList<String> wordList = readWordList.readData(prefixWordList, db);
        ArrayList<Integer> titleMaxTf = readTf.readData(prefixTitleTf, db);
        ArrayList<Integer> contentMaxTf = readTf.readData(prefixContentTf, db);
        HashMap<String, Integer> df = readDf.readData(prefixDf, db);
        int numDocs = readNumDocs.readData(prefixNumFocs, db);
        System.out.println("numDocs" + numDocs);
        DfWord curDf = new DfWord(df);
        MaxTf TitlemaxTf = new MaxTf(titleMaxTf);
        MaxTf ContetmaxTf = new MaxTf(contentMaxTf);
        // Start
        List<String> queryWordList = handler.preProcess(wordList);
        System.out.println("query: " + query);
        System.out.println("queryWordList: " + queryWordList);
        List<ForwardData> forwardDatas = new ArrayList<>();

        for (String word : queryWordList) {
            invertList.add(readInvert.readData(prefixInvert + word, db));
        }
        // for(InvertedData i:invertList){
        // i.print();
        // }
        List<Integer> docsList = new ArrayList<>();
        for (int i = 0; i < numDocs; i++) {
            docsList.add(i);
        }
        // System.out.println(numDocs);
        // filter
        List<Integer> listDocsWithPhraseMatch = new ArrayList<>();
        List<String> phrases = handler.phrase();
        for (String phrase : phrases) {
            List<String> phraseList = Arrays.asList(phrase.split(" "));
            if (phraseList.size() <= 1)
                continue;
            System.out.println("phraseList: " + phraseList);
            List<String> lists = new ArrayList<>();
            for (int i = 0; i < phraseList.size(); i++) {
                String tmpWord = StopStem.wordProcess().stopStemWord(phraseList.get(i).toLowerCase());
                // System.out.println("tmpWord:" + tmpWord);
                if (tmpWord.length() > 0 && !wordList.contains(tmpWord)) {
                    tmpWord = "";
                }
                lists.add(tmpWord);
            }
            // List<String> lists = StopStem.wordProcess().getListWords(phraseList);

            // System.out.println(lists);
            List<List<SimplePageInfo>> invertedTitle = new ArrayList<>();
            List<List<SimplePageInfo>> invertedContent = new ArrayList<>();
            List<InvertedData> invertedFile = new ArrayList<>();
            for (String s : lists) {
                if (!wordList.contains(s)) {
                    invertedFile.add(null);
                    invertedContent.add(null);
                    invertedTitle.add(null);
                    continue;
                }
                InvertedData data = readInvert.readData(prefixInvert + s, db);
                if (data != null) {
                    invertedFile.add(data);
                    invertedTitle.add(data.title);
                    invertedContent.add(data.content);
                }
            }
            // for (InvertedData d : invertedFile) {
            // d.print();
            // }
            // System.out.println("run filter");
            listDocsWithPhraseMatch.addAll(filter(lists, invertedContent));
            for (Integer docId : filter(lists, invertedTitle)) {
                if (!listDocsWithPhraseMatch.contains(docId)) {
                    listDocsWithPhraseMatch.add(docId);
                }
            }
        }
        //
        // computer similarity
        HashMap<Integer, Integer> mapp = new HashMap<>();
        for (int i = 0; i < docsList.size(); i++) {
            mapp.put(docsList.get(i), i);
        }
        //
        List<Double> finalSimilarity = similarity(mapp, docsList, queryWordList, invertList, handler,
                numDocs, TitlemaxTf, ContetmaxTf, curDf);
        // System.out.println(finalSimilarity);
        // output forward index list
        List<PairDocs> pairDocs = new ArrayList<>();
        List<Double> scores = new ArrayList<>();
        for (int i = 0; i < docsList.size(); i++) {
            if (phrases.size() > 0 &&
                    !listDocsWithPhraseMatch.contains(docsList.get(i))) {
                pairDocs.add(new PairDocs(docsList.get(i), finalSimilarity.get(i) * 0.5));
            } else {
                pairDocs.add(new PairDocs(docsList.get(i), finalSimilarity.get(i)));
            }
        }
        Collections.sort(pairDocs);
        int count = 0;
        // System.out.println(handler.phrase());
        // for (PairDocs x : pairDocs) {
        // System.out.println(x.pageId + " and" + x.score);
        // count++;
        // if (count > 20) {
        // break;
        // }
        // }
        for (int i = 0; i < 50; i++) {
            if (i >= docsList.size()) {
                break;
            }
            if (pairDocs.get(i).score == 0)
                break;
            scores.add(pairDocs.get(i).score);
            forwardDatas.add(readForward.readData(prefixForward + pairDocs.get(i).pageId, db));
        }
        System.out.println("Results retrieved: " + forwardDatas.size());
        for (ForwardData x : forwardDatas) {
            x.printinfo();
        }
        // Here is top up to 50 docs // fowardDatas
        if (db != null) {
            db.cancelAllBackgroundWork(true);
            db.close();
        }
        return new QueryResScore(queryWordList, forwardDatas, scores);
    }
}

class PairDocs implements Comparable<PairDocs> {
    Integer pageId;
    Double score;

    public PairDocs(int pageId, double score) {
        this.pageId = pageId;
        this.score = score;
    }

    @Override
    public int compareTo(PairDocs other) {
        if (this.score.compareTo(other.score) != 0) {
            return -this.score.compareTo(other.score);
        } else {
            return this.pageId.compareTo(other.pageId);
        }
    }
}