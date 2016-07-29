package com.merrill.helper;
import javax.naming.AuthenticationException;

import com.merrill.Constants;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class HttpClient {
	
	public static Client client = Client.create();
	
	static enum HttpMethod {
		GET, POST, PUT;
	}
	
	public static String sendPost(String url, String message, String jwt) throws Exception {
		return sendOperation(HttpMethod.POST, url, jwt, message);
	}
	
	public static String sendPut(String url, String message, String jwt) throws Exception {
		return sendOperation(HttpMethod.PUT, url, jwt, message);
	}
	
	public static String sendGet(String url, String jwt) throws Exception {
		return sendOperation(HttpMethod.GET, url, jwt, null);
	}
	
	public static String sendOperation(HttpMethod method, String url, String jwt, String body) throws Exception {
		
	    WebResource webResource = client.resource(Constants.BASE_URL + url);
	    WebResource.Builder builder = webResource.type("application/json").accept("application/json");
	    if (jwt != null) {
	    	builder.header("Authorization", "Bearer " + jwt);
	    }
	    ClientResponse response = null;
	    
	    switch (method) {
	    	case PUT:
	    		response = builder.put(ClientResponse.class, body.toString());
	    		break;
	    	case POST:
	    		response = builder.post(ClientResponse.class, body.toString());
	    		break;
	    	case GET:
	    		response = builder.get(ClientResponse.class);
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unaccepted HTTP Method");
	    }
	     
	    int statusCode = response.getStatus();
	    if (statusCode == 401) {
	        throw new AuthenticationException("Invalid Username or Password");
	    }
	    return response.getEntity(String.class);
	}
	
	

}
