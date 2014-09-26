package mind;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;

import com.taskadapter.redmineapi.RedmineException;

public class Analyzer {
	private SonarReader sonarReader;
	private IssueTrackerReader issueTrackerReader;
	private SCMReader scmReader;
	HashMap<String,HashMap<String, Integer>> mapOfNumberOfDefectsRelatedToClassPerVersion = new HashMap<String, HashMap<String,Integer>>();

	public Analyzer(SonarReader sonarReader, IssueTrackerReader issueTrackerReader, SCMReader scmReader) {
		this.sonarReader = sonarReader;
		this.issueTrackerReader = issueTrackerReader;
		this.scmReader = scmReader;
	}

	public HashMap<String, HashMap<String, Integer>> getTechnicalDebtTable()
			throws ConfigurationException, IOException, InvalidRemoteException,
			TransportException, GitAPIException, RedmineException, VersionIdentifierConflictException {
		List<String> resources = sonarReader.getListOfAllResources();
		List<AbstractMap.SimpleEntry<String, String>> versionMap = sonarReader.getMapOfAllVersionsOfProject();
		HashMap<String, HashMap<String, Integer>> table = new HashMap<String, HashMap<String, Integer>>();
		mapOfNumberOfDefectsRelatedToClassPerVersion = getMapOfNumberOfDefectsRelatedToResource(versionMap, resources, scmReader.getHeadBranch());
		
		for (String resource : resources) {
				for(int i = versionMap.size()-1; i > 0 ; i--)
				{
					int currentVersionId = i;
					int previousVersionId = i-1;
					int numberOfDefectsForThisResourceInThisVersion = mapOfNumberOfDefectsRelatedToClassPerVersion.get(versionMap.get(currentVersionId).getKey()).get(resource);
					
					table.put(
					resource + "_" + versionMap.get(currentVersionId).getKey(),
					getTechnicalDebtRowForRevision(versionMap.get(currentVersionId), versionMap.get(previousVersionId), resource, numberOfDefectsForThisResourceInThisVersion));
				}
			}
		return table;
	}

	public HashMap<String, Integer> getTechnicalDebtRowForRevision(
			Entry<String, String> currentVersion, Entry<String, String> previousVersion, String className, int numberDefects) throws IOException, NoSuchBranchException {
		HashMap<String, Integer> technicalDebtRow = new HashMap<String, Integer>();
		HashMap<String, Integer> map = sonarReader
				.getNumberOfViolationsPerRule(currentVersion.getValue(), className);
		Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) it
					.next();
			technicalDebtRow.put(pairs.getKey(), pairs.getValue());
			it.remove();
		}
		
		technicalDebtRow.put("locTouched",
				scmReader.getNumberOfLOCtouched(currentVersion.getKey(), previousVersion.getKey(), className));
		technicalDebtRow.put("size",
				sonarReader.getSizeOfClass(currentVersion.getValue(), className));
		technicalDebtRow.put("numberDefects", numberDefects);
		return technicalDebtRow;
	}
	
	//FIXME: TO COMPLICATED AND VERY VERY SLOW
	public HashMap<String, HashMap<String, Integer>> getMapOfNumberOfDefectsRelatedToResource(List<AbstractMap.SimpleEntry<String, String>> versionMap, List<String> resources, String branch) throws NoHeadException, IOException, GitAPIException, RedmineException, VersionIdentifierConflictException
	{
		HashMap<String, HashMap<String, Set<Integer>>> mapOfDefectsRelatedToResource = getMapOfDefectsRealtedToResource(versionMap, resources, branch);
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
	
	
	private HashMap<String, HashMap<String, Set<Integer>>> getMapOfDefectsRealtedToResource(List<AbstractMap.SimpleEntry<String, String>> versionMap, List<String> resources, String branch) throws NoHeadException, IOException, GitAPIException, RedmineException, VersionIdentifierConflictException 
	{
		HashMap<String, HashMap<String, Set<Integer>>> mapOfDefectsRelatedToResource = new HashMap<String, HashMap<String,Set<Integer>>>();
		

		for(Entry<String, String> version : versionMap)
		{
			HashMap<String, Set<Integer>> resourceToDefectsMap = new HashMap<String, Set<Integer>>();
			for(String resource : resources)
			{
				Set<Integer> setOfDefectIds= new HashSet<Integer>();
				resourceToDefectsMap.put(resource, setOfDefectIds);
			}
			mapOfDefectsRelatedToResource.put(version.getKey(), new HashMap<String, Set<Integer>>(resourceToDefectsMap));
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
							HashMap<String, Set<Integer>> resourceToNumberOfDef = mapOfDefectsRelatedToResource.get(bug.getValue());
							if(resourceToNumberOfDef == null)
							{
								throw new VersionIdentifierConflictException("Check if versions in SCM, Issue Tracker and Git are the same");
							}
								 
							if(resourceToNumberOfDef.containsKey(resource))
								resourceToNumberOfDef.get(resource).add(bug.getKey());
						}
					}
				}
		}
		return mapOfDefectsRelatedToResource;
	}
}
