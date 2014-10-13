package interfaces;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import com.taskadapter.redmineapi.RedmineException;

/**
 * 
 * Classes that implement this interface enable reading access to a issue tracker like redmine or jira. 
 * 
 *
 */
public interface IssueTrackerReader {
	/**
	 * 
	 * @return a map of bugs related to a version for example
	 * {"10000" : "version1"} the key is the bug id that should be contained in one of the git revisions comments and the value is the version tag used in issue tracker
	 * @throws RedmineException
	 * @throws ConfigurationException
	 */
	public HashMap<Integer, String> getMapOfBugsRelatedToTheirVersion() throws RedmineException, ConfigurationException;
	
	/**
	 * 
	 * @return a list of versions that are configured in the mind configuration for the issue tracker
	 * @throws ConfigurationException
	 */
	public List<String> getConfiguredVersions() throws ConfigurationException;
}
