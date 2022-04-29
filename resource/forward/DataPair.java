package resource.forward;

import java.io.Serializable;
// Supporting data for the real data
//Using in crawling procwss
public class DataPair implements Serializable,Comparable<DataPair>{
		private static final long serialVersionUID = 1L;
        // The word
		public String data;
        // Position of the word
        public int position;
        // Constructor
        public DataPair(String data,int position){
            this.data = data;
            this.position = position;
        }
        // Debug
        public void print(){
            System.out.println("Data pair "+data+ "\t"+position);
        }
        @Override 
        public int compareTo(DataPair other){
            return this.data.compareTo(other.data);
        }
    }