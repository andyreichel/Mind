package mind;

import java.io.IOException;
import java.util.List;

public interface SonarWebApi {
	public List<String> getListOfAllRules() throws IOException;
	public List<String> getListOfAllResources() throws IOException;
	public int getNumberOfViolationsOfSpecificRuleForResource(String resourceKey, String rule) throws IOException;
	public int getSizeOfResource(String resourceKey) throws IOException;
	public List<String> getConfiguredVersions();
}
