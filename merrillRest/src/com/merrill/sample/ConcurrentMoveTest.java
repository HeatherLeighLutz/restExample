package com.merrill.sample;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.merrill.helper.HttpClient;
import com.merrill.helper.JavelinHelper;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class ConcurrentMoveTest {

	private static List<ServerAddress> addresses = new ArrayList<ServerAddress>();
	private static List<MongoCredential> creds = new ArrayList<MongoCredential>();
	static {
		creds.add(MongoCredential.createCredential("mongoAdmin", "admin", "@dm!n".toCharArray()));
		addresses.add(new ServerAddress("mgd-us2s-rd-1a.dmz01.mrll.com", 27017));
		addresses.add(new ServerAddress("mgd-us2s-rd-1b.dmz01.mrll.com", 27017));
		addresses.add(new ServerAddress("mgd-us2s-rd-1c.dmz01.mrll.com", 27017));
	}
	
	
	
	public static void main(String[] args) {
		
		try {
		
			MongoClient client  = new MongoClient(addresses, creds);
			MongoDatabase db = client.getDatabase("docMetadata");
			MongoCollection coll = db.getCollection("metadata");
			
			// User should set these
			String[] destinations = new String[] {"1.2.2.2", "1.2.2.3"};
			String sourceFolder = "1.2.3";
			
			int numThreads = 2;
			
			// User should not set these
			String destId = "";
			JSONArray sourceFiles = new JSONArray();
			String[] destIds = new String[destinations.length];
			
			for (int i = 0; i < destinations.length; i++) {
				System.out.println(destinations[i]);
				Iterator<Document> results = coll.find(new Document("index", destinations[i])).limit(1).iterator();
				Document record = results.next();
				String id = record.getObjectId("_id").toString();
				destIds[i] = id;
			}
			
			Iterator<Document> results = coll.find(new Document("parentId", sourceFolder)).iterator();
			while (results.hasNext()) {
				Document record = results.next();
				String id = record.getObjectId("_id").toString();
				System.out.println(id);
				sourceFiles.put(id);
			}
			
			Thread[] threads = new Thread[numThreads];
			for (int i =0; i < threads.length; i ++) {
				int index = i % destIds.length;
				threads[i] = new Thread(new MoveThread(sourceFiles, destIds[index]));
				threads[i].start();
			}
			for (int i =0; i < threads.length; i ++) {
				threads[i].join();
			}

			
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	static class MoveThread implements Runnable {
		
		final JSONArray sourceFiles;
		final String destFolder;
		
		public MoveThread(JSONArray sourceFiles, String destFolder) {
			this.sourceFiles = sourceFiles;
			this.destFolder = destFolder;
		}

		@Override
		public void run() {
			try {
				System.out.println("Moving specified files to " + destFolder);
				String jwt = JavelinHelper.getJWTToken();
				String uri = "/api/projects/project1/metadata/move";
				JSONObject moveParams = new JSONObject();
				moveParams.put("destinationIndex", "0");
				moveParams.put("destinationMetadataId", destFolder);
				moveParams.put("sourceMetadataIds", sourceFiles);
				String response = HttpClient.sendPut(uri, moveParams.toString(), jwt);
				System.out.println(response.toString());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
