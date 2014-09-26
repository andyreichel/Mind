package mind;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;

public interface SonarReader {
	public HashMap<String, Integer> getNumberOfViolationsPerRule(String versionDate, String className) throws IOException;
	public int getSizeOfClass(String version, String className) throws IOException;
	public List<String >getListOfAllResources() throws IOException;
	public List<AbstractMap.SimpleEntry<String, String>> getMapOfAllVersionsOfProject() throws IOException;
}
