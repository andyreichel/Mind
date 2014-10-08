package mind;

import interfaces.IssueTrackerReader;
import interfaces.RedmineApi;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import com.google.inject.Inject;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;

public class RedmineReader implements IssueTrackerReader {
	private RedmineApi api;
	
	@Inject
	RedmineReader(RedmineApi api)
	{
		this.api = api;
	}
	
	public HashMap<Integer, String> getMapOfBugsRelatedToTheirVersion() throws RedmineException, ConfigurationException
	{
		HashMap<Integer, String> mapOfBugsRelatedToTheirVersion = new HashMap<Integer, String>();
		List<Issue> listOfAllIssues = api.getAllIssues();
		for(Issue issue : listOfAllIssues)
		{
			if(api.getBugKey().equals(issue.getTracker().getName()))
			{
				if(api.isSpecialVersionIdentifierSet())
				{
					String specialVersionIdentifier = api.getSpecialVersionIdentifier();
					if(specialVersionIdentifier != null)
					{
						mapOfBugsRelatedToTheirVersion.put(issue.getId(), issue.getCustomField(specialVersionIdentifier));
						
					}
				}else if(issue.getTargetVersion() != null)
				{
					mapOfBugsRelatedToTheirVersion.put(issue.getId(), issue.getTargetVersion().getName());
				}
			}
		}
		return mapOfBugsRelatedToTheirVersion;
	}
	
	
	public List<String> getConfiguredVersions() throws ConfigurationException
	{
		return api.getConfiguredVersions();
	}
}
