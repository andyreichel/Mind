package interfaces;

import java.util.HashMap;
import java.util.List;

import com.taskadapter.redmineapi.RedmineException;

public interface IssueTrackerReader {
	public HashMap<Integer, String> getMapOfBugsRelatedToTheirVersion() throws RedmineException; //FIXME: MAKE NESTED EXCEPTION;
	public List<String> getConfiguredVersions();
}
