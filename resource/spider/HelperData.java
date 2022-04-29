package resource.spider;
import java.lang.RuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
class StringPair implements Serializable{
		private static final long serialVersionUID = 1L;
		public String title;
        public String url;
        public StringPair(String title,String url){
            this.title = title;
            this.url = url;
        }
        public byte[] toByteArray(){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bos.toByteArray();
        }
    }
class Link{
	String url;
	int level;
	Link (String url, int level) {  
	    this.url = url;
	    this.level = level; 
	}  
}

@SuppressWarnings("serial")

class RevisitException 
	extends RuntimeException {
	public RevisitException() {
	    super();
	}
}