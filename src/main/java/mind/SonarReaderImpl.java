package mind;
//1.0
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;


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
