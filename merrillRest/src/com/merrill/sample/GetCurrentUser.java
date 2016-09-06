package com.merrill.sample;
import org.codehaus.jackson.map.ObjectMapper;

import com.merrill.helper.HttpClient;
import com.merrill.helper.JavelinHelper;
import com.merrill.model.UserDetails;

public class GetCurrentUser {
	
	
	public static void main(String[] args) {
		
		try {
		
			String jwt = JavelinHelper.getJWTToken();					
			String uri = "/api/dashboard/currentUser";
			String response = HttpClient.sendGet(uri, jwt);
			System.out.println(response);

			ObjectMapper mapper = new ObjectMapper();
			UserDetails user = mapper.readValue(response, UserDetails.class);
			System.out.println(user);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

}
