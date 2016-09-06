package com.merrill.sample;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.merrill.helper.HttpClient;
import com.merrill.helper.JavelinHelper;

public class MoveSingleFile {

	public static void main(String[] args) {
		
		try {
		
			// 1.19 - 57c5f7c9f136fed7993f3158
			// 1.20 - 57c5f7d260d2d90581aa5cfd
			// 1.20.1 - 57c5fa4a5a804757e85dd469
			
			
			
			String sourceDoc = "57c5fa4a5a804757e85dd469";
			
			String destIndex = "0";
			String destId = "57c5f7c9f136fed7993f3158";  // 1.19
			//String destId = "57c5f7d260d2d90581aa5cfd";    // 1.20
			
			String jwt = JavelinHelper.getJWTToken();
			String uri = "/api/projects/project1/metadata/move";
			JSONObject moveParams = new JSONObject();
			JSONArray array = new JSONArray();
			array.put(sourceDoc);
			moveParams.put("destinationIndex", destIndex);
			moveParams.put("destinationMetadataId", destId);
			moveParams.put("sourceMetadataIds", array);
			String response = HttpClient.sendPut(uri, moveParams.toString(), jwt);
			System.out.println(response.toString());
			
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
}
