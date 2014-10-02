package mind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;

public class RedmineReader implements IssueTrackerReader {
	private static org.apache.log4j.Logger log = Logger.getLogger(RedmineReader.class);
	private RedmineApi api;
	RedmineReader(RedmineApi api)
	{
		this.api = api;
	}
	
	public HashMap<Integer, String> getMapOfBugsRelatedToTheirVersion() throws RedmineException
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
	
	
	public List<String> getConfiguredVersions()
	{
		return api.getConfiguredVersions();
	}
}
