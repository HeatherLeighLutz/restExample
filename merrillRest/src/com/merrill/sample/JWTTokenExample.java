package com.merrill.sample;
import org.codehaus.jettison.json.JSONObject;

import com.merrill.helper.HttpClient;

public class JWTTokenExample {
	
	
	public static void main(String[] args) {
		
		try {
		
			// URL data
			String uri = "/api/tokens/login";
			
			// Body data
			JSONObject data = new JSONObject();
			data.put("username", "test@merrillcorp.com");
			data.put("password", "Collaboration!");
			
			// Get Token
			String response = HttpClient.sendPost(uri, data.toString(), null);
			System.out.println(response);
			
			// Parse Response Body
			JSONObject tokenData = new JSONObject(response);
			String jwt = tokenData.getString("jwt");
			System.out.println(jwt);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

}
