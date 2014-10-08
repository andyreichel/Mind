package interfaces;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;


public interface MindConfiguration {
	public String getSonarHost() throws ConfigurationException;
	public String getSonarProject() throws ConfigurationException;
	public String getGitName() throws ConfigurationException;
	public String getGitPassword() throws ConfigurationException;
	public String getGitUrl() throws ConfigurationException;
	public String getGitWorkingDir() throws ConfigurationException;
	public String getRedmineBugKey() throws ConfigurationException;
	public String getRedmineHost() throws ConfigurationException;
	public String getRedmineApiAccessKey() throws ConfigurationException;
	public String getRedmineProjectKey() throws ConfigurationException;
	public List<String> getGitVersionTags() throws ConfigurationException;
	public String getRedmineVersionIdentifier() throws ConfigurationException;
	public boolean isRedmineVersionIdentifierSet() throws ConfigurationException;
	public List<String> getRedmineVersionTags() throws ConfigurationException;
	public List<String> getSonarVersionTags() throws ConfigurationException;
	public List<String> getSonarRuleRepositories() throws ConfigurationException;
	public String getSonarQubeConfig() throws ConfigurationException;
	public String getRscriptExecutablePath() throws ConfigurationException;
}
