package mind;

import interfaces.MindConfiguration;
import interfaces.RedmineApi;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import com.google.inject.Inject;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;

public class RedmineApiImpl implements RedmineApi {
	MindConfiguration config;
	
	@Inject
	public RedmineApiImpl(MindConfiguration config)
	{
		this.config = config;
	}
	
	public List<Issue> getAllIssues() throws RedmineException, ConfigurationException {
		RedmineManager mgr = new RedmineManager(config.getRedmineHost(), config.getRedmineApiAccessKey());
		
		List<Issue> allIssues = mgr.getIssues(config.getRedmineProjectKey(), null);
		mgr.shutdown();
		return allIssues;
	}

	public String getBugKey() throws RedmineException, ConfigurationException {
		return config.getRedmineBugKey();
	}
	
	public boolean isSpecialVersionIdentifierSet()
	{
		try
		{
			if (config.isRedmineVersionIdentifierSet()) {
				return !config.getRedmineVersionIdentifier().isEmpty();	
			}			
		}catch(ConfigurationException ce)
		{
			return false;
		}

		return false;
	}
	
	public String getSpecialVersionIdentifier() throws ConfigurationException
	{
		return config.getRedmineVersionIdentifier();
	}
	
	public List<String> getConfiguredVersions() throws ConfigurationException
	{
		return config.getRedmineVersionTags();
	}
	
}
