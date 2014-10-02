package mind;


import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;

import com.taskadapter.redmineapi.RedmineException;

public class Analyzer {
	private SonarReader sonarReader;
	private IssueTrackerReader issueTrackerReader;
	private SCMReader scmReader;
	private VersionDAO versionDao;
	private SonarRunnerApi sonarRunner;
	private static org.apache.log4j.Logger log = Logger.getLogger(Analyzer.class);
	
	public Analyzer(SonarReader sonarReader, IssueTrackerReader issueTrackerReader, SCMReader scmReader, SonarRunnerApi sonarRunner) throws IOException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException {
		this.sonarReader = sonarReader;
		this.issueTrackerReader = issueTrackerReader;
		this.scmReader = scmReader;
		versionDao = new VersionDAO(scmReader, issueTrackerReader, sonarReader);
		this.sonarRunner = sonarRunner;
	}

	public HashMap<String, HashMap<String, Integer>> getTechnicalDebtTable()
			throws ConfigurationException, IOException, InvalidRemoteException,
			TransportException, GitAPIException, RedmineException, VersionIdentifierConflictException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, KeyNotFoundException {
		
		log.debug("start analyze");
		HashMap<String, HashMap<String, Integer>> table = new HashMap<String, HashMap<String, Integer>>();
		
		String previousVersionKey = "0";
		for(String currentVersionKey : versionDao.getKeySet())
		{
			log.debug("run analyzing for " + currentVersionKey);
			sonarRunner.runSonar(versionDao.getScmVersion(currentVersionKey));
			log.debug("analyzing for " + currentVersionKey + " finished");
			List<String> resources = sonarReader.getListOfAllResources();
			log.debug("analyzing for " + currentVersionKey + " finished");
			
			log.debug("start getting number of defects of class");
			HashMap<String,HashMap<String, Integer>> mapOfNumberOfDefectsRelatedToClassPerVersion = getMapOfNumberOfDefectsRelatedToResource(resources, scmReader.getHeadBranch());
			log.debug("stop getting number of defects of class");
			
			for (String resource : resources)
			{
				log.debug("start computing row for " + resource);
				int numberOfDefectsForThisResourceInThisVersion = mapOfNumberOfDefectsRelatedToClassPerVersion.get(versionDao.getMainKeyVersion(currentVersionKey)).get(resource);
				table.put(
				resource + "_" + versionDao.getMainKeyVersion(currentVersionKey), getTechnicalDebtRowForRevision(currentVersionKey, previousVersionKey, resource, numberOfDefectsForThisResourceInThisVersion));
				log.debug("stop computing row for " + resource);
			}
			previousVersionKey = currentVersionKey;
			log.debug("stop analyze for " + currentVersionKey);
		}
		return table;
	}

	public HashMap<String, Integer> getTechnicalDebtRowForRevision(
			String currentVersionKey, String previousVersionKey, String className, int numberDefects) throws IOException, NoSuchBranchException, KeyNotFoundException {
		HashMap<String, Integer> technicalDebtRow = new HashMap<String, Integer>();
		
		HashMap<String, Integer> mapOfViolationsPerRule = sonarReader.getNumberOfViolationsPerRule(className);
		Iterator<Map.Entry<String, Integer>> it = mapOfViolationsPerRule.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) it.next();
			technicalDebtRow.put(pairs.getKey(), pairs.getValue());
			it.remove();
		}
		
		try
		{
			int numberOfLOCTouched = scmReader.getNumberOfLOCtouched(versionDao.getScmVersion(currentVersionKey), versionDao.getScmVersion(previousVersionKey), className);
			technicalDebtRow.put("locTouched", numberOfLOCTouched);
		}catch(KeyNotFoundException e)
		{
			technicalDebtRow.put("locTouched",0);
		}
			
		int sizeOfClass = sonarReader.getSizeOfClass(className);
		technicalDebtRow.put("size", sizeOfClass);
		technicalDebtRow.put("numberDefects", numberDefects);
		return technicalDebtRow;
	}
	
	//FIXME: TO COMPLICATED AND VERY VERY SLOW
	public HashMap<String, HashMap<String, Integer>> getMapOfNumberOfDefectsRelatedToResource(List<String> resources, String branch) throws NoHeadException, IOException, GitAPIException, RedmineException, VersionIdentifierConflictException, KeyNotFoundException
	{
		HashMap<String, HashMap<String, Set<Integer>>> mapOfDefectsRelatedToResource = getMapOfDefectsRelatedToResource(resources, branch);
		HashMap<String, HashMap<String, Integer>> mapOfNumberOfDefectsRelatedToResource = new HashMap<String, HashMap<String,Integer>>();
		
		for(Entry<String, HashMap<String, Set<Integer>>> map : mapOfDefectsRelatedToResource.entrySet())
		{
			HashMap<String, Integer> resourceToNumberOfDefects = new HashMap<String, Integer>();
			for(Entry<String, Set<Integer>> resourceCount : map.getValue().entrySet())
			{
				resourceToNumberOfDefects.put(resourceCount.getKey(), resourceCount.getValue().size());
				mapOfNumberOfDefectsRelatedToResource.put(map.getKey(), resourceToNumberOfDefects);
			}
			
		}
		return mapOfNumberOfDefectsRelatedToResource;
	}
	
	
	private HashMap<String, HashMap<String, Set<Integer>>> getMapOfDefectsRelatedToResource( List<String> resources, String branch) throws NoHeadException, IOException, GitAPIException, RedmineException, VersionIdentifierConflictException, KeyNotFoundException 
	{
		HashMap<String, HashMap<String, Set<Integer>>> mapOfDefectsRelatedToResource = new HashMap<String, HashMap<String,Set<Integer>>>();
		

		for(String version : versionDao.getKeySet())
		{
			HashMap<String, Set<Integer>> resourceToDefectsMap = new HashMap<String, Set<Integer>>();
			for(String resource : resources)
			{
				Set<Integer> setOfDefectIds= new HashSet<Integer>();
				resourceToDefectsMap.put(resource, setOfDefectIds);
			}
			mapOfDefectsRelatedToResource.put(versionDao.getMainKeyVersion(version), new HashMap<String, Set<Integer>>(resourceToDefectsMap));
		}
		
		HashMap<String, List<String>> commitMessagesAndTouchedResourcesForEachRev = scmReader.getCommitMessagesAndTouchedFilesForEachRevision(branch);
		HashMap<Integer,String> bugIssueIdList = issueTrackerReader.getMapOfBugsRelatedToTheirVersion();
		for(Entry<String, List<String>> commit : commitMessagesAndTouchedResourcesForEachRev.entrySet())
		{
				for(Entry<Integer,String> bug : bugIssueIdList.entrySet())
				{
					if(commit.getKey().contains(bug.getKey().toString()))
					{
						for(String resource : commit.getValue())
						{
							log.debug("bugfix in file" + resource);
							if(mapOfDefectsRelatedToResource.containsKey(bug.getValue()))
							{
								HashMap<String, Set<Integer>> resourceToNumberOfDef = mapOfDefectsRelatedToResource.get(bug.getValue());
								
								if(resourceToNumberOfDef.containsKey(resource))
									resourceToNumberOfDef.get(resource).add(bug.getKey());	
							}
						}
					}
				}
		}
		return mapOfDefectsRelatedToResource;
	}
}
