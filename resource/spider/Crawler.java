package resource.spider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.*;
import java.util.stream.Collectors;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import resource.database.ReadData;
import resource.database.WriteData;
import resource.forward.CreateForward;
import resource.forward.ForwardData;
import resource.invert.*;
import resource.processing.*;

public class Crawler {
	private HashSet<String> urls;     // the set of urls that have been visited before
	public Vector<Link> todos; // the queue of URLs to be crawled
	private int max_crawl_depth = 9000;  // feel free to change the depth limit of the spider.
	
	public Crawler(String _url) {
		this.todos = new Vector<Link>();
		this.todos.add(new Link(_url, 1));
		this.urls = new HashSet<String>();
	}
	
	/**
	 * Send an HTTP request and analyze the response.
	 * @return {Response} res
	 * @throws HttpStatusException for non-existing pages
	 * @throws IOException
	 */
	public Response getResponse(String url) throws HttpStatusException, IOException {
		if (this.urls.contains(url)) {
			throw new RevisitException(); // if the page has been visited, break the function
		 }
		
		// Connection conn = Jsoup.connect(url).followRedirects(false);
		// the default body size is 2Mb, to attain unlimited page, use the following.
		Connection conn = Jsoup.connect(url).maxBodySize(0).followRedirects(false);
		Response res;
		try {
			/* establish the connection and retrieve the response */
			 res = conn.execute();
			 /* if the link redirects to other place... */
			 if(res.hasHeader("location")) {
				 String actual_url = res.header("location");
				 if (this.urls.contains(actual_url)) {
					throw new RevisitException();
				 }
				 else {
					 this.urls.add(url);
					 //System.out.println(actual_url);
				 }
			 }
			 else {
				 this.urls.add(url);
				 //System.out.println(url);
			 }
		} catch (HttpStatusException e) {
			throw e;
		}
		/* Get the metadata from the result */
		//String lastModified = res.header("last-modified");
		//int size = res.bodyAsBytes().length;
		//String htmlLang = res.parse().select("html").first().attr("lang");
		//String bodyLang = res.parse().select("body").first().attr("lang");
		//String lang = htmlLang + bodyLang;
		// System.out.printf("Last Modified: %s\n", lastModified);
		// System.out.printf("Size: %d Bytes\n", size);
		//System.out.printf("Language: %s\n", lang);
		return res;
	}


	public static boolean isValidURL(String url){
        String regex = "((http|https)://)(www.)?"
              + "[a-zA-Z0-9@:%._\\+~#?&//=]"
              + "{2,256}\\.[a-z]"
              + "{2,6}\\b([-a-zA-Z0-9@:%"
              + "._\\+~#?&//=]*)";
        Pattern p = Pattern.compile(regex);
        if (url == null) {
            return false;
        }
        return p.matcher(url).matches();
        // return m.matches();
    }

	/** Extract words in the web page content.
	 * note: use StringTokenizer to tokenize the result
	 * @param {Document} doc
	 * @return {Vector<String>} a list of words in the web page body
	 */
	public Vector<String> extractWords(Document doc) {
		 Vector<String> result = new Vector<String>();
		// ADD YOUR CODES HERE
		 String contents = doc.body().text(); 
	     StringTokenizer st = new StringTokenizer(contents);
	     while (st.hasMoreTokens()) {
			String[] cleanStrings = st.nextToken().split("[$&+,:;=?@#|'<>.^*()%!-]");
			for(int temp = 0; temp < cleanStrings.length; temp++) {
				if(cleanStrings[temp].equals("")) continue;
				result.add(cleanStrings[temp]);
			}
			// result.add(st.nextToken());
	     }
	     return result;		
	}
	
	// TODO Filter the invalid url

	/** Extract the title */
	public String extractTitle(Document doc){
		return doc.title();
	}
	public HashMap<String, Integer> vectorToMap(Vector<String> words) {
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		for(String word : words) {
			Integer cnt = hm.get(word);
			hm.put(word, (cnt == null) ? 1 : cnt + 1);
		}
		return hm;
	}
	/** Extract useful external urls on the web page.
	 * note: filter out images, emails, etc.
	 * @param {Document} doc
	 * @return {Vector<String>} a list of external links on the web page
	 */
	public Vector<String> extractLinks(Document doc) {
		Vector<String> result = new Vector<String>();
		// ADD YOUR CODES HERE
        Elements links = doc.select("a[href]");
        for (Element link: links) {
        	String linkString = link.attr("href");
        	// filter out emails
        	if (linkString.contains("mailto:")) {
        		continue;
        	}
            result.add(link.attr("href"));
        }
        return result;
	}
	
	
	/**
		Add inverted file to database
	*/

	/** Use a queue to manage crawl tasks.
	 */

	// TODO Finish inverted file and two global db
	public void crawlLoop(RocksDB db) {
		// Key
        String prefixForward = "Page@URL";
		String prefixInvert = "Word@@";
		String prefixWordList = "Word@List@";
		String prefixTitleTf = "Title@maxTf";
		String prefixContentTf = "Content@maxTf";
		String prefixDf = "Word@Df";
		String prefixNumFocs = "Number@Document";
		int docId = 0;
		// Data
		List<ForwardData> forwardData = new ArrayList<>();
		ArrayList<String> wordList = new ArrayList<>();
		ArrayList<Integer> titleMaxTf  = new ArrayList<>();
		ArrayList<Integer> contentMaxTf  = new ArrayList<>();
		HashMap<String,Integer> df_word = new HashMap<String,Integer>();
		HashMap<String,ArrayList<String> > parents = new HashMap<>();
		HashMap<String,Integer> urlToPageId = new HashMap<>();
		
		// Read interface
		ReadData<ForwardData> reader = new ReadData<>();
		// write interface
		WriteData<ForwardData> writer = new WriteData<>();
		WriteData<InvertedData> writeIntert = new WriteData<>();
		WriteData< ArrayList<String> > writeWordList = new WriteData<>();
		WriteData< HashMap<String,Integer> > writeDf = new WriteData<>();
		WriteData< ArrayList<Integer> > writeMaxTf = new WriteData<>();
		WriteData< Integer > writeNumDocs = new WriteData<>();
		// UrlValidator urlValidator = new UrlValidator();
		while(!this.todos.isEmpty()) {
			Link focus = this.todos.remove(0);
			if (focus.level > this.max_crawl_depth || docId == 9000) break; // stop criteria
			if (this.urls.contains(focus.url)) continue;   // ignore pages that has been visited
			if (!isValidURL(focus.url)) continue;
			/* start to crawl on the page */
			try {

				Response res = this.getResponse(focus.url);

				urlToPageId.put(focus.url,docId);

				int size = res.bodyAsBytes().length;

				Document doc = res.parse();

				String curTitle = extractTitle(doc);

				List<String> titleWords = Arrays.asList(curTitle.split(" "));

				Vector<String> words = this.extractWords(doc);

				String dataContent = String.join(" ",words);

				Vector<String> links = this.extractLinks(doc);

				ArrayList<String> childLinks = new ArrayList<>();
				for(String link:links){
					if(isValidURL(link)){
						childLinks.add(link);
						this.todos.add(new Link(link, focus.level + 1)); // add links
					}
					
				}
				//System.out.println(lastModified);
				//filter out page that does not have dateModified
				//if(lastModified == null) continue;
				//filter out page that does not have title
				if(curTitle == null || curTitle == "") continue;
				if(curTitle.contains("301")) continue;
				if(curTitle.contains("302")) continue;
				if(curTitle.contains("????")) continue;
				if(curTitle.contains("Document Moved")) continue;
				//filter out page that does not have content
				if(dataContent == null || dataContent == "" || words.size() == 0) continue;
				//System.out.println(curTitle);
				if(docId%100==0){
					System.out.println("process "+docId);
				}
				String lastModified = res.header("last-modified");
				if(lastModified==null){
					lastModified = "";
				}
				wordList.addAll(StopStem.wordProcess().getListWords(titleWords));
				wordList.addAll(StopStem.wordProcess().getListWords(words));
				// urls.add(focus.url);
				// sizes.add(size);
				// title.add(curTitle);
				// content.add(dataContent);
				//System.out.println(docId);	
				//System.out.println(curTitle);
				ForwardData datas = CreateForward.Index(docId,focus.url,size,lastModified,curTitle,
													    new ArrayList<String>(),childLinks,curTitle,dataContent);
				titleMaxTf.add(datas.MaxTfTitle());
				contentMaxTf.add(datas.MaxTfContent());
				forwardData.add(datas);
				writer.writeToDb(prefixForward+String.valueOf(docId),datas,db);
				//System.out.println(docId);
				//datas.print();
				for(String link: links) {
					if(isValidURL(link)){
						ArrayList<String> temp = parents.get(link);
						if(temp==null){
							ArrayList<String> new_list = new ArrayList<>();
							new_list.add(focus.url);
							parents.put(link,new_list);
						}
						else{
							temp.add(focus.url);
							parents.put(link,temp);
						}
					}
				}
			} catch (HttpStatusException e) {
				//System.out.printf("\nLink Error: %s\n", focus.url);
				docId--;
	    	} catch (IOException e) {
				docId--;
				//System.out.println("IO ERROR");
	    		//e.printStackTrace();
	    	} catch (RevisitException e) {
				
				//System.out.println("Revisit ERROR");
				docId--;
	    	}
			catch (RocksDBException e) {
				//System.out.println("SDB ERROR");
			// TODO Auto-generated catch block
				//e.printStackTrace();
				docId--;
			}
			docId++;
		}
		// Debug
		try{
			for(int i=0;i<docId;i++){
				ForwardData temp = reader.readData(prefixForward+String.valueOf(i), db);
				temp.updateParentLink(parents.get(temp.url));
				// if(i<10){
				// 	System.out.println(parents.get(temp.url));
				// }
				writer.writeToDb(prefixForward+String.valueOf(i),temp,db);
			}
		} catch(RocksDBException e){
			System.out.println("SDB2 ERROR");
			e.printStackTrace();
		}
		//TODO Update the parent link
		// read from db
		// update
		// write back to db
		//done but quite lazy


		// create the inverted file
		ArrayList<String> finalWordList = (ArrayList<String>) wordList.stream().distinct().collect(Collectors.toList());
		HashMap<String,InvertedData> finalInvertedFile =  CreateInverted.invertedFile(finalWordList,forwardData);
		//create the global files
		try{
			for(String word:finalWordList){
				writeIntert.writeToDb(prefixInvert+word,finalInvertedFile.get(word),db);
				df_word.put(word,finalInvertedFile.get(word).df_word());
			}
			writeWordList.writeToDb(prefixWordList,finalWordList,db);
			writeDf.writeToDb(prefixDf,df_word,db);
			writeMaxTf.writeToDb(prefixTitleTf,titleMaxTf,db);
			writeMaxTf.writeToDb(prefixContentTf,contentMaxTf,db);
			writeNumDocs.writeToDb(prefixNumFocs,docId,db);
		} catch(RocksDBException e){
			System.out.println("SDB3 ERROR");
			e.printStackTrace();
		}
	}
}