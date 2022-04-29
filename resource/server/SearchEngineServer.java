package resource.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import resource.database.ReadData;
import resource.forward.ForwardData;
import resource.forward.WordData;
import resource.retrieval.QueryResScore;
import resource.retrieval.Retrieval;

// import java.io.*;
import java.util.*;

public final class SearchEngineServer {

    private static final long serialVersionUID = 1L;

    /**
     * Respond to a GET request for the content produced by
     * this servlet.
     *
     * @param request  The servlet request we are processing
     * @param response The servlet response we are producing
     *
     * @exception IOException      if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     * @throws RocksDBException
     */
    public static void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException, RocksDBException {
        // String dbPath =
        // "/root/apache-tomcat-10.0.20/webapps/ROOT/WEB-INF/classes/resource/db";
        // RocksDB.loadLibrary();
        // String prefix = "Page@URL";
        // try {
        // Options options = new Options();
        // options.setCreateIfMissing(true);

        // // Creat and open the database
        // RocksDB db = RocksDB.open(options, dbPath);

        // ReadData<ForwardData> reads = new ReadData<ForwardData>();
        // } catch (RocksDBException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        String query = request.getParameter("query");
        QueryResScore results = Retrieval.doQuery(query);
        List<ForwardData> output = results.getResults();
        List<String> queryWordList = results.getQuery();
        List<Double> scores = results.getScores();
        DecimalFormat df = new DecimalFormat("#.###");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("<!DOCTYPE html><html>");
            writer.println("<head>");
            writer.println("<meta charset=\"UTF-8\" />");
            writer.println("<title>Search Engine Results</title>");
            writer.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
            writer.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
            writer.println(
                    "<link href='https://fonts.googleapis.com/css2?family=Josefin+Sans:wght@100&family=Righteous&display=swap' rel='stylesheet'>");
            writer.println("</head>");
            writer.println(
                    "<body style='padding-left: 30px; padding-right: 30px;text-overflow: ellipsis;font-family: 'Josefin Sans', sans-serif;font-family: 'Righteous', cursive;'>");

            // writer.println("<div style=\"float: left; padding: 10px;\">");
            // writer.println("<img src=\"images/tomcat.gif\" alt=\"\" />");
            // writer.println("</div>");
            writer.println("<h1>Results</h1>");
            writer.println("<h3>Results retrieved: " + output.size() + "</h3>");

            // writer.println("<p>");
            writer.println("<table style='width: 100%'>");
            for (int curRes = 0; curRes < output.size(); curRes++) {
                writer.println("<tr>");
                writer.println("<td style='vertical-align: top; width: 100px; margin-top: 20px; margin-right: 20px'>");
                writer.println("Score: " + df.format(scores.get(curRes)));
                writer.println("</td>");
                writer.println("<td style='vertical-align: top'>");
                writer.print("<a style='font-size:25px;text-decoration:none;color:#028FC8;' href='"
                        + output.get(curRes).url + "'>");
                writer.print(output.get(curRes).wordTitle);
                writer.println("</a>");
                writer.println("<br>");
                writer.print("<a style='text-decoration:none;color:#028FC8;' href='" + output.get(curRes).url + "'>");
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
                int maxLink = 5;
                int curLink = 0;
                if (output.get(curRes).parentLink != null)
                    writer.println("Parent Links:<br>");
                for (int link = 0; output.get(curRes).parentLink != null
                        && link < output.get(curRes).parentLink.size(); link++) {
                    if (output.get(curRes).parentLink.get(link) == null)
                        continue;
                    curLink++;
                    writer.print(
                            "<a style='text-decoration:none;color:#028FC8;' href='"
                                    + output.get(curRes).parentLink.get(link) + "'>");
                    writer.print(output.get(curRes).parentLink.get(link));
                    writer.println("</a>");
                    writer.println("<br>");
                    if (curLink >= maxLink)
                        break;
                }
                curLink = 0;
                if (output.get(curRes).childrenLink != null)
                    writer.println("Children Links:<br>");
                for (int link = 0; output.get(curRes).childrenLink != null
                        && link < output.get(curRes).childrenLink.size(); link++) {
                    curLink++;
                    writer.print("<a style='text-decoration:none;color:#028FC8;' href='"
                            + output.get(curRes).childrenLink.get(link)
                            + "'>");
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