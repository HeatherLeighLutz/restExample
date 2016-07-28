import org.codehaus.jettison.json.JSONObject;

public class JWTTokenExample {
	
	
	public static void main(String[] args) {
		
		try {
		
			// URL data
			String base = "http://javelin-api-gateway.apps.javelinmc.com";
			String function = "/api/tokens/login";
			String params = "";
			
			// Body data
			JSONObject data = new JSONObject();
			data.put("username", "test@merrillcorp.com");
			data.put("password", "Collaboration!");
			
			// Get Token
			String url = base + function + params;
			String response = HttpClient.sendPost(url, data.toString(), null);
			System.out.println(response);
			
			JSONObject tokenData = new JSONObject(response);
			String jwt = tokenData.getString("jwt");
			
			// Send get w/ jwt
			function = "/api/dashboard/currentUser";
			url = base + function + params;
			response = HttpClient.sendGet(url, jwt);
			System.out.println(response);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

}
