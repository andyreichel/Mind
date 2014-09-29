package mind;
//1.0touched
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface SonarWebApi {
	public List<String> getListOfAllRules() throws IOException;
	public List<String> getListOfAllResources() throws IOException;
	public int getNumberOfViolationsOfSpecificRuleForResource(String version, String resourceKey, String rule) throws IOException;
	public HashMap<String,String> getMapOfAllVersionsOfProject() throws IOException;
	public int getSizeOfResource(String resourceKey, String versionDate) throws IOException;
}
