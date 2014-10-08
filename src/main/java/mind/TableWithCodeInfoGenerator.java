package mind;

import interfaces.IssueTrackerReader;
import interfaces.SCMReader;
import interfaces.SonarReader;
import interfaces.SonarRunnerApi;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

import com.google.inject.Inject;
import com.taskadapter.redmineapi.RedmineException;

import dao.TableDAO;
import dao.VersionDAO;
import exceptions.ConfiguredVersionNotExistInSonarException;
import exceptions.KeyNotFoundException;
import exceptions.UnequalNumberOfVersionsException;
import exceptions.VersionIdentifierConflictException;


/**
 * This class is using a IssueTrackerReader a SCMReader and a SonarReader and
 * creates a table with the violations, number loc, number loc Touched and number defects per class per version (example)
 * Version1={class1={rule1Violations=5;numberDefects=2;size=500;numberLOCTouched=200}}
 * Version2={class1={rule1Violations=2;numberDefects=0;size=550;numberLOCTouched=150}}
 *
 */
public class TableWithCodeInfoGenerator {
	private SonarReader sonarReader;
	private IssueTrackerReader issueTrackerReader;
	private SCMReader scmReader;
	private VersionDAO versionDao;
	private SonarRunnerApi sonarRunner;
	private static org.apache.log4j.Logger log = Logger
			.getLogger(TableWithCodeInfoGenerator.class);

	/**
	 * Initializes the Analyzer and validates the software versions of the different tools 
	 * @param sonarReader
	 * @param issueTrackerReader
	 * @param scmReader
	 * @param sonarRunner
	 * @throws IOException
	 * @throws ConfiguredVersionNotExistInSonarException
	 * @throws UnequalNumberOfVersionsException
	 * @throws ConfigurationException 
	 */
	@Inject
	public TableWithCodeInfoGenerator(SonarReader sonarReader,
			IssueTrackerReader issueTrackerReader, SCMReader scmReader,
			SonarRunnerApi sonarRunner) throws IOException,
			ConfiguredVersionNotExistInSonarException,
			UnequalNumberOfVersionsException, ConfigurationException {
		this.sonarReader = sonarReader;
		this.issueTrackerReader = issueTrackerReader;
		this.scmReader = scmReader;
		versionDao = new VersionDAO(scmReader, issueTrackerReader, sonarReader);
		this.sonarRunner = sonarRunner;
	}

	/**
	 * Main algorithm to generate the table utilizing the given apis
	 * @return
	 * @throws ConfigurationException
	 * @throws IOException
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 * @throws RedmineException
	 * @throws VersionIdentifierConflictException
	 * @throws ConfiguredVersionNotExistInSonarException
	 * @throws UnequalNumberOfVersionsException
	 * @throws KeyNotFoundException
	 */
	public TableDAO getTableWithCodeInfoForEveryClassInEveryRelease()
			throws ConfigurationException, IOException, InvalidRemoteException,
			TransportException, GitAPIException, RedmineException, VersionIdentifierConflictException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, KeyNotFoundException {
		
		LinkedHashMap<String, HashMap<String, HashMap<String, Integer>>> table = new LinkedHashMap<String, HashMap<String, HashMap<String, Integer>>>();
		
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
				
				Integer numberOfLOCTouched;
				int sizeOfClass = sonarReader.getSizeOfClass(resource);
				Iterator<Map.Entry<String, Integer>> it;
				if(previousVersionKey.equals("0") || !violationsOfResourcePerVersion.get(previousVersionKey).containsKey(resource))
				{
					numberOfLOCTouched = null;
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
		return new TableDAO(table);
	}

	/**
	 * Returns a map of number of defects related to one resource for example
	 * {Version1={class1=5;class2=3},
	 *  Version2={class1=3;class2=4}}
	 *  The information is pulled from the issue tracker. It is assumed that in the issue is documented to which version this defect was injected.
	 *  The issue number will than be searched in the git revisions. If the issue number is found all affected files will be regarded as files 
	 *  of this defect and hence the defect number for this class will be increased.
	 * @param resources
	 * @param branch
	 * @return
	 * @throws NoHeadException
	 * @throws IOException
	 * @throws GitAPIException
	 * @throws RedmineException
	 * @throws VersionIdentifierConflictException
	 * @throws KeyNotFoundException
	 * @throws ConfigurationException 
	 */
	public HashMap<String, HashMap<String, Integer>> getMapOfNumberOfDefectsRelatedToResource(
			List<String> resources, String branch) throws NoHeadException,
			IOException, GitAPIException, RedmineException,
			VersionIdentifierConflictException, KeyNotFoundException, ConfigurationException {
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

	/**
	 * Returns a map of defects related to one resource for example
	 * {Version1={class1={issueid=1000, issueid=10001};class2={issueid=1000, issueid=10001}},
	 *  Version2={class1={issueid=1003, issueid=10002}}}
	 * @param resources
	 * @param branch
	 * @return
	 * @throws NoHeadException
	 * @throws IOException
	 * @throws GitAPIException
	 * @throws RedmineException
	 * @throws VersionIdentifierConflictException
	 * @throws KeyNotFoundException
	 * @throws ConfigurationException 
	 */
	private HashMap<String, HashMap<String, Set<Integer>>> getMapOfDefectsRelatedToResource(
			List<String> resources, String branch) throws NoHeadException,
			IOException, GitAPIException, RedmineException,
			VersionIdentifierConflictException, KeyNotFoundException, ConfigurationException {
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
		HashMap<Integer, String> bugIssueIdList = issueTrackerReader.getMapOfBugsRelatedToTheirVersion();
		for (Entry<String, List<String>> commit : commitMessagesAndTouchedResourcesForEachRev
				.entrySet()) {
			for (Entry<Integer, String> bug : bugIssueIdList.entrySet()) {
				if (commit.getKey().contains(bug.getKey().toString())) {
					for (String resource : commit.getValue()) {
						log.debug("bugfix in file" + resource);
						if (mapOfDefectsRelatedToResource.containsKey(bug.getValue())) {
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
