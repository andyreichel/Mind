package mind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;

public class RedmineReader implements IssueTrackerReader {
	
	RedmineApi api;
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
					mapOfBugsRelatedToTheirVersion.put(issue.getId(), issue.getTargetVersion().getName());
		}
		return mapOfBugsRelatedToTheirVersion;
	}
}
