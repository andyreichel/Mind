package externalinterfaces;

import org.apache.commons.configuration.ConfigurationException;

public interface SonarRunnerApi {
	public void runSonar(String version) throws ConfigurationException;
}
