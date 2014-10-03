package mind;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONException;


public class SonarReaderImpl implements SonarReader {
	private SonarWebApi api;
	public SonarReaderImpl(SonarWebApi api)
	{
		this.api = api;
	}
	
	public HashMap<String, Integer> getNumberOfViolationsPerRule(String resourceKey) throws IOException {
		//FIXME: PULL OUT AND JUST MAKE IT ONE TIME 
		List<String> allRules = api.getListOfAllRules();
		HashMap<String, Integer> numberOfViolationsPerRule = new HashMap<String, Integer>();
		
		for(String rule : allRules)
		{
			try
			{
				numberOfViolationsPerRule.put(rule, api.getNumberOfViolationsOfSpecificRuleForResource(resourceKey, rule));
			}catch(JSONException e)
			{
				//FIXME: DO SOMETHING
				//System.out.println(e.getMessage());
				//System.out.println("Version: " + version + " ResourceKey: " + resourceKey + " Rule: " + rule + " not found");
			}
		}
		
		return numberOfViolationsPerRule;
	}
	
	public HashMap<String, Integer> getNumberOfViolationsPerRuleEverythingZero() throws IOException
	{
		List<String> allRules = api.getListOfAllRules();
		HashMap<String, Integer> numberOfViolationsPerRule = new HashMap<String, Integer>();
		
		for(String rule : allRules)
		{
			numberOfViolationsPerRule.put(rule, 0);
		}
		
		return numberOfViolationsPerRule;
	}

	public int getSizeOfClass(String resourceKey) throws IOException {
		try
		{
			return api.getSizeOfResource(resourceKey);
		}catch(JSONException e)
		{
			//System.out.println(e.getMessage());
			//System.out.println("Version: " + versionDate + " ResourceKey: " + resourceKey + " not found");
			return 0;
		}
		
	}

	public List<String> getListOfAllResources() throws IOException {
		return api.getListOfAllResources();
	}

	public List<String> getConfiguredVersions() {
		return api.getConfiguredVersions();
	}
}
