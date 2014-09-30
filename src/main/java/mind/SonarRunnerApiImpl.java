package mind;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.sonar.runner.api.ForkedRunner;
import org.sonar.runner.api.EmbeddedRunner;
import org.sonar.runner.api.Runner;

public class SonarRunnerApiImpl implements SonarRunnerApi {
	Configuration config;
	
	public SonarRunnerApiImpl(Configuration config) throws ConfigurationException
	{
		this.config = config;
	}
	
	public void runSonar(String version) throws ConfigurationException {
		Runner<?> runner = ForkedRunner.create();
		String sonarRunnerConfigPath = ConfigAccessor.getValue(config, "git.workingdir") + "\\" + version + "\\sonar-project.properties";
		String sonarQubeConfigPath = ConfigAccessor.getValue(config, "sonar.sonarQubeConfig");
		Configuration sonarRunnerConfig = new PropertiesConfiguration(sonarRunnerConfigPath);
		Configuration sonarQubeConfig = new PropertiesConfiguration(sonarQubeConfigPath);
		
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
