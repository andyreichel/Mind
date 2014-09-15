package main.java;

import java.util.HashMap;
import java.util.List;

public interface SonarReader {
	public HashMap<String, Integer> getNumberOfViolationsPerRule(String version, String className);
}
