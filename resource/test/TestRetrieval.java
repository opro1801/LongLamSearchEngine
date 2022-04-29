package resource.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import resource.forward.ForwardData;
import resource.forward.WordData;
import resource.retrieval.QueryResScore;
import resource.retrieval.Retrieval;
import org.rocksdb.RocksDBException;

public class TestRetrieval {
    public static void main(String args[]) {
        try {
            String query = "restaurant bread milk";
            PrintStream originalOut = System.out;
            PrintStream writer = new PrintStream("./retrieval_result_" + query + ".txt");
            // System.setOut(writer);
            QueryResScore results = Retrieval.doQuery(query);
            List<ForwardData> output = results.getResults();
            List<String> queryWordList = results.getQuery();
            List<Double> scores = results.getScores();
            DecimalFormat df = new DecimalFormat("#.###");
            // Retrieval.doQuery("food");
            // Retrieval.doQuery("food \"hong kong\"");
            writer.println("<!DOCTYPE html><html>");
            writer.println("<head>");
            writer.println("<meta charset=\"UTF-8\" />");
            writer.println("<title>Search Engine Results</title>");
            writer.println("</head>");
            writer.println("<body style='padding-left: 30px; padding-right: 30px;text-overflow: ellipsis;'>");

            // writer.println("<div style=\"float: left; padding: 10px;\">");
            // writer.println("<img src=\"images/tomcat.gif\" alt=\"\" />");
            // writer.println("</div>");
            writer.println("<h1>Results</h1>");
            writer.println("<h3>Results retrieved: " + output.size() + "</h3>");

            // writer.println("<p>");
            writer.println("<table style='width: 600px'>");
            for (int curRes = 0; curRes < output.size(); curRes++) {
                writer.println("<tr>");
                writer.println("<td style='vertical-align: top; width: 100px'>");
                writer.println("Score: " + df.format(scores.get(curRes)));
                writer.println("</td>");
                writer.println("<td style='vertical-align: top'>");
                writer.print("<a style='font-size:25px;text-decoration:none;' href='" + output.get(curRes).url + "'>");
                writer.print(output.get(curRes).wordTitle);
                writer.println("</a>");
                writer.println("<br>");
                writer.print("<a href='" + output.get(curRes).url + "'>");
                writer.print(output.get(curRes).url);
                writer.println("</a>");
                writer.println("<br>");
                // writer.print("No last modified date");
                if (output.get(curRes).lastModified.replaceAll("\\s+", "").equals("")) {
                    writer.print("No last modified date");
                } else {
                    writer.print(output.get(curRes).lastModified);
                }
                writer.print("; ");
                writer.print(output.get(curRes).size + " characters");
                List<WordData> keyWordListTitle = getKeyWordList(output.get(curRes).title, queryWordList);
                if (keyWordListTitle.size() > 0) {
                    writer.println("<br>");
                    writer.println("Title Matches: <br>");
                }
                for (WordData keyWord : keyWordListTitle) {
                    writer.print(keyWord.data + " " + keyWord.count + "; ");
                }
                List<WordData> keyWordList = getKeyWordList(output.get(curRes).content, queryWordList);
                if (keyWordList.size() > 0) {
                    writer.println("<br>");
                    writer.println("Content Matches: <br>");
                }
                for (WordData keyWord : keyWordList) {
                    writer.print(keyWord.data + " " + keyWord.count + "; ");
                }
                if (keyWordList.size() == 0 && keyWordListTitle.size() == 0) {
                    writer.print("No Keyword Matched <br>");
                }
                writer.println("<br>");
                int maxLink = 3;
                int curLink = 0;
                writer.println("Parent Links:<br>");
                for (int link = 0; output.get(curRes).parentLink != null
                        && link < output.get(curRes).parentLink.size(); link++) {
                    if (output.get(curRes).parentLink.get(link) == null)
                        continue;
                    curLink++;
                    writer.print("<a href='" + output.get(curRes).parentLink.get(link) + "'>");
                    writer.print(output.get(curRes).parentLink.get(link));
                    writer.println("</a>");
                    writer.println("<br>");
                    if (curLink >= maxLink)
                        break;
                }
                curLink = 0;
                writer.println("Children Links:<br>");
                for (int link = 0; output.get(curRes).childrenLink != null
                        && link < output.get(curRes).childrenLink.size(); link++) {
                    curLink++;
                    writer.print("<a href='" + output.get(curRes).childrenLink.get(link) + "'>");
                    writer.print(output.get(curRes).childrenLink.get(link));
                    writer.println("</a>");
                    writer.println("<br>");
                    if (curLink >= maxLink)
                        break;
                }
                writer.println("<br>");
                writer.println("</td>");
                writer.println("</tr>");
            }
            writer.println("</table>");
            // writer.println("</p>");

            writer.println("</body>");
            writer.println("</html>");

            // System.setOut(originalOut);
        } catch (RocksDBException e) {

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }

    public static List<WordData> getKeyWordList(List<WordData> content, List<String> query) {
        List<WordData> res = new ArrayList<>();
        for (WordData word : content) {
            if (query.contains(word.data)) {
                res.add(word);
            }
        }
        return res;
    }
}
