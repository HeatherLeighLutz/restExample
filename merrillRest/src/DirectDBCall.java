import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DirectDBCall {
	
	private static List<ServerAddress> addresses = new ArrayList<ServerAddress>();
	private static List<MongoCredential> creds = new ArrayList<MongoCredential>();
	static {
		creds.add(MongoCredential.createCredential("mongoAdmin", "admin", "@dm!n".toCharArray()));
		addresses.add(new ServerAddress("mgd-us2s-gd-1a.dmz01.mrll.com", 27017));

	}
	
	public static void main(String[] args) {
		
		
		
		try {
			
			MongoClient client  = new MongoClient(addresses, creds);		
			MongoDatabase db = client.getDatabase("userDetails");		
			MongoCollection coll = db.getCollection("userDetails");
			
			
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd, mm:ss:SS");
			double avg = 0;
			int iterations = 1;
			
			for (int i = 0; i < iterations; i++) {
				long start = System.currentTimeMillis();
				String startString = df.format(new Date());
				Document query = new Document("$comment", "test query");
				Document result = (Document)coll.find(query).first();
				long stop = System.currentTimeMillis();
				long diff = (stop - start);
				String endString = df.format(new Date());
				System.out.println("start=" + start + ", stop=" + stop + ", diff=" + diff);
				System.out.println(" > start=" + startString + ", stop=" + endString);
				avg += diff;
			}
			avg = avg / iterations;
			
			System.out.println("Average time, " + avg);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

}
