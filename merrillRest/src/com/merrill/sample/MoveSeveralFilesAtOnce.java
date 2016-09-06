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


public class MoveSeveralFilesAtOnce {

	private static List<ServerAddress> addresses = new ArrayList<ServerAddress>();
	private static List<MongoCredential> creds = new ArrayList<MongoCredential>();
	static {
		creds.add(MongoCredential.createCredential("mongoAdmin", "admin", "@dm!n".toCharArray()));
		addresses.add(new ServerAddress("mgd-us2s-rd-1a.dmz01.mrll.com", 27017));
		addresses.add(new ServerAddress("mgd-us2s-rd-1b.dmz01.mrll.com", 27017));
		addresses.add(new ServerAddress("mgd-us2s-rd-1c.dmz01.mrll.com", 27017));
	}
	
	
	
	
	
	
	static String[] files = new String[] {"57c728d01a613fc2d1846c46", "57c728d01a613fc2d1846c47", "57c728d01a613fc2d1846c48", "57c728d01a613fc2d1846c6a", "57c728d01a613fc2d1846ca9", "57c728d01a613fc2d1846c84"};
	
	public static void main(String[] args) {
		
		try {
		
			MongoClient client  = new MongoClient(addresses, creds);
			MongoDatabase db = client.getDatabase("docMetadata");
			MongoCollection coll = db.getCollection("metadata");
			
			/*
			 *  
			 *  57c72c0ac73c2159dc9fbc0a - 2.4 - test
				57c728d01a613fc2d1846c3c - 2.1 - Reports
				57c728d01a613fc2d1846c49 - 2.2 - Planning
				57c728d01a613fc2d1846c45 - 2.1.3
			 */
			
			String parent = "57c728d01a613fc2d1846c45";
			
			String source = "^2.1";
			
			///////////
			//String des = "57c728d01a613fc2d1846c45";
			String des = "57c72c0ac73c2159dc9fbc0a";
			///////////
			
			List<String> sourceIds = new ArrayList<String>();
			int count = 0;
			//Iterator<Document> results = coll.find(new Document("index", new Document("$regex", source))).iterator();
			Iterator<Document> results = coll.find(new Document("parentId", "2.1.3")).iterator();
			while (results.hasNext()) {
				Document record = results.next();
				String id = record.getObjectId("_id").toString();
				System.out.println(id);
				sourceIds.add(id);
			}
			System.out.println(count);
			client.close();
			
			System.out.println("Moving specified files to " + des);
			String jwt = JavelinHelper.getJWTToken();
			String uri = "/api/projects/project1/metadata/move";
			JSONObject moveParams = new JSONObject();
			JSONArray array = new JSONArray();
			for (String id : files) {
				array.put(id);
			}
			moveParams.put("destinationIndex", "0");
			moveParams.put("destinationMetadataId", des);
			moveParams.put("sourceMetadataIds", array);
			String response = HttpClient.sendPut(uri, moveParams.toString(), jwt);
			System.out.println(response.toString());
			
			
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
}
