package mind;
import interfaces.MindConfiguration;
import interfaces.SonarRunnerApi;

import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.sonar.runner.api.ForkedRunner;
import org.sonar.runner.api.Runner;

import com.google.inject.Inject;

public class SonarRunnerApiImpl implements SonarRunnerApi {
	MindConfiguration config;
	
	@Inject
	public SonarRunnerApiImpl(MindConfiguration config) throws ConfigurationException
	{
		this.config = config;
	}
	
	public void runSonar(String version) throws ConfigurationException {
		Runner<?> runner = ForkedRunner.create();
		String projectBaseDir = config.getGitWorkingDir() + "\\" + version;
		String sonarRunnerConfigPath = projectBaseDir + "\\sonar-project.properties";
		String sonarQubeConfigPath = config.getSonarQubeConfig();
		
		Configuration sonarRunnerConfig = new PropertiesConfiguration(sonarRunnerConfigPath);
		Configuration sonarQubeConfig = new PropertiesConfiguration(sonarQubeConfigPath);
		
		runner.setProperty("sonar.projectBaseDir", projectBaseDir);
		addAllPropertiesToRunner(runner, sonarRunnerConfig);
		addAllPropertiesToRunner(runner, sonarQubeConfig);
		runner.execute();
	}
	
	private void addAllPropertiesToRunner(Runner<?> runner, Configuration config)
	{
		Iterator<String> it1 = config.getKeys();
		while(it1.hasNext())
		{
			String key = it1.next();
			String val = (String)config.getProperty(key);
			runner.setProperty(key, val);
		}
	}
}
