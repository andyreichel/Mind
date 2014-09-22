package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class SonarReaderImpl implements SonarReader {
	private SonarWebApi api;
	public SonarReaderImpl(SonarWebApi api)
	{
		this.api = api;
	}
	
	public HashMap<String, Integer> getNumberOfViolationsPerRule(String version, String resourceKey) throws IOException {
		List<String> allRules = api.getListOfAllRules();
		HashMap<String, Integer> numberOfViolationsPerRule = new HashMap<String, Integer>();
		
		for(String rule : allRules)
		{
			numberOfViolationsPerRule.put(rule, api.getNumberOfViolationsOfSpecificRuleForResource(version, resourceKey, rule));
		}
		
		return numberOfViolationsPerRule;
	}

	public int getSizeOfClass(String versionDate, String resourceKey) throws IOException {
		return api.getSizeOfResource(resourceKey, versionDate);
	}

}
