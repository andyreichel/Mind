package main.java;

import java.util.HashMap;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;


public class SonarReaderImpl implements SonarReader {
	private SonarWebApiAccess api;
	public SonarReaderImpl(SonarWebApiAccess api)
	{
		this.api = api;
	}
	
	public HashMap<String, Integer> getNumberOfViolationsPerRule(
			String version, String className) {

		return null;
	}

}
