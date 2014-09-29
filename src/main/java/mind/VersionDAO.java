package mind;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VersionDAO {
	private List<String> scmVersions;
	private List<String> issueTrackerVersions;
	private LinkedHashMap<String, String> sonarVersions;
	private LinkedHashMap<String, HashMap<String, String>> versionDao;
	
	public VersionDAO(SCMReader scmReader, IssueTrackerReader itReader, SonarReader sonarReader) throws IOException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException
	{
		scmVersions = scmReader.getConfiguredVersions();
		sonarVersions = sonarReader.getMapOfAllConfiguredVersionsOfProject();
		issueTrackerVersions = itReader.getConfiguredVersions();
		
		if(!isNumberOfVersionsEqual())
			throw new UnequalNumberOfVersionsException(	"SCM has "+ scmVersions.size() + " versions.\n" +
														"Sonar has " + sonarVersions.size() + " versions.\n" +
														"IssueTracker has " + issueTrackerVersions.size() + " versions.\n");	
		createVersionDAO();
	}
	
	private void createVersionDAO()
	{
		LinkedHashMap<String, HashMap<String, String>> versionDao = new LinkedHashMap<String, HashMap<String, String>>();
		Iterator<Map.Entry<String, String>> sonarIt = sonarVersions.entrySet().iterator();
		for(int i = 0; i < issueTrackerVersions.size(); i++)
		{
			Map.Entry<String, String> sonarEntry = sonarIt.next();
			HashMap<String, String> versionMap = new HashMap<String, String>();
			versionMap.put("SCM", scmVersions.get(i));
			versionMap.put("IT", issueTrackerVersions.get(i));
			versionMap.put("SONARKEY", sonarEntry.getKey());
			versionMap.put("SONARDATE", sonarEntry.getValue());
			//Does not matter which key is taken as main key
			versionDao.put(issueTrackerVersions.get(i), versionMap);
		}
		this.versionDao = versionDao;
	}

	public List<String> getKeySet()
	{
		//does not matter which version set is main
		return issueTrackerVersions;
	}
	
	public String getMainKeyVersion(String key) throws KeyNotFoundException
	{
		return getIssueTrackerVersion(key);
	}
	
	public String getScmVersion(String key) throws KeyNotFoundException
	{
		return getVersion(key, "SCM");
	}
	
	public String getSonarKeyVersion(String key) throws KeyNotFoundException
	{
		return getVersion(key, "SONARKEY");
	}
	
	public String getSonarDateVersion(String key) throws KeyNotFoundException
	{
		return getVersion(key, "SONARDATE");
	}
	
	public String getIssueTrackerVersion(String key) throws KeyNotFoundException
	{
		return getVersion(key, "IT");
	}
	
	private String getVersion(String key, String component) throws KeyNotFoundException
	{
		if(!versionDao.containsKey(key))
			throw new KeyNotFoundException(key + " was not found.");
		return versionDao.get(key).get(component);
	}
	
	public boolean isNumberOfVersionsEqual()
	{
		return ((scmVersions.size() == sonarVersions.size()) && (sonarVersions.size()== issueTrackerVersions.size()) && (scmVersions.size() == issueTrackerVersions.size()));
	}
}
