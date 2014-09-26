package mind;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public interface SonarWebApi {
	public List<String> getListOfAllRules() throws IOException;
	public List<String> getListOfAllResources() throws IOException;
	public int getNumberOfViolationsOfSpecificRuleForResource(String version, String resourceKey, String rule) throws IOException;
	public LinkedHashMap<String, String> getMapOfAllVersionsOfProject() throws IOException;
	public int getSizeOfResource(String resourceKey, String versionDate) throws IOException;
	public List<String> getConfiguredVersions();
}
