package mind;

import interfaces.MindConfiguration;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import utils.ConfigAccessor;

public class MindConfigurationImpl implements MindConfiguration{
	Configuration config;
	public MindConfigurationImpl() throws ConfigurationException
	{
		config = new PropertiesConfiguration("mind.properties");
	}
	
	public String getSonarHost() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "sonar.host");
	}
	
	public String getSonarProject() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "sonar.project");
	}
	
	
	public String getGitName() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "git.name");
	}

	public String getGitPassword() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "git.password");
	}
	
	public String getGitUrl() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "git.url");
	}
	
	public String getGitWorkingDir() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "git.workingdir");
	}

	public String getRedmineBugKey() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "redmine.bugkey");
	}

	public String getRedmineHost() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "redmine.host");
	}
	
	public String getRedmineApiAccessKey() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "redmine.apiaccesskey");
	}
	
	public String getRedmineProjectKey() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "redmine.projectkey");
	}
	
	public List<String> getGitVersionTags() throws ConfigurationException
	{
		return Arrays.asList(config.getString("git.versiontags").split(";"));
	}

	public String getRedmineVersionIdentifier() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "redmine.versionIdentifier");
	}
	
	public boolean isRedmineVersionIdentifierSet() throws ConfigurationException
	{
		return config.containsKey("redmine.versionIdentifier");
	}
	
	public List<String> getRedmineVersionTags() throws ConfigurationException
	{
		return Arrays.asList(config.getString("redmine.versiontags").split(";"));
	}
	
	public List<String> getSonarVersionTags() throws ConfigurationException
	{
		return Arrays.asList(config.getString("sonar.versiontags").split(";"));
	}
	
	public List<String> getSonarRuleRepositories() throws ConfigurationException
	{
		return Arrays.asList(ConfigAccessor.getValue(config, "sonar.rulerepositories").split(";"));
	}

	public String getSonarQubeConfig() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "sonar.sonarQubeConfig");
	}
	
	public String getRscriptExecutablePath() throws ConfigurationException
	{
		return ConfigAccessor.getValue(config, "R.RscriptExecutablePath");
	}
}
