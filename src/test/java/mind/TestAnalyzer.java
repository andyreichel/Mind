package mind;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.taskadapter.redmineapi.RedmineException;

@RunWith(MockitoJUnitRunner.class)
public class TestAnalyzer {
	@Mock
	IssueTrackerReader issueTrackerReader;
	
	@Mock
	SCMReader scmReader;
	
	@Mock
	SonarReader sonarReader;
	
	@Mock
	SonarWebApi api;
	
	@Mock
	SonarRunnerApi sonarRunner;
	
	@Test 
	public void getTechnicalDebtTableTest_resourcesFromCommitAreNotTheSameAsInSonar() throws IOException, ConfigurationException, InvalidRemoteException, TransportException, GitAPIException, RedmineException, VersionIdentifierConflictException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, KeyNotFoundException
	{
		List<String> allResources = new ArrayList<String>();
		allResources.add("class1");
		
		List<String> allRules = new ArrayList<String>();
		allRules.add("r1");
		
		List<String> issueTrackerVersions = new ArrayList<String>();
		issueTrackerVersions.add("v1");
		
		List<String> sonarVersions = new ArrayList<String>();
		sonarVersions.add("v1");
		
		List<String> scmVersions = new ArrayList<String>();
		scmVersions.add("v1");
		
		Mockito.doReturn(scmVersions).when(scmReader).getConfiguredVersions();
		Mockito.doReturn(issueTrackerVersions).when(issueTrackerReader).getConfiguredVersions();
		Mockito.doReturn(allResources).when(sonarReader).getListOfAllResources();
		Mockito.doReturn(sonarVersions).when(sonarReader).getConfiguredVersions();
		Mockito.doReturn(allRules).when(api).getListOfAllRules();
		Mockito.doReturn(0).when(scmReader).getNumberOfLOCtouched("v1", "0", "class1");
		Mockito.doReturn("someBranch").when(scmReader).getHeadBranch();
		
		HashMap<String, Integer> violationsPerRuleClass1V0 = new HashMap<String, Integer>();
		violationsPerRuleClass1V0.put("r1", 0);
		
		Mockito.doReturn(violationsPerRuleClass1V0).when(sonarReader).getNumberOfViolationsPerRuleEverythingZero();
		
		HashMap<Integer, String> mapOfBugsRelatedToTheirVersion = new HashMap<Integer, String>();
		mapOfBugsRelatedToTheirVersion.put(10001, "v1");
		
		Mockito.doReturn(mapOfBugsRelatedToTheirVersion).when(issueTrackerReader).getMapOfBugsRelatedToTheirVersion();
		
		HashMap<String, List<String>> commitMessagesAndTouchedFilesForEachRevision = new HashMap<String, List<String>>();
		List<String> touchedFilesRev1 = new ArrayList<String>();
		touchedFilesRev1.add("class1blabla");
		
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10001", touchedFilesRev1);
		
		Mockito.doReturn(commitMessagesAndTouchedFilesForEachRevision).when(scmReader).getCommitMessagesAndTouchedFilesForEachRevision("someBranch");
		
		
		Mockito.doReturn(500).when(sonarReader).getSizeOfClass("class1");

		Mockito.doNothing().when(sonarRunner).runSonar("v1");
		
		Analyzer testAna = new Analyzer(sonarReader, issueTrackerReader, scmReader, sonarRunner);
		
		HashMap<String, HashMap<String, HashMap<String, Integer>>> expectedTable = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();
		HashMap<String, Integer> class1v1_data = new HashMap<String, Integer>();
		class1v1_data.put("numberDefects", 0);
		class1v1_data.put("locTouched", 500);
		class1v1_data.put("size", 0);
		class1v1_data.put("r1", 0);
		HashMap<String, HashMap<String, Integer>> class1v1_row = new HashMap<String, HashMap<String,Integer>>();
		class1v1_row.put("class1", class1v1_data);
		expectedTable.put("v1", class1v1_row);
		
		HashMap<String, HashMap<String, HashMap<String, Integer>>> actualTable = testAna.getTechnicalDebtTable();
		Assert.assertEquals(expectedTable, actualTable);
	}
	
	@Test 
	public void getTechnicalDebtTableTest() throws IOException, ConfigurationException, InvalidRemoteException, TransportException, GitAPIException, RedmineException, VersionIdentifierConflictException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, KeyNotFoundException
	{
		List<String> allResourcesV1 = new ArrayList<String>();
		allResourcesV1.add("class1");
		allResourcesV1.add("class2");
		
		List<String> allResourcesV2 = new ArrayList<String>();
		allResourcesV2.add("class1");
		allResourcesV2.add("class2");
		
		List<String> allResourcesV3 = new ArrayList<String>();
		allResourcesV3.add("class1");
		allResourcesV3.add("class2");
		allResourcesV3.add("class3");
		
		List<String> allRules = new ArrayList<String>();
		allRules.add("r1");
		allRules.add("r2");
		
		List<String> sonarVersions = new ArrayList<String>();
		sonarVersions.add("v1");
		sonarVersions.add("v2");
		sonarVersions.add("v3");
		
		List<String> issueTrackerVersions = new ArrayList<String>();
		issueTrackerVersions.add("v1");
		issueTrackerVersions.add("v2");
		issueTrackerVersions.add("v3");
		
		List<String> scmVersions = new ArrayList<String>();
		scmVersions.add("v1");
		scmVersions.add("v2");
		scmVersions.add("v3");
		
		Mockito.doReturn(scmVersions).when(scmReader).getConfiguredVersions();
		Mockito.doReturn(issueTrackerVersions).when(issueTrackerReader).getConfiguredVersions();
		Mockito.doReturn(sonarVersions).when(sonarReader).getConfiguredVersions();
		
		Mockito.when(sonarReader.getListOfAllResources()).thenReturn(allResourcesV1).thenReturn(allResourcesV2).thenReturn(allResourcesV3);
		Mockito.doReturn(allRules).when(api).getListOfAllRules();
		Mockito.doReturn(0).when(scmReader).getNumberOfLOCtouched("v1", "0", "class1");
		Mockito.doReturn(150).when(scmReader).getNumberOfLOCtouched("v2", "v1", "class1");
		Mockito.doReturn(0).when(scmReader).getNumberOfLOCtouched("v1", "0", "class2");
		Mockito.doReturn(150).when(scmReader).getNumberOfLOCtouched("v2", "v1", "class2");
		Mockito.doReturn(50).when(scmReader).getNumberOfLOCtouched("v3", "v2", "class1");
		Mockito.doReturn(25).when(scmReader).getNumberOfLOCtouched("v3", "v2", "class2");
		Mockito.doReturn("someBranch").when(scmReader).getHeadBranch();
		
		HashMap<String, Integer> violationsZeroC1 = new HashMap<String, Integer>();
		violationsZeroC1.put("r1", 0);
		violationsZeroC1.put("r2", 0);
		HashMap<String, Integer> violationsZeroC2 = new HashMap<String, Integer>();
		violationsZeroC2.put("r1", 0);
		violationsZeroC2.put("r2", 0);
		HashMap<String, Integer> violationsZeroC3 = new HashMap<String, Integer>();
		violationsZeroC3.put("r1", 0);
		violationsZeroC3.put("r2", 0);
		
		HashMap<String, Integer> violationsPerRuleClass1V1 = new HashMap<String, Integer>();
		violationsPerRuleClass1V1.put("r1", 1);
		violationsPerRuleClass1V1.put("r2", 0);
		
		HashMap<String, Integer> violationsPerRuleClass1V2 = new HashMap<String, Integer>();
		violationsPerRuleClass1V2.put("r1", 5);
		violationsPerRuleClass1V2.put("r2", 7);
		
		HashMap<String, Integer> violationsPerRuleClass2V1 = new HashMap<String, Integer>();
		violationsPerRuleClass2V1.put("r1", 13);
		violationsPerRuleClass2V1.put("r2", 9);
		
		HashMap<String, Integer> violationsPerRuleClass2V2 = new HashMap<String, Integer>();
		violationsPerRuleClass2V2.put("r1", 0);
		violationsPerRuleClass2V2.put("r2", 7);
		
		HashMap<String, Integer> violationsPerRuleClass1V3 = new HashMap<String, Integer>();
		violationsPerRuleClass1V3.put("r1", 54);
		violationsPerRuleClass1V3.put("r2", 2);
		
		HashMap<String, Integer> violationsPerRuleClass2V3 = new HashMap<String, Integer>();
		violationsPerRuleClass2V3.put("r1", 3);
		violationsPerRuleClass2V3.put("r2", 77);
		
		Mockito.when(sonarReader.getNumberOfViolationsPerRuleEverythingZero()).thenReturn(violationsZeroC1).thenReturn(violationsZeroC2).thenReturn(violationsZeroC3);
		Mockito.when(sonarReader.getNumberOfViolationsPerRule("class1")).thenReturn(violationsPerRuleClass1V1).thenReturn(violationsPerRuleClass1V2).thenReturn(violationsPerRuleClass1V3);
		Mockito.when(sonarReader.getNumberOfViolationsPerRule("class2")).thenReturn(violationsPerRuleClass2V1).thenReturn(violationsPerRuleClass2V2).thenReturn(violationsPerRuleClass2V3);
		
		HashMap<Integer, String> mapOfBugsRelatedToTheirVersion = new HashMap<Integer, String>();
		mapOfBugsRelatedToTheirVersion.put(10001, "v1");
		mapOfBugsRelatedToTheirVersion.put(10002, "v1");
		mapOfBugsRelatedToTheirVersion.put(10003, "v1");
		mapOfBugsRelatedToTheirVersion.put(10004, "v2");
		mapOfBugsRelatedToTheirVersion.put(10005, "v2");
		mapOfBugsRelatedToTheirVersion.put(10006, "v3");
		mapOfBugsRelatedToTheirVersion.put(10007, "v3");
		
		Mockito.doReturn(mapOfBugsRelatedToTheirVersion).when(issueTrackerReader).getMapOfBugsRelatedToTheirVersion();
		
		HashMap<String, List<String>> commitMessagesAndTouchedFilesForEachRevision = new HashMap<String, List<String>>();
		List<String> touchedFilesRev1 = new ArrayList<String>();
		touchedFilesRev1.add("class1");
		touchedFilesRev1.add("class2");
		List<String> touchedFilesRev2 = new ArrayList<String>();
		touchedFilesRev2.add("class2");
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10001", touchedFilesRev2);
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10002", touchedFilesRev1);
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug"	  , touchedFilesRev2);
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10003", touchedFilesRev2);
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10004", touchedFilesRev2);
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10005", touchedFilesRev1);
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10006", touchedFilesRev1);
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10007", touchedFilesRev2);
		
		Mockito.doReturn(commitMessagesAndTouchedFilesForEachRevision).when(scmReader).getCommitMessagesAndTouchedFilesForEachRevision("someBranch");
		
		Mockito.when(sonarReader.getSizeOfClass("class1")).thenReturn(500).thenReturn(550).thenReturn(600);
		Mockito.when(sonarReader.getSizeOfClass("class2")).thenReturn(500).thenReturn(550).thenReturn(700);
		Mockito.when(sonarReader.getSizeOfClass("class3")).thenReturn(200).thenReturn(220).thenReturn(240);

		Mockito.doNothing().when(sonarRunner).runSonar("v1");
		Mockito.doNothing().when(sonarRunner).runSonar("v2");
		Mockito.doNothing().when(sonarRunner).runSonar("v3");
		
		Analyzer testAna = new Analyzer(sonarReader, issueTrackerReader, scmReader, sonarRunner);
		
		
		HashMap<String, Integer> class1v1_row = new HashMap<String, Integer>();
		class1v1_row.put("numberDefects", 1);
		class1v1_row.put("locTouched", 500);
		class1v1_row.put("size", 0);
		class1v1_row.put("r1", 0);
		class1v1_row.put("r2", 0);
		
		
		HashMap<String, Integer> class1v2_row = new HashMap<String, Integer>();
		class1v2_row.put("numberDefects", 1);
		class1v2_row.put("locTouched", 150);
		class1v2_row.put("size", 500);
		class1v2_row.put("r1", 1);
		class1v2_row.put("r2", 0);
		
		HashMap<String, Integer> class1v3_row = new HashMap<String, Integer>();
		class1v3_row.put("numberDefects", 1);
		class1v3_row.put("locTouched", 50);
		class1v3_row.put("size", 550);
		class1v3_row.put("r1", 5);
		class1v3_row.put("r2", 7);
		
		HashMap<String, Integer> class2v1_row = new HashMap<String, Integer>();
		class2v1_row.put("numberDefects", 3);
		class2v1_row.put("locTouched", 500);
		class2v1_row.put("size", 0);
		class2v1_row.put("r1", 0);
		class2v1_row.put("r2", 0);


		HashMap<String, Integer> class2v2_row = new HashMap<String, Integer>();
		class2v2_row.put("numberDefects", 2);
		class2v2_row.put("locTouched", 150);
		class2v2_row.put("size", 500);
		class2v2_row.put("r1", 13);
		class2v2_row.put("r2", 9);
		
		HashMap<String, Integer> class2v3_row = new HashMap<String, Integer>();
		class2v3_row.put("numberDefects", 2);
		class2v3_row.put("locTouched", 25);
		class2v3_row.put("size", 550);
		class2v3_row.put("r1", 0);
		class2v3_row.put("r2", 7);
		
		HashMap<String, Integer> class3v3_row = new HashMap<String, Integer>();
		class3v3_row.put("numberDefects", 0);
		class3v3_row.put("locTouched", 200);
		class3v3_row.put("size", 0);
		class3v3_row.put("r1", 0);
		class3v3_row.put("r2", 0);
		
		
		HashMap<String, HashMap<String, Integer>> v1rows = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, HashMap<String, Integer>> v2rows = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, HashMap<String, Integer>> v3rows = new HashMap<String, HashMap<String,Integer>>();
		v1rows.put("class1", class1v1_row);
		v1rows.put("class2", class2v1_row);
		v2rows.put("class1", class1v2_row);
		v2rows.put("class2", class2v2_row);
		v3rows.put("class1", class1v3_row);
		v3rows.put("class2", class2v3_row);
		v3rows.put("class3", class3v3_row);
		
		HashMap<String, HashMap<String, HashMap<String, Integer>>> expectedTable = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();
		expectedTable.put("v1", v1rows);
		expectedTable.put("v2", v2rows);
		expectedTable.put("v3", v3rows);
		
		HashMap<String, HashMap<String, HashMap<String, Integer>>> actualTable = testAna.getTechnicalDebtTable();
		Assert.assertEquals(expectedTable, actualTable);
	}
	
	@Test
	public void getMapOfNumberOfDefectsRelatedToResourceTest_successfull() throws RedmineException, NoHeadException, IOException, GitAPIException, VersionIdentifierConflictException, KeyNotFoundException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, ConfigurationException
	{
		List<String> resources = new ArrayList<String>();
		resources.add("project:path/file1");
		resources.add("project:path/file2");
		resources.add("project:path/file3");
		resources.add("project:path/file4");
		resources.add("project:path/file5");
		
		
		List<String> touchedFilesRev1 = new ArrayList<String>();
		touchedFilesRev1.add("project:path/file1");
		touchedFilesRev1.add("project:path/file2");
		List<String> touchedFilesRev2 = new ArrayList<String>();
		touchedFilesRev2.add("project:path/file2");
		touchedFilesRev2.add("project:path/file4");
		
		HashMap<String, List<String>> commitMessagesAndTouchedFilesForEachRevision = new HashMap<String, List<String>>();
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10000", touchedFilesRev1);
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10001", touchedFilesRev2);
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug", touchedFilesRev2);
		
		
		HashMap<Integer, String> mapOfBugsRelatedToTheirVersion = new HashMap<Integer, String>();
		mapOfBugsRelatedToTheirVersion.put(10000, "1.0");
		mapOfBugsRelatedToTheirVersion.put(10001, "1.1");
		
		Mockito.doReturn(mapOfBugsRelatedToTheirVersion).when(issueTrackerReader).getMapOfBugsRelatedToTheirVersion();
		Mockito.doReturn(commitMessagesAndTouchedFilesForEachRevision).when(scmReader).getCommitMessagesAndTouchedFilesForEachRevision("someBranch");
		
		List<String> itVersionMap = new ArrayList<String>();
		itVersionMap.add("1.0");
		itVersionMap.add("1.1");
		List<String> scmVersionMap = new ArrayList<String>();
		scmVersionMap.add("1.0");
		scmVersionMap.add("1.1");
		List<String> sonarVersionMap = new ArrayList<String>();
		sonarVersionMap.add("1.0");
		sonarVersionMap.add("1.1");
		
		Mockito.doReturn(sonarVersionMap).when(sonarReader).getConfiguredVersions();
		Mockito.doReturn(itVersionMap).when(issueTrackerReader).getConfiguredVersions();
		Mockito.doReturn(scmVersionMap).when(scmReader).getConfiguredVersions();
		
		
		HashMap<String, HashMap<String, Integer>> expectedMapOfNumberOfDefectsRelatedToResource = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, Integer> version10NumberOfDefectsRelatedToResource = new HashMap<String, Integer>();
		version10NumberOfDefectsRelatedToResource.put("project:path/file1", 1);
		version10NumberOfDefectsRelatedToResource.put("project:path/file2", 1);
		version10NumberOfDefectsRelatedToResource.put("project:path/file3", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file4", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file5", 0);
		HashMap<String, Integer> version11NumberOfDefectsRelatedToResource = new HashMap<String, Integer>();
		version11NumberOfDefectsRelatedToResource.put("project:path/file1", 0);
		version11NumberOfDefectsRelatedToResource.put("project:path/file2", 1);
		version11NumberOfDefectsRelatedToResource.put("project:path/file3", 0);
		version11NumberOfDefectsRelatedToResource.put("project:path/file4", 1);
		version11NumberOfDefectsRelatedToResource.put("project:path/file5", 0);
		expectedMapOfNumberOfDefectsRelatedToResource.put("1.0", version10NumberOfDefectsRelatedToResource);
		expectedMapOfNumberOfDefectsRelatedToResource.put("1.1", version11NumberOfDefectsRelatedToResource);
		
		Mockito.doNothing().when(sonarRunner).runSonar("1.0");
		Mockito.doNothing().when(sonarRunner).runSonar("1.1");
		
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader, sonarRunner);
		Assert.assertEquals(expectedMapOfNumberOfDefectsRelatedToResource, ana.getMapOfNumberOfDefectsRelatedToResource(resources, "someBranch"));
	}
	
	@Test
	public void getMapOfNumberOfDefectsRelatedToResourceTest_referenceOfBugInMultipleMessages() throws RedmineException, NoHeadException, IOException, GitAPIException, VersionIdentifierConflictException, KeyNotFoundException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, ConfigurationException
	{
		List<String> resources = new ArrayList<String>();
		resources.add("project:path/file1");
		resources.add("project:path/file2");
		resources.add("project:path/file3");
		resources.add("project:path/file4");
		resources.add("project:path/file5");
		
		
		List<String> touchedFilesRev1 = new ArrayList<String>();
		touchedFilesRev1.add("project:path/file1");
		touchedFilesRev1.add("project:path/file2");
		List<String> touchedFilesRev2 = new ArrayList<String>();
		touchedFilesRev2.add("project:path/file2");
		touchedFilesRev2.add("project:path/file4");
		
		HashMap<String, List<String>> commitMessagesAndTouchedFilesForEachRevision = new HashMap<String, List<String>>();
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10000", touchedFilesRev1);
		commitMessagesAndTouchedFilesForEachRevision.put("this is a g 10000", touchedFilesRev1);
		commitMessagesAndTouchedFilesForEachRevision.put("this g 10000", touchedFilesRev2);
		commitMessagesAndTouchedFilesForEachRevision.put("this g 10001", touchedFilesRev2);
		
		
		HashMap<Integer, String> mapOfBugsRelatedToTheirVersion = new HashMap<Integer, String>();
		mapOfBugsRelatedToTheirVersion.put(10000, "1.0");
		mapOfBugsRelatedToTheirVersion.put(10001, "1.0");
		
		Mockito.doReturn(mapOfBugsRelatedToTheirVersion).when(issueTrackerReader).getMapOfBugsRelatedToTheirVersion();
		Mockito.doReturn(commitMessagesAndTouchedFilesForEachRevision).when(scmReader).getCommitMessagesAndTouchedFilesForEachRevision("someBranch");
		
		List<String> itVersionMap = new ArrayList<String>();
		itVersionMap.add("1.0");
		List<String> scmVersionMap = new ArrayList<String>();
		scmVersionMap.add("1.0");
		List<String> sonarVersionMap = new ArrayList<String>();
		sonarVersionMap.add("1.0");
		
		
		Mockito.doReturn(sonarVersionMap).when(sonarReader).getConfiguredVersions();
		Mockito.doReturn(itVersionMap).when(issueTrackerReader).getConfiguredVersions();
		Mockito.doReturn(scmVersionMap).when(scmReader).getConfiguredVersions();
		
		HashMap<String, HashMap<String, Integer>> expectedMapOfNumberOfDefectsRelatedToResource = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, Integer> version10NumberOfDefectsRelatedToResource = new HashMap<String, Integer>();
		version10NumberOfDefectsRelatedToResource.put("project:path/file1", 1);
		version10NumberOfDefectsRelatedToResource.put("project:path/file2", 2);
		version10NumberOfDefectsRelatedToResource.put("project:path/file3", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file4", 2);
		version10NumberOfDefectsRelatedToResource.put("project:path/file5", 0);
		expectedMapOfNumberOfDefectsRelatedToResource.put("1.0", version10NumberOfDefectsRelatedToResource);
		
		Mockito.doNothing().when(sonarRunner).runSonar("1.0");
		
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader, sonarRunner);
		Assert.assertEquals(expectedMapOfNumberOfDefectsRelatedToResource, ana.getMapOfNumberOfDefectsRelatedToResource(resources, "someBranch"));
	}
	
	@Test
	public void getMapOfNumberOfDefectsRelatedToResourceTest_VersionOfRedmineAndSonarAreNotEqual() throws RedmineException, NoHeadException, IOException, GitAPIException, VersionIdentifierConflictException, KeyNotFoundException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, ConfigurationException
	{
		List<String> resources = new ArrayList<String>();
		resources.add("project:path/file1");
		resources.add("project:path/file2");
		resources.add("project:path/file3");
		resources.add("project:path/file4");
		resources.add("project:path/file5");
		
		
		List<String> touchedFilesRev1 = new ArrayList<String>();
		touchedFilesRev1.add("project:path/file1");
		touchedFilesRev1.add("project:path/file2");
		List<String> touchedFilesRev2 = new ArrayList<String>();
		touchedFilesRev2.add("project:path/file2");
		touchedFilesRev2.add("project:path/file4");
		
		HashMap<String, List<String>> commitMessagesAndTouchedFilesForEachRevision = new HashMap<String, List<String>>();
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug ", touchedFilesRev1);
		
		
		HashMap<Integer, String> mapOfBugsRelatedToTheirVersion = new HashMap<Integer, String>();
		mapOfBugsRelatedToTheirVersion.put(10000, "1.0");
		
		Mockito.doReturn(mapOfBugsRelatedToTheirVersion).when(issueTrackerReader).getMapOfBugsRelatedToTheirVersion();
		Mockito.doReturn(commitMessagesAndTouchedFilesForEachRevision).when(scmReader).getCommitMessagesAndTouchedFilesForEachRevision("someBranch");
		
		List<String> itVersionMap = new ArrayList<String>();
		itVersionMap.add("1.0");
		List<String> scmVersionMap = new ArrayList<String>();
		scmVersionMap.add("1.0");
		List<String> sonarVersionMap = new ArrayList<String>();
		sonarVersionMap.add("1.0");
		
		
		Mockito.doReturn(sonarVersionMap).when(sonarReader).getConfiguredVersions();
		Mockito.doReturn(itVersionMap).when(issueTrackerReader).getConfiguredVersions();
		Mockito.doReturn(scmVersionMap).when(scmReader).getConfiguredVersions();
		
		HashMap<String, HashMap<String, Integer>> expectedMapOfNumberOfDefectsRelatedToResource = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, Integer> version10NumberOfDefectsRelatedToResource = new HashMap<String, Integer>();
		version10NumberOfDefectsRelatedToResource.put("project:path/file1", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file2", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file3", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file4", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file5", 0);
		expectedMapOfNumberOfDefectsRelatedToResource.put("1.0", version10NumberOfDefectsRelatedToResource);
		
		Mockito.doNothing().when(sonarRunner).runSonar("1.0");
		
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader, sonarRunner);
		Assert.assertEquals(expectedMapOfNumberOfDefectsRelatedToResource, ana.getMapOfNumberOfDefectsRelatedToResource(resources, "someBranch"));
	}
	
	@Test
	public void getMapOfNumberOfDefectsRelatedToResourceTest_bugVersionDoesNotMatchAnalyzedFiles() throws RedmineException, NoHeadException, IOException, GitAPIException, VersionIdentifierConflictException, KeyNotFoundException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, ConfigurationException
	{
		List<String> resources = new ArrayList<String>();
		resources.add("project:path/file1");
		resources.add("project:path/file2");
		resources.add("project:path/file3");
		resources.add("project:path/file4");
		resources.add("project:path/file5");
		
		
		List<String> touchedFilesRev1 = new ArrayList<String>();
		touchedFilesRev1.add("project:path/file1");
		touchedFilesRev1.add("project:path/file2");
		List<String> touchedFilesRev2 = new ArrayList<String>();
		touchedFilesRev2.add("project:path/file2");
		touchedFilesRev2.add("project:path/file4");
		
		HashMap<String, List<String>> commitMessagesAndTouchedFilesForEachRevision = new HashMap<String, List<String>>();
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10000", touchedFilesRev1);
		
		
		HashMap<Integer, String> mapOfBugsRelatedToTheirVersion = new HashMap<Integer, String>();
		mapOfBugsRelatedToTheirVersion.put(10000, "1.5");
		
		Mockito.doReturn(mapOfBugsRelatedToTheirVersion).when(issueTrackerReader).getMapOfBugsRelatedToTheirVersion();
		Mockito.doReturn(commitMessagesAndTouchedFilesForEachRevision).when(scmReader).getCommitMessagesAndTouchedFilesForEachRevision("someBranch");
		
		List<String> itVersionMap = new ArrayList<String>();
		itVersionMap.add("1.0");
		List<String> scmVersionMap = new ArrayList<String>();
		scmVersionMap.add("1.0");
		List<String> sonarVersionMap = new ArrayList<String>();
		sonarVersionMap.add("1.0");
		
		
		Mockito.doReturn(sonarVersionMap).when(sonarReader).getConfiguredVersions();
		Mockito.doReturn(itVersionMap).when(issueTrackerReader).getConfiguredVersions();
		Mockito.doReturn(scmVersionMap).when(scmReader).getConfiguredVersions();
		
		HashMap<String, HashMap<String, Integer>> expectedMapOfNumberOfDefectsRelatedToResource = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, Integer> version10NumberOfDefectsRelatedToResource = new HashMap<String, Integer>();
		version10NumberOfDefectsRelatedToResource.put("project:path/file1", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file2", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file3", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file4", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file5", 0);
		expectedMapOfNumberOfDefectsRelatedToResource.put("1.0", version10NumberOfDefectsRelatedToResource);
		
		Mockito.doNothing().when(sonarRunner).runSonar("1.0");
		
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader, sonarRunner);
		Assert.assertEquals(expectedMapOfNumberOfDefectsRelatedToResource, ana.getMapOfNumberOfDefectsRelatedToResource(resources, "someBranch"));
	}
	
}
