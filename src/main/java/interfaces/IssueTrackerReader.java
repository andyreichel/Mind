package interfaces;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import com.taskadapter.redmineapi.RedmineException;

public interface IssueTrackerReader {
	public HashMap<Integer, String> getMapOfBugsRelatedToTheirVersion() throws RedmineException, ConfigurationException;
	public List<String> getConfiguredVersions() throws ConfigurationException;
}
