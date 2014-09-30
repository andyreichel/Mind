package mind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

public class SonarWebApiImpl implements SonarWebApi {
	private String sonarHost; 
	private String project;
	private List<String> configuredVersions;
	
	public SonarWebApiImpl(Configuration config)
	{
		this.sonarHost = config.getString("sonar.host");
		this.project = config.getString("sonar.project");
		this.configuredVersions = Arrays.asList(config.getString("sonar.versiontags").split(";"));
	}
	
	public List<String> getListOfAllRules() throws IOException
	{
		//FIXME:ONLYWORKS WITH JAVA SQUID RULE REPOSITORY
		String json2 = sendGet(sonarHost + "/api/rules/search?repositories=squid");
		JSONObject rulesObj = new JSONObject(json2);
		List<String> rulesList = new ArrayList<String>();
		JSONArray rules = (JSONArray) rulesObj.get("rules");
		for (int i = 0; i < rules.length(); i++) {
			rulesList.add(((JSONObject) (rules.get(i))).get("key").toString());
		}
		
		return rulesList;
	}
	

	public List<String> getListOfAllResources() throws IOException {
		String resourcesJSON = sendGet(sonarHost + "/api/resources?resource=" + project + ";depth=-1;scopes=FIL");
		JSONArray resourcesArray = new JSONArray(resourcesJSON.substring(0, resourcesJSON.length()));
		List<String> resourcesList = new ArrayList<String>();
		for(int i = 0; i < resourcesArray.length(); i++)
		{
			resourcesList.add(((JSONObject) (resourcesArray.get(i))).get("key").toString());
		}
		return resourcesList;
	}

	public int getNumberOfViolationsOfSpecificRuleForResource(String versionDate, String resourceKey, String rule) throws IOException {
		String numberOfViolationsJSON = sendGet(sonarHost + "/api/timemachine?resource=" + project + "&metrics=violations&resource="  + resourceKey + "&fromDateTime=" + versionDate + "&toDateTime=" + versionDate);
		return JsonParserForSonarApiResponses.getNumberOfViolationsOfSpecificRuleForResource(numberOfViolationsJSON);
	}

	public String getDateOfLastSonarAnalyse(String version) throws IOException {
		String versionsJSON = sendGet(sonarHost + "/api/events?resource=" + project + "&categories=Version");
		return JsonParserForSonarApiResponses.getDateOfLastSonarAnalyse(version, versionsJSON);
	}

	public int getSizeOfResource(String resourceKey, String versionDate) throws IOException {
		String versionsJSON = sendGet(sonarHost + "/api/timemachine?metrics=ncloc&fromDateTime=" + versionDate + "&toDateTime=" + versionDate + "&resource=" + resourceKey);
		return JsonParserForSonarApiResponses.getNloc(versionsJSON);
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

		return response.toString();
	}
	
	public List<String> getConfiguredVersions()
	{
		return configuredVersions;
	}
}
