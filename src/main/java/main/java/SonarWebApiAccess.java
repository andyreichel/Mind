package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

public class SonarWebApiAccess {
	private String sonarHost; 
	
	public SonarWebApiAccess(Configuration config)
	{
		this.sonarHost = config.getString("sonar.host");
	}
	
	public List<String> getListOfAllRules() throws IOException
	{
		String json2 = sendGet(sonarHost + "/api/rules/search");
		JSONObject rulesObj = new JSONObject(json2);
		List<String> rulesList = new ArrayList<String>();
		JSONArray rules = (JSONArray) rulesObj.get("rules");
		for (int i = 0; i < rules.length(); i++) {
			rulesList.add(((JSONObject) (rules.get(i))).get("key").toString());
		}
		
		return rulesList;
	}
	
	private static String sendGet(String url) throws IOException {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		String USER_AGENT = "Mozilla/32.0";
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		return response.toString();
	}
}
