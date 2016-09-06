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


public class MoveServeralFilesBetter {

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
			
			// Modify
			String source = "1.2.3";
			String dest = "1.2.2.3";
			
			// Leave alone
			List<String> sourceIds = new ArrayList<String>();
			int count = 0;
			Iterator<Document> results = coll.find(new Document("parentId", source)).iterator();
			while (results.hasNext()) {
				Document record = results.next();
				String id = record.getObjectId("_id").toString();
				System.out.println("Going to move index " + record.getString("index") + ", " + id);
				sourceIds.add(id);
			}
			
			String destId = "";
			results = coll.find(new Document("index", dest)).limit(1).iterator();
			Document record = results.next();
			destId = record.getObjectId("_id").toString();
			
			
			System.out.println("Moving children of " + source + " to " + dest);
			String jwt = JavelinHelper.getJWTToken();
			String uri = "/api/projects/project1/metadata/move";
			JSONObject moveParams = new JSONObject();
			JSONArray array = new JSONArray();
			for (String id : sourceIds) {
				array.put(id);
			}
			moveParams.put("destinationIndex", "0");
			moveParams.put("destinationMetadataId", destId);
			moveParams.put("sourceMetadataIds", array);
			String response = HttpClient.sendPut(uri, moveParams.toString(), jwt);
			System.out.println(response.toString());
			
			client.close();
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
}
