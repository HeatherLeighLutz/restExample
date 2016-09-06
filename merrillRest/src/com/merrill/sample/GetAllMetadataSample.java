package com.merrill.sample;

import org.codehaus.jackson.map.ObjectMapper;

import com.merrill.helper.HttpClient;
import com.merrill.helper.JavelinHelper;
import com.merrill.model.UserDetails;

public class GetAllMetadataSample {

	public static void main(String[] args) {
		
		try {
		
			String jwt = JavelinHelper.getJWTToken();
			String uri = "/api/dashboard/currentUser";
			String response = HttpClient.sendGet(uri, jwt);

			ObjectMapper mapper = new ObjectMapper();
			UserDetails user = mapper.readValue(response, UserDetails.class);
			
			for (String id : user.projects) {
				System.out.println(id);
				uri = "/api/projects/" + id + "/metadata/all";
				String params = "?level=5";
				response = HttpClient.sendGet(uri + params, jwt);
				System.out.println(response);
			}
			
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
}
