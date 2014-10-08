package mind;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;

import externalinterfaces.RedmineApi;

public class RedmineApiImpl implements RedmineApi {
	Configuration config;
	public RedmineApiImpl(Configuration config)
	{
		this.config = config;
	}
	
	public List<Issue> getAllIssues() throws RedmineException {
		RedmineManager mgr = new RedmineManager(config.getString("redmine.host"), config.getString("redmine.apiaccesskey"));
		
		List<Issue> allIssues = mgr.getIssues(config.getString("redmine.projectkey"), null);
		mgr.shutdown();
		return allIssues;
	}

	public String getBugKey() throws RedmineException {
		return config.getString("redmine.bugkey");
	}
	
	public boolean isSpecialVersionIdentifierSet()
	{
		if (config.containsKey("redmine.versionIdentifier")) {
			return !config.getString("redmine.versionIdentifier").isEmpty();	
		}
		return false;
	}
	
	public String getSpecialVersionIdentifier()
	{
		return config.getString("redmine.versionIdentifier");
	}
	
	public List<String> getConfiguredVersions()
	{
		return Arrays.asList(config.getString("redmine.versiontags").split(";"));
	}
	
}
