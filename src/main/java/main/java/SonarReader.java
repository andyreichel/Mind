package main.java;

import java.io.IOException;
import java.util.HashMap;

public interface SonarReader {
	public HashMap<String, Integer> getNumberOfViolationsPerRule(String version, String className) throws IOException;
}
