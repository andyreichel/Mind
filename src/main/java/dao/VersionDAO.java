package dao;

import interfaces.IssueTrackerReader;
import interfaces.SCMReader;
import interfaces.SonarReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import exceptions.ConfiguredVersionNotExistInSonarException;
import exceptions.KeyNotFoundException;
import exceptions.UnequalNumberOfVersionsException;

public class VersionDAO {
	private List<String> scmVersions;
	private List<String> issueTrackerVersions;
	private List<String> sonarVersions;
	private LinkedHashMap<String, HashMap<String, String>> versionDao;
	
	public VersionDAO(SCMReader scmReader, IssueTrackerReader itReader, SonarReader sonarReader) throws IOException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException
	{
		scmVersions = scmReader.getConfiguredVersions();
		issueTrackerVersions = itReader.getConfiguredVersions();
		sonarVersions = sonarReader.getConfiguredVersions();
		
		if(!isNumberOfVersionsEqual())
			throw new UnequalNumberOfVersionsException(	"SCM has "+ scmVersions.size() + " versions.\n" +
														"Sonar has " + sonarVersions.size() + " versions.\n" +
														"IssueTracker has " + issueTrackerVersions.size() + " versions.\n");	
		createVersionDAO();
	}
	
	private void createVersionDAO()
	{
		LinkedHashMap<String, HashMap<String, String>> versionDao = new LinkedHashMap<String, HashMap<String, String>>();
		for(int i = 0; i < issueTrackerVersions.size(); i++)
		{
			LinkedHashMap<String, String> versionMap = new LinkedHashMap<String, String>();
			versionMap.put("SCM", scmVersions.get(i));
			versionMap.put("IT", issueTrackerVersions.get(i));
			versionMap.put("SONAR", sonarVersions.get(i));
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
		return getVersion(key, "SONAR");
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
