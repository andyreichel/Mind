package mind;

import interfaces.MindConfiguration;
import interfaces.SonarWebApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.JsonParserForSonarApiResponses;

import com.google.inject.Inject;

public class SonarWebApiImpl implements SonarWebApi {
	private String sonarHost; 
	private String project;
	private List<String> configuredVersions;
	private List<String> configuredRepos;
	
	@Inject
	public SonarWebApiImpl(MindConfiguration config) throws ConfigurationException
	{
		this.sonarHost = config.getSonarHost();
		this.project = config.getSonarProject();
		this.configuredVersions = config.getSonarVersionTags();
		this.configuredRepos = config.getSonarRuleRepositories();
	}
	
	public List<String> getListOfAllRules() throws IOException
	{
		List<String> rulesList = new ArrayList<String>();
		for(String repo : configuredRepos)
		{
			String json2 = sendGet(sonarHost + "/api/rules/search?repositories=" + repo);
			JSONObject rulesObj = new JSONObject(json2);
			
			JSONArray rules = (JSONArray) rulesObj.get("rules");
			for (int i = 0; i < rules.length(); i++) {
				rulesList.add(((JSONObject) (rules.get(i))).get("key").toString());
			}	
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

	public int getNumberOfViolationsOfSpecificRuleForResource(String resourceKey, String rule) throws IOException {
		String numberOfViolationsJSON = sendGet(sonarHost+"/api/issues/search?componentRoots=" + resourceKey + "&rules="+rule);
		return JsonParserForSonarApiResponses.getNumberOfViolationsOfSpecificRuleForResource(numberOfViolationsJSON);
	}

	public String getDateOfLastSonarAnalyse(String version) throws IOException {
		String versionsJSON = sendGet(sonarHost + "/api/events?resource=" + project + "&categories=Version");
		return JsonParserForSonarApiResponses.getDateOfLastSonarAnalyse(version, versionsJSON);
	}

	public int getSizeOfResource(String resourceKey) throws IOException {
		String sizeJson = sendGet(sonarHost + "/api/resources?resource=" + resourceKey + ";metrics=ncloc");
		return JsonParserForSonarApiResponses.getNloc(sizeJson);
	}

	private static String sendGet(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		String USER_AGENT = "Mozilla/32.0";
		con.setRequestProperty("User-Agent", USER_AGENT);

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
