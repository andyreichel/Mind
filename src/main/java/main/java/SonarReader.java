package main.java;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

public interface SonarReader {
	public HashMap<String, Integer> getNumberOfViolationsPerRule(String version, String className);
}
