package resource.forward;

// import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class WordData implements Serializable,Comparable<WordData>{
		private static final long serialVersionUID = 1L;
        // the word
		public String data;     
        // number of time that the word in the docs
        public int count;
        // list of position of the word in the docs
        public ArrayList<Integer> position = new ArrayList<>();
        // Constructor
        public WordData(String data,int count,ArrayList<Integer> position){
            this.data = data;
            this.count = count;
            this.position = position;
        }
        //Constructor from list of DataPair from a list of same word
        // Use for processing
        public WordData(List<DataPair> other){
            this.data = other.get(0).data;
            this.count = other.size();
            for(DataPair dataPair:other){
                this.position.add(dataPair.position);
            }
        }
        // Using to debug 
        public void print(){
            System.out.println("Description of word is "+data);
            System.out.println("Description of count is"+count);
            System.out.println("List of position is "+position);
        }
        // //Use to test print to html
        // public void print(PrintWriter out){
        //     out.println("Description of word is "+data);
        //     out.println("List of position is "+position);
        // }
        // Using for processing
        @Override
        public int compareTo(WordData other){
            return this.data.compareTo(other.data);
        }
}