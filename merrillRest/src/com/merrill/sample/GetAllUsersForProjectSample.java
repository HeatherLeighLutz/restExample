package com.merrill.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.merrill.helper.HttpClient;
import com.merrill.model.UserDetails;

public class GetAllUsersForProjectSample {
	
	public static final int THREAD_COUNT = 400;

	public static void main(String[] args) {
		
		try {
			
			Thread[] threads = new Thread[THREAD_COUNT];
			for (int i = 0; i < THREAD_COUNT; i++) {
				threads[i] = new Thread(new TestExecutor());
				threads[i].start();
			}
			for (int i = 0; i < THREAD_COUNT; i++) {
				threads[i].join();
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

	static class TestExecutor implements Runnable {

		@Override
		public void run() {
			
			String response = "";
			try {
				
				//String jwt = JavelinHelper.getJWTToken();
				Map<String, List<JSONObject>> users = new HashMap<String, List<JSONObject>>();
				Map<String, List<String>> cags = new HashMap<String, List<String>>();
				List<String> projects = new ArrayList<String>();
				
				String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiYTAxYTk0MC0xMGQxLTRlMTUtODBiZS1iM2VjMzM3YmExZDQiLCJncm91cCI6ImNsaWVudCIsImV4cCI6MTUwMjkxMDc0MDg5NCwic2NvcGUiOltdLCJpc3MiOiJpYW0ubWVycmlsbGNvcnAuY29tIiwiYXVkIjoiamF2ZWxpbm1jLmNvbSIsIlVzZXJOYW1lIjoidGVzdHVzZXIzQG1haWxpbmF0b3IuY29tIn0.PDnf4E9bhPhd9pBTd4YAXWD41ab0ryrMU-yK4ie3zN4";
				String uri = "/api/dashboard/currentUser";
				response = HttpClient.sendGet(uri, jwt);

				ObjectMapper mapper = new ObjectMapper();
				UserDetails user = mapper.readValue(response, UserDetails.class);
				
				
				int projectsToFetch = 2;
				List<String> projectArray = user.projects;
				for (int k = 0; (k < projectsToFetch && k < projectArray.size()); k++) {
					
					String projectId = projectArray.get(k);
					// Build projects and Users
					//String projectId = project.getProjectId();
					if (projectId.equals("P1") || projectId.equals("P101"))
						continue;
					projects.add(projectId);
					String id = projectId;
					
					System.out.println("\ngetting users for project " + projectId);
					uri = "/api/projects/" + id + "/users/registered";
					response = HttpClient.sendGet(uri, jwt);
					JSONArray userArray = new JSONArray(response);
					for (int i = 0; i < userArray.length(); i++) {
						JSONObject userWrapper = userArray.getJSONObject(i);
						JSONObject userData = userWrapper.getJSONObject("user");
						List userList = users.get(projectId);
						if (userList == null) {
							userList = new ArrayList<JSONObject>();
							users.put(projectId, userList);
						}
						userList.add(userData);
					}
					
					
					// Build list of possible cags for project
					System.out.println("getting cags for project " + projectId);
					uri = "/api/projects/" + id + "/groups/cag";
					response = HttpClient.sendGet(uri, jwt);
					JSONArray cagArray = new JSONArray(response);
					for (int i = 0; i < cagArray.length(); i++) {
						JSONObject cagData = cagArray.getJSONObject(i);
						List<String> cagList = cags.get(projectId);
						if (cagList == null) {
							cagList = new ArrayList<String>();
							cags.put(projectId, cagList);
						}
						cagList.add(cagData.getString("id"));
					}
				}
				
				
				
				
				int iterations = 1000;
				int updateInterval = 2;
				int fetchInterval = 1;
				Random random = new Random();
				for (int i = 0; i < iterations; i++) {
					String projectId = projects.get(random.nextInt(projects.size()));
					
					List<JSONObject> userList = users.get(projectId);
					List<String> cagList = cags.get(projectId);
					
					for (int j = 0; j < userList.size(); j++) {
						JSONObject userData = userList.get(j);
						String userId = userData.getString("id");
						if (userId != null && !userId.isEmpty()) {
							// Fetch periodically
							if ((j % fetchInterval) == 0) {
								// Make fetch Rest call
								
								uri = "/api/projects/" + projectId + "/users/" + userId;
								response = HttpClient.sendGet(uri, jwt);
								System.out.println("Fetched user " + userId + ", " + response);
							}
							
							// Modify periodically
							if ((j % updateInterval) == 0) {
								String newCag = cagList.get(random.nextInt(cagList.size()));
								// Set new cag
								JSONObject postData = new JSONObject();
								JSONArray usersToChange = new JSONArray();
								usersToChange.put(userId);
								postData.put("userIds", usersToChange);
								
								String postBody = postData.toString();
								uri = "/api/projects/" + projectId + "/users/changeRole?contentAccessGroupId=" + newCag;
								response = HttpClient.sendPost(uri, postData.toString(), jwt);
								System.out.println("Modified user " + userId + ", " + response);
							}
						}
						
					}
				}
			
			
		} catch (Throwable e) {
			System.out.println("Response, " + response);
			e.printStackTrace();
		}
		
	}
	}
	
}
