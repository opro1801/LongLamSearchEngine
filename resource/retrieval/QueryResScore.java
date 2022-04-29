package resource.retrieval;

import java.util.List;

import resource.forward.ForwardData;

public class QueryResScore {
    List<String> queryWordList;
    List<ForwardData> results;
    List<Double> scores;

    public QueryResScore(List<String> queryWordList, List<ForwardData> results, List<Double> scores) {
        this.queryWordList = queryWordList;
        this.results = results;
        this.scores = scores;
    }

    public List<String> getQuery() {
        return queryWordList;
    }

    public List<ForwardData> getResults() {
        return results;
    }

    public List<Double> getScores() {
        return scores;
    }
}
