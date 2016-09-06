import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class FindSourceFiles {
	
	private static final int THREADS = 1;
	private static final int READ_COUNT = 100000000;
	private static final int PRINT_INTERVAL = 2500;
	
	private static List<ServerAddress> addresses = new ArrayList<ServerAddress>();
	private static List<MongoCredential> creds = new ArrayList<MongoCredential>();
	static {
		creds.add(MongoCredential.createCredential("mongoAdmin", "admin", "@dm!n".toCharArray()));
		addresses.add(new ServerAddress("mgd-us2s-rd-1a.dmz01.mrll.com", 27017));
		addresses.add(new ServerAddress("mgd-us2s-rd-1b.dmz01.mrll.com", 27017));
		addresses.add(new ServerAddress("mgd-us2s-rd-1c.dmz01.mrll.com", 27017));
	}
	
	private static List<ObjectId> ids = new ArrayList<ObjectId>();
	
	public static void main(String[] args) {
		
		Thread[] threads = new Thread[THREADS];
		
		try {
			
			// Build an array of sample _id fields for querying
			MongoClient client  = new MongoClient(addresses, creds);
			
			// Start threads and join on them
			long start = System.currentTimeMillis();
			int count = READ_COUNT / THREADS;
			
			for (int i = 0; i < THREADS; i++) {
				int index = i * count;
				threads[i] = new Thread(new Reader(index, count));
				threads[i].start();
			}
			for (int i = 0; i < THREADS; i++) {
				threads[i].join();
			}
			
			long end = System.currentTimeMillis();
			
			System.out.println("Read " + READ_COUNT + " in " + (end - start) + " miliseconds");
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	
	static class Reader implements Runnable {

		private final int start;
		private final int count;
		private int current;
		private MongoClient client;
		private MongoDatabase db;
		private MongoCollection coll;
		private Random random = new Random();
		
		public Reader(int start, int count) {
			this.start = start;
			this.count = count;
			
			client  = new MongoClient(addresses, creds);
			db = client.getDatabase("docMetadata");
			coll = db.getCollection("metadata");
		}
		
		public void run() {
			int count = 0;
			Iterator<Document> results = coll.find(new Document("index", new Document("$regex", "^2.1"))).iterator();
			while (results.hasNext()) {
				System.out.println(results.next());
				count += 1;
			}
			System.out.println(count);
			client.close();
		}
	}

}
