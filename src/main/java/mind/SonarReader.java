package mind;

import java.io.IOException;
import java.util.HashMap;

public interface SonarReader {
	public HashMap<String, Integer> getNumberOfViolationsPerRule(String versionDate, String className) throws IOException;
	public int getSizeOfClass(String version, String className) throws IOException;
}
