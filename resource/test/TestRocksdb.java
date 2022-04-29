// package resource.test;
// import org.rocksdb.RocksDB;
// import org.rocksdb.Options;
// import org.rocksdb.RocksDBException;  
// import org.rocksdb.RocksIterator;
// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.io.ObjectInputStream;
// import java.util.ArrayList;
// import java.util.List;
// import java.io.Serializable;
// import java.io.ByteArrayInputStream;
// import java.io.IOException;
// import java.io.ObjectOutputStream;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Vector;
// import java.util.Collections;
// import java.util.HashSet;
// import java.util.HashMap;
// import java.util.StringTokenizer;
// import java.io.IOException;
// import java.lang.RuntimeException;
// import java.io.FileNotFoundException;
// import java.io.PrintStream;
// import java.util.regex.*;

// public class DataPair implements Serializable{
// 		private static final long serialVersionUID = 1L;
// 		public String data;
//         public int count;
//         public Integer position;
//         public DataPair(String data,int count){
//             this.data = data;
//             this.count = count;
//         }
//     }
// class StringPair implements Serializable{
// 		private static final long serialVersionUID = 1L;
// 		public String title;
//         public String url;
//         public StringPair(String title,String url){
//             this.title = title;
//             this.url = url;
//         }
//         public byte[] toByteArray(){

//             ByteArrayOutputStream bos = new ByteArrayOutputStream();
//             try {
//                 ObjectOutputStream oos = new ObjectOutputStream(bos);
//                 oos.writeObject(this);
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }
//             return bos.toByteArray();
//         }
//     }
// public class PageInfo implements Serializable{
//     public String title;
//     public String url;
//     public String lastModified;
//     public int size;
//     public List<String> links;
//     public List<DataPair> wordFrequency;
//     public byte[] toByteArray(){

//             ByteArrayOutputStream bos = new ByteArrayOutputStream();
//             try {
//                 ObjectOutputStream oos = new ObjectOutputStream(bos);
//                 oos.writeObject(this);
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }
//             return bos.toByteArray();
//     }
//     static PageInfo byteToPage(byte[] bytes){
//         ObjectInputStream ois;
//         try {
//             ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
//             @SuppressWarnings("unchecked")
//             PageInfo page = (PageInfo) ois.readObject();
//             return page;
//         } catch(IOException e){
//             e.printStackTrace();
//             return null;
//         } catch (ClassNotFoundException e) {
//             e.printStackTrace();
//             return null;
//         }
//     }
// }
// public class TestRocksdb {
//     public static void main(String[] args){
//         String dbPath = "/root/phase1/src/db";
//         int numOfDocument =30;
//         RocksDB.loadLibrary();
// 		String prefix = "Page@URL";
//         try {
//             // The Options class contains a set of configurable DB options
//             // that determines the behaviour of the database.
//             Options options = new Options();
//             options.setCreateIfMissing(true);

//             // Creat and open the database
//             RocksDB db;
//             db = RocksDB.open(options, dbPath);
//             PrintStream originalOut = System.out;
// 			PrintStream fileOut = new PrintStream("./spider_result.txt");
// 			System.setOut(fileOut);
// 			for(int i=1;i<=30;i++){
// 				String PageId = prefix+String.valueOf(i);
// 				byte[] keyId = PageId.getBytes();
// 				PageInfo cur = PageInfo.byteToPage(db.get(keyId));
//                 System.out.println("Page title: " +cur.title);
// 				System.out.println("URL: "+cur.url);
// 				System.out.printf("Last Modified: %s, ", cur.lastModified);
// 				System.out.printf("Size: %d Bytes\n", cur.size);
//                 List<DataPair> wordsFrequency = cur.wordFrequency;
//                 for(DataPair dat:wordsFrequency){
//                     System.out.print(dat.data + " " + dat.count + "; ");
//                 }
//                 System.out.println("");
// 				System.out.printf("\n\nLinks:");
//                 for(String link:cur.links){
//                     System.out.println(link);
//                 }
// 			}
// 			System.setOut(originalOut);
            
//         } catch (RocksDBException e) {
//                 System.err.println(e.toString());
//         } catch (FileNotFoundException e){

//         }
//     }
//     static byte[] DataPairToByte(List <DataPair> list) throws RocksDBException
//     {
//         ByteArrayOutputStream baos = new ByteArrayOutputStream();

//         ObjectOutputStream oos;
//         try {
//             oos = new ObjectOutputStream(baos);
//             oos.writeObject(list);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         // Convert to Byte Array
//         byte[] byteArray = baos.toByteArray();
//         return byteArray;
//     }
//     static void setForwardIndex(PageInfo pageInfo,String PageID,RocksDB db) throws RocksDBException{
//         byte[] keys = PageID.getBytes();
//         byte[] datas = pageInfo.toByteArray();
//         db.put(keys,datas);
//     }
//     static void setForwardPairIndex(List < DataPair > words,String PageID,RocksDB db) throws RocksDBException{
//         byte[] keys = PageID.getBytes();
//         byte[] datas = DataPairToByte(words);
//         db.put(keys,datas);
//     }
// }