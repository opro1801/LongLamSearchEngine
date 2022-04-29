package resource.processing;

import resource.forward.WordData;
import resource.forward.DataPair;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StopStem {
	public static StopStem wordProcess() throws IOException {
		return new StopStem("resource/processing/stopwords-en.txt");
	}

	private Porter porter;
	private java.util.HashSet<String> stopWords;

	public boolean isStopWord(String str) {
		return stopWords.contains(str);
	}

	public String stopStemWord(String word) {
		if (word == null || word.length() == 0 || isStopWord(word)) {
			return "";
		}
		if (stem(word).length() > 0)
			return stem(word);
		return "";
	}

	public List<String> getListWords(List<String> data) {
		List<String> result = new ArrayList<>();
		for (String page : data) {
			List<String> temp = Arrays.asList(page.split(" "));
			for (String word : temp) {

				// System.out.println(word);
				if (word != null && word.length() > 0 && !isStopWord(word)) {
					// System.out.println(word);
					if (!result.contains(stem(word))) {
						if (stem(word).length() > 0) {
							result.add(stem(word));
						}
					}
				}
			}
		}
		return result;
	}

	public StopStem(String str) throws IOException {
		super();
		porter = new Porter();
		stopWords = new java.util.HashSet<String>();
		List<String> content = Files.readAllLines(Paths.get(str));
		// CHANGE THIS PART TO FILE CONTENT
		stopWords.addAll(content);
	}

	public String stem(String str) {
		return porter.stripAffixes(str);
	}

	public List<WordData> process(String doc) {
		ArrayList<WordData> indexers = new ArrayList<WordData>();
		// StopStem stopStem = new StopStem("project/stopwords-en.txt");
		String input = doc;
		List<String> listwords = Arrays.asList(input.split(" "));
		// System.out.println("Debug");
		// System.out.println(listwords);
		List<DataPair> temp_index = new ArrayList<>();
		for (int i = 0; i < listwords.size(); i++) {
			if (listwords.get(i).length() > 0) {
				if (!isStopWord(listwords.get(i))) {
					String stem = stem(listwords.get(i));
					if (stem.length() > 0) {
						temp_index.add(new DataPair(stem, i));
					}
				}
			}
		}
		Collections.sort(temp_index);
		ArrayList<DataPair> temp = new ArrayList<DataPair>();
		for (int i = 0; i < temp_index.size(); i++) {
			if (i == 0) {
				temp.add(temp_index.get(i));
			} else {
				if (temp_index.get(i).compareTo(temp_index.get(i - 1)) == 0) {
					temp.add(temp_index.get(i));
				} else {
					indexers.add(new WordData(temp));
					temp.clear();
					temp.add(temp_index.get(i));
				}
			}
		}
		if (!temp.isEmpty()) {
			indexers.add(new WordData(temp));
		}
		return indexers;
	}

}
