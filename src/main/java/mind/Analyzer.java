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
	private static org.apache.log4j.Logger log = Logger
			.getLogger(Analyzer.class);

	public Analyzer(SonarReader sonarReader,
			IssueTrackerReader issueTrackerReader, SCMReader scmReader,
			SonarRunnerApi sonarRunner) throws IOException,
			ConfiguredVersionNotExistInSonarException,
			UnequalNumberOfVersionsException {
		this.sonarReader = sonarReader;
		this.issueTrackerReader = issueTrackerReader;
		this.scmReader = scmReader;
		versionDao = new VersionDAO(scmReader, issueTrackerReader, sonarReader);
		this.sonarRunner = sonarRunner;
	}

	public HashMap<String, HashMap<String, HashMap<String, Integer>>> getTechnicalDebtTable()
			throws ConfigurationException, IOException, InvalidRemoteException,
			TransportException, GitAPIException, RedmineException, VersionIdentifierConflictException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, KeyNotFoundException {
		
		HashMap<String, HashMap<String, HashMap<String, Integer>>> table = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();
		
		String previousVersionKey = "0";
		HashMap<String, HashMap<String, Integer>> sizeOfResourcePerVersion = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, HashMap<String, HashMap<String, Integer>>> violationsOfResourcePerVersion = new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
		
		
		for(String currentVersionKey : versionDao.getKeySet())
		{
			sonarRunner.runSonar(versionDao.getScmVersion(currentVersionKey));
			List<String> resources = sonarReader.getListOfAllResources();
			
			HashMap<String,HashMap<String, Integer>> mapOfNumberOfDefectsRelatedToClassPerVersion = getMapOfNumberOfDefectsRelatedToResource(resources, scmReader.getHeadBranch());
			HashMap<String, HashMap<String, Integer>> resourceRows = new HashMap<String, HashMap<String,Integer>>();
			HashMap<String, Integer> sizeOfResources = new HashMap<String, Integer>();
			HashMap<String, HashMap<String, Integer>> violationsOfResource = new HashMap<String, HashMap<String, Integer>>();
			
			
			for (String resource : resources)
			{
				int numberOfDefectsForThisResourceInThisVersion = mapOfNumberOfDefectsRelatedToClassPerVersion.get(versionDao.getMainKeyVersion(currentVersionKey)).get(resource);
				
				HashMap<String, Integer> technicalDebtRow = new HashMap<String, Integer>();
				
				int numberOfLOCTouched;
				int sizeOfClass = sonarReader.getSizeOfClass(resource);
				Iterator<Map.Entry<String, Integer>> it;
				if(previousVersionKey.equals("0") || !violationsOfResourcePerVersion.get(previousVersionKey).containsKey(resource))
				{
					numberOfLOCTouched = sizeOfClass;
					technicalDebtRow.put("size", 0);
					violationsOfResource.put(resource, sonarReader.getNumberOfViolationsPerRuleEverythingZero());
					it = violationsOfResource.get(resource).entrySet().iterator();
				}else
				{
					numberOfLOCTouched = scmReader.getNumberOfLOCtouched(versionDao.getScmVersion(currentVersionKey), versionDao.getScmVersion(previousVersionKey), resource);
					technicalDebtRow.put("size", sizeOfResourcePerVersion.get(previousVersionKey).get(resource));
					it = violationsOfResourcePerVersion.get(previousVersionKey).get(resource).entrySet().iterator();

				}
		 		while (it.hasNext()) {
		 			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) it.next();
				 			technicalDebtRow.put(pairs.getKey(), pairs.getValue());
				 			it.remove();
				 }
				
				technicalDebtRow.put("numberDefects", numberOfDefectsForThisResourceInThisVersion);
				technicalDebtRow.put("locTouched", numberOfLOCTouched);
				

				resourceRows.put(resource, technicalDebtRow);
				
				sizeOfResources.put(resource, sizeOfClass);
				violationsOfResource.put(resource, sonarReader.getNumberOfViolationsPerRule(resource));
				
				sizeOfResourcePerVersion.put(currentVersionKey, sizeOfResources);
				violationsOfResourcePerVersion.put(currentVersionKey, violationsOfResource);
			}
			table.put(currentVersionKey, resourceRows);
			previousVersionKey = currentVersionKey;
		}
		return table;
	}

	// FIXME: TO COMPLICATED AND VERY VERY SLOW
	public HashMap<String, HashMap<String, Integer>> getMapOfNumberOfDefectsRelatedToResource(
			List<String> resources, String branch) throws NoHeadException,
			IOException, GitAPIException, RedmineException,
			VersionIdentifierConflictException, KeyNotFoundException {
		HashMap<String, HashMap<String, Set<Integer>>> mapOfDefectsRelatedToResource = getMapOfDefectsRelatedToResource(
				resources, branch);
		HashMap<String, HashMap<String, Integer>> mapOfNumberOfDefectsRelatedToResource = new HashMap<String, HashMap<String, Integer>>();

		for (Entry<String, HashMap<String, Set<Integer>>> map : mapOfDefectsRelatedToResource
				.entrySet()) {
			HashMap<String, Integer> resourceToNumberOfDefects = new HashMap<String, Integer>();
			for (Entry<String, Set<Integer>> resourceCount : map.getValue()
					.entrySet()) {
				resourceToNumberOfDefects.put(resourceCount.getKey(),
						resourceCount.getValue().size());
				mapOfNumberOfDefectsRelatedToResource.put(map.getKey(),
						resourceToNumberOfDefects);
			}

		}
		return mapOfNumberOfDefectsRelatedToResource;
	}

	private HashMap<String, HashMap<String, Set<Integer>>> getMapOfDefectsRelatedToResource(
			List<String> resources, String branch) throws NoHeadException,
			IOException, GitAPIException, RedmineException,
			VersionIdentifierConflictException, KeyNotFoundException {
		HashMap<String, HashMap<String, Set<Integer>>> mapOfDefectsRelatedToResource = new HashMap<String, HashMap<String, Set<Integer>>>();

		for (String version : versionDao.getKeySet()) {
			HashMap<String, Set<Integer>> resourceToDefectsMap = new HashMap<String, Set<Integer>>();
			for (String resource : resources) {
				Set<Integer> setOfDefectIds = new HashSet<Integer>();
				resourceToDefectsMap.put(resource, setOfDefectIds);
			}
			mapOfDefectsRelatedToResource.put(
					versionDao.getMainKeyVersion(version),
					new HashMap<String, Set<Integer>>(resourceToDefectsMap));
		}

		HashMap<String, List<String>> commitMessagesAndTouchedResourcesForEachRev = scmReader
				.getCommitMessagesAndTouchedFilesForEachRevision(branch);
		HashMap<Integer, String> bugIssueIdList = issueTrackerReader
				.getMapOfBugsRelatedToTheirVersion();
		for (Entry<String, List<String>> commit : commitMessagesAndTouchedResourcesForEachRev
				.entrySet()) {
			for (Entry<Integer, String> bug : bugIssueIdList.entrySet()) {
				if (commit.getKey().contains(bug.getKey().toString())) {
					for (String resource : commit.getValue()) {
						log.debug("bugfix in file" + resource);
						if (mapOfDefectsRelatedToResource.containsKey(bug
								.getValue())) {
							HashMap<String, Set<Integer>> resourceToNumberOfDef = mapOfDefectsRelatedToResource
									.get(bug.getValue());

							if (resourceToNumberOfDef.containsKey(resource))
								resourceToNumberOfDef.get(resource).add(
										bug.getKey());
						}
					}
				}
			}
		}
		return mapOfDefectsRelatedToResource;
	}
}
