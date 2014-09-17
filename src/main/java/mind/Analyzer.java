package mind;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class Analyzer {
	private SonarReader sonarReader;
	private SonarWebApi api;
	private IssueTrackerReader issueTrackerReader;
	private SCMReader scmReader;
	
	public static void main(String[] args) throws ConfigurationException, IOException, InvalidRemoteException, TransportException, GitAPIException
	{
		SCMReader scm = new GitReader(new PropertiesConfiguration("mind.properties"));
		//SonarReader sonar = new SonarReaderImpl();
	}
	
	public Analyzer(SonarReader sonarReader, SonarWebApi api, IssueTrackerReader issueTrackerReader, SCMReader scmReader)
	{
		this.sonarReader = sonarReader;
		this.api = api;
		this.issueTrackerReader = issueTrackerReader;
		this.scmReader = scmReader;
	}
	
	public HashMap<String, HashMap<String, Integer>> getTechnicalDebtTable() throws ConfigurationException, IOException, InvalidRemoteException, TransportException, GitAPIException
	{
		List<String> resources = api.getListOfAllResources();
		HashMap<String, String> versionMap = api.getMapOfAllVersionsOfProject();
		HashMap<String, HashMap<String, Integer>> table = new HashMap<String, HashMap<String,Integer>>();
		for(String resource : resources)
		{
			for(Entry<String, String> version : versionMap.entrySet())
			{
				table.put(resource + "_" + version.getKey(), getTechnicalDebtRowForRevision(version.getValue(), resource));
			}
		}
		return table;
	}
	
	public HashMap<String, Integer> getTechnicalDebtRowForRevision(String versionDate, String className) throws IOException
	{
		HashMap<String, Integer> technicalDebtRow = new HashMap<String, Integer>();
		HashMap<String, Integer> map = sonarReader.getNumberOfViolationsPerRule(versionDate, className);
		Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
		
		while(it.hasNext())
		{
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) it.next();
			technicalDebtRow.put(pairs.getKey(), pairs.getValue());
			it.remove();
		}
		
		technicalDebtRow.put("locTouched", scmReader.getNumberOfLOCtouched(versionDate, className));
		technicalDebtRow.put("size", sonarReader.getSizeOfClass(versionDate, className));
		
		
		technicalDebtRow.put("numberDefects", scmReader.getNumberOfDefectsRelatedToClass(versionDate, className, issueTrackerReader));
		return technicalDebtRow;
	}
}
