package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface SonarReader {
	public HashMap<String, Integer> getNumberOfViolationsPerRule(String className) throws IOException;
	public int getSizeOfClass(String resourceKey) throws IOException;
	public List<String >getListOfAllResources() throws IOException;
	public List<String> getConfiguredVersions();
	public HashMap<String, Integer> getNumberOfViolationsPerRuleEverythingZero() throws IOException;
}
