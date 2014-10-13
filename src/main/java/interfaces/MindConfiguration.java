package interfaces;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

/**
 * 
 * Classes that implement this interface allow access to the basic configuration of this application.
 *
 */
public interface MindConfiguration {
	/**
	 * 
	 * @return the sonar host address
	 * @throws ConfigurationException
	 */
	public String getSonarHost() throws ConfigurationException;
	/**
	 * 
	 * @return project name in sonar
	 * @throws ConfigurationException
	 */
	public String getSonarProject() throws ConfigurationException;
	/**
	 * 
	 * @return git name for authentification
	 * @throws ConfigurationException
	 */
	public String getGitName() throws ConfigurationException;
	/**
	 * 
	 * @return git passowrd for authentification
	 * @throws ConfigurationException
	 */
	public String getGitPassword() throws ConfigurationException;
	
	/**
	 * 
	 * @return git url to access the repository
	 * @throws ConfigurationException
	 */
	public String getGitUrl() throws ConfigurationException;
	
	/**
	 * 
	 * @return git working dir where the branches are checked out
	 * @throws ConfigurationException
	 */
	public String getGitWorkingDir() throws ConfigurationException;
	
	/**
	 * 
	 * @return the string key that is used in redmine to specifiy a bug
	 * @throws ConfigurationException
	 */
	public String getRedmineBugKey() throws ConfigurationException;
	
	/**
	 * 
	 * @return the url of the redmine host
	 * @throws ConfigurationException
	 */
	public String getRedmineHost() throws ConfigurationException;
	
	/**
	 * 
	 * @return api access key that is stored in your account in redmine. This is needed for authentification
	 * @throws ConfigurationException
	 */
	public String getRedmineApiAccessKey() throws ConfigurationException;
	
	/**
	 * 
	 * @return the project key that is used in redmine
	 * @throws ConfigurationException
	 */
	public String getRedmineProjectKey() throws ConfigurationException;
	
	/**
	 * 
	 * @return semicolon separated list of version tags for example: 1.0;1.1
	 * @throws ConfigurationException
	 */
	public List<String> getGitVersionTags() throws ConfigurationException;
	
	/**
	 * 
	 * @return the string columnname that is used in redmine to store the version
	 * @throws ConfigurationException
	 */
	public String getRedmineVersionIdentifier() throws ConfigurationException;
	
	/**
	 * 
	 * @return true if a specific redmine version identifier is set. Otherwise use a standard column
	 * @throws ConfigurationException
	 */
	public boolean isRedmineVersionIdentifierSet() throws ConfigurationException;
	
	/**
	 * 
	 * @return semicolon separated list of version tags for example: 1.0;1.1
	 * @throws ConfigurationException
	 */
	public List<String> getRedmineVersionTags() throws ConfigurationException;
	
	/**
	 * 
	 * @return semicolon separated list of version tags for example: 1.0;1.1
	 * @throws ConfigurationException
	 */
	public List<String> getSonarVersionTags() throws ConfigurationException;
	/**
	 * 
	 * @return specifies the rule repositories that will be used during the sonar run
	 * @throws ConfigurationException
	 */
	public List<String> getSonarRuleRepositories() throws ConfigurationException;
	/**
	 * 
	 * @return path to the sonarqube config file
	 * @throws ConfigurationException
	 */
	public String getSonarQubeConfig() throws ConfigurationException;
	
	/**
	 * 
	 * @return path to the RScript.exe file. This is used to execute R for statistical purposes
	 * @throws ConfigurationException
	 */
	public String getRscriptExecutablePath() throws ConfigurationException;
}
