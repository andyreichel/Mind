package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


/**
 * 
 * Classes that implement this interface provide methods to read inforamtion from sonar
 *
 */
public interface SonarReader {
	public HashMap<String, Integer> getNumberOfViolationsPerRule(String className) throws IOException;
	public int getSizeOfClass(String resourceKey) throws IOException;
	public List<String >getListOfAllResources() throws IOException;
	public List<String> getConfiguredVersions();
	public HashMap<String, Integer> getNumberOfViolationsPerRuleEverythingZero() throws IOException;
}
