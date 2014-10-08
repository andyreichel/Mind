package interfaces;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;

public interface RedmineApi {
	List<Issue> getAllIssues() throws RedmineException, ConfigurationException;
	String getBugKey() throws RedmineException, ConfigurationException;
	List<String> getConfiguredVersions() throws ConfigurationException;
	String getSpecialVersionIdentifier() throws ConfigurationException;
	boolean isSpecialVersionIdentifierSet();
}
