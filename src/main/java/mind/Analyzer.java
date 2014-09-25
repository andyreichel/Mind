package mind;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;

import com.taskadapter.redmineapi.RedmineException;

public class Analyzer {
	private SonarReader sonarReader;
	private SonarWebApi api;
	private IssueTrackerReader issueTrackerReader;
	private SCMReader scmReader;
	HashMap<String,HashMap<String, Integer>> mapOfNumberOfDefectsRelatedToClassPerVersion = new HashMap<String, HashMap<String,Integer>>();

	public Analyzer(SonarReader sonarReader, SonarWebApi api,
			IssueTrackerReader issueTrackerReader, SCMReader scmReader) {
		this.sonarReader = sonarReader;
		this.api = api;
		this.issueTrackerReader = issueTrackerReader;
		this.scmReader = scmReader;
	}

	public HashMap<String, HashMap<String, Integer>> getTechnicalDebtTable()
			throws ConfigurationException, IOException, InvalidRemoteException,
			TransportException, GitAPIException, RedmineException {
		
		//branches = getListOfAllRelevantBranches
		//for(branch : branches)
		// git.clone(branch)
		// sonar.execute(branch)
		//for(resource)
		// loctouched = getLocTouched(resource)
		// size = getSize(resource)
		// getViolations
		// getDefects
		
		List<String> resources = api.getListOfAllResources();
		List<AbstractMap.SimpleEntry<String, String>> versionMap = api.getMapOfAllVersionsOfProject();
		HashMap<String, HashMap<String, Integer>> table = new HashMap<String, HashMap<String, Integer>>();
		mapOfNumberOfDefectsRelatedToClassPerVersion = getMapOfNumberOfDefectsRelatedToResource(versionMap, resources, "master");
		
		
		for (String resource : resources) {
				for(int i = versionMap.size()-1; i > 0 ; i--)
				{
					int currentVersionId = i;
					int previousVersionId = i-1;
					table.put(
					resource + "_" + versionMap.get(currentVersionId).getKey(),
					getTechnicalDebtRowForRevision(versionMap.get(currentVersionId), versionMap.get(previousVersionId), resource));
				}
			}
		return table;
	}

	public HashMap<String, Integer> getTechnicalDebtRowForRevision(
			Entry<String, String> currentVersion, Entry<String, String> previousVersion, String className) throws IOException {
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

		return technicalDebtRow;
	}
	
	public HashMap<String, HashMap<String, Integer>> getMapOfNumberOfDefectsRelatedToResource(List<AbstractMap.SimpleEntry<String, String>> versionMap, List<String> resources, String branch) throws NoHeadException, IOException, GitAPIException, RedmineException
	{
		HashMap<String, HashMap<String, Integer>> mapOfNumberOfDefectsRelatedToResource = new HashMap<String, HashMap<String,Integer>>();
		

		for(Entry<String, String> version : versionMap)
		{
			HashMap<String, Integer> resourceToNumberDefectsMap = new HashMap<String, Integer>();
			for(String resource : resources)
			{
				resourceToNumberDefectsMap.put(ResourceUtils.stripOfClassNameFromClassId(resource), 0);
			}
			mapOfNumberOfDefectsRelatedToResource.put(version.getKey(), new HashMap<String, Integer>(resourceToNumberDefectsMap));
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
							String strippedResource = ResourceUtils.stripOfClassNameFromClassId(resource);
							HashMap<String, Integer> resourceToNumberOfDef = mapOfNumberOfDefectsRelatedToResource.get(bug.getValue());
							resourceToNumberOfDef.put(resource, resourceToNumberOfDef.get(strippedResource).intValue()+1);
						}
					}
				}
		}
		
		return mapOfNumberOfDefectsRelatedToResource;
	}
}
