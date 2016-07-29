package com.merrill.helper;
import org.codehaus.jettison.json.JSONObject;

import com.merrill.Constants;

public class JavelinHelper {
	
	public static String getJWTToken() throws Exception {
		
		String uri = "/api/tokens/login";
		
		// Body data
		JSONObject data = new JSONObject();
		data.put("username", Constants.USERNAME);
		data.put("password", Constants.PASSWORD);
		
		// Get Token
		String response = HttpClient.sendPost(uri, data.toString(), null);
		
		// Parse Response Body
		JSONObject tokenData = new JSONObject(response);
		return tokenData.getString("jwt");
	}
	

}
