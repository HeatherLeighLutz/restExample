import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class PersonPopulator {
	
	private static final int THREADS = 40;
	private static final int RECORD_COUNT = 10000;
	private static final boolean BULK = true;
	private static final int BULK_CHUNK = 500;
	private static final int PRINT_INTERVAL = 2500;
	
	private static String[] firstNames = new String[] {"ben", "dave", "bob", "todd", "jim", "sam", "donny", "ted", "joe", "derrick", "alex"};
	private static String[] lastNames = new String[] {"sandmann", "anderson", "smith"};
	private static String[] streets = new String[] {"Mass Ave", "Downing St.", "Campbell Ave", "Pennsylvania Ave", "Hartwell Ave", "Clark St.", "State St."};
	private static String[] cities = new String[] {"Chicago", "Dallas", "Austin", "Boston", "Portland", "Morgan", "Clarksville", "Forth Worth", "San Diego"};
	private static String[] states = new String[] {"MN", "IL", "TX", "AZ", "NY", "FL", "CA", "WA", "NM"};
	
	private static List<ServerAddress> addresses = new ArrayList<ServerAddress>();
	
	private static List<MongoCredential> creds = new ArrayList<MongoCredential>();
	static {
		creds.add(MongoCredential.createCredential("mongoAdmin", "admin", "@dm!n".toCharArray()));
		addresses.add(new ServerAddress("mgd-us2s-gd-1a.dmz01.mrll.com", 27017));
		addresses.add(new ServerAddress("mgd-us2s-gd-1b.dmz01.mrll.com", 27017));
		addresses.add(new ServerAddress("mgd-us2s-gd-1c.dmz01.mrll.com", 27017));
	}
	
	public static void main(String[] args) {
		
		Thread[] threads = new Thread[THREADS];
		
		try {
			
			long start = System.currentTimeMillis();
			
			int count = RECORD_COUNT / THREADS;
			
			for (int i = 0; i < THREADS; i++) {
				int index = i * count;
				threads[i] = new Thread(new Populator(index, count));
				threads[i].start();
			}
			for (int i = 0; i < THREADS; i++) {
				threads[i].join();
			}
			
			long end = System.currentTimeMillis();
			
			System.out.println("Populated " + RECORD_COUNT + " in " + (end - start) + " miliseconds");
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	
	static class Populator implements Runnable {

		private final int start;
		private final int count;
		private int current;
		private MongoClient client;
		private MongoDatabase db;
		private MongoCollection coll;
		private Random random = new Random();
		
		public Populator(int start, int count) {
			this.start = start;
			this.count = count;
			
			client  = new MongoClient(addresses, creds);		
			db = client.getDatabase("store");		
			coll = db.getCollection("persons");
		}
		
		public void run() {
			
			List<Document> docs = new ArrayList<Document>();
			int counter = 0;
			
			for (int i = 0 ; i < count; i ++) {
				
				Document person = new Document();
				String firstName = firstNames[random.nextInt(firstNames.length)];
				String lastName = lastNames[random.nextInt(lastNames.length)];
				
				int addressCount = random.nextInt(4);
				int age = random.nextInt(20000);
				
				person.put("firstName", firstName);
				person.put("lastName", lastName);
				person.put("age", age);

				List<Document> addresses = new ArrayList<Document>();
				for (int j = 0; j < addressCount; j++) {
					Document address = new Document();
					String city = cities[random.nextInt(cities.length)];
					String state = states[random.nextInt(states.length)];
					String street = streets[random.nextInt(streets.length)];
					address.put("city", city);
					address.put("state", state);
					address.put("street", street);
					address.put("houseNumber", random.nextInt(2000));
					addresses.add(address);
				}
				person.put("addresses", addresses);
				
				// Check if bulk insert or individual
				if (!BULK) {
					coll.insertOne(person);
				}
				else {
					docs.add(person);
					if ((i % BULK_CHUNK) == 0) {
						coll.insertMany(docs);
						docs = new ArrayList<Document>();
					}
				}
				
				// Print status?
				counter += 1;
				if ((counter % PRINT_INTERVAL) == 0) {
					System.out.println(Thread.currentThread().getName() + " inserted " + i);
				}
				
			}
			if (!docs.isEmpty())
				coll.insertMany(docs);
			client.close();
		}
	}

}
