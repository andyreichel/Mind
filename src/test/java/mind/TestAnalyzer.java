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
	public void getTechnicalDebtRowForRevisionTest_successfull() throws IOException, NoSuchBranchException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, KeyNotFoundException, ConfigurationException
	{
		List<String> itVersionMap = new ArrayList<String>();
		itVersionMap.add("1");
		itVersionMap.add("0");
		List<String> scmVersionMap = new ArrayList<String>();
		scmVersionMap.add("1");
		scmVersionMap.add("0");
		List<String> sonarVersionMap = new ArrayList<String>();
		sonarVersionMap.add("1");
		sonarVersionMap.add("0");
		
		Mockito.doReturn(sonarVersionMap).when(sonarReader).getConfiguredVersions();
		Mockito.doReturn(itVersionMap).when(issueTrackerReader).getConfiguredVersions();
		Mockito.doReturn(scmVersionMap).when(scmReader).getConfiguredVersions();
		Mockito.doReturn("201405").when(sonarReader).getDateOfLastSonarAnalyse("1");
		
		Mockito.doReturn(100).when(sonarReader).getSizeOfClass("201405", "someClass");
		Mockito.doReturn(50).when(scmReader).getNumberOfLOCtouched("1", "0", "someClass");
		HashMap<String, Integer> violationsPerRule = new HashMap<String, Integer>();
		violationsPerRule.put("r1", 1);
		violationsPerRule.put("r2", 0);
		
		Mockito.doReturn(violationsPerRule).when(sonarReader).getNumberOfViolationsPerRule("201405", "someClass");
		
		Mockito.doNothing().when(sonarRunner).runSonar("0");
		Mockito.doNothing().when(sonarRunner).runSonar("1");
		
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader, sonarRunner);
		HashMap<String, Integer> analyzedRow = ana.getTechnicalDebtRowForRevision("1", "0", "someClass", 2);
		
		HashMap<String,Integer> expectedRow = new HashMap<String, Integer>();
		expectedRow.put("r1", 1);
		expectedRow.put("r2", 0);
		expectedRow.put("size", 100);
		expectedRow.put("numberDefects", 2);
		expectedRow.put("locTouched", 50);
		
		Assert.assertEquals(expectedRow, analyzedRow);
	}
	
	@Test
	public void getTechnicalDebtRowForRevisionTest_noViolations() throws IOException, NoSuchBranchException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, KeyNotFoundException, ConfigurationException
	{
		List<String> itVersionMap = new ArrayList<String>();
		itVersionMap.add("v1");
		itVersionMap.add("v0");
		List<String> scmVersionMap = new ArrayList<String>();
		scmVersionMap.add("v1");
		scmVersionMap.add("v0");
		List<String> sonarVersionMap = new ArrayList<String>();
		sonarVersionMap.add("v1");
		sonarVersionMap.add("v0");
		
		
		Mockito.doReturn(sonarVersionMap).when(sonarReader).getConfiguredVersions();
		Mockito.doReturn(itVersionMap).when(issueTrackerReader).getConfiguredVersions();
		Mockito.doReturn(scmVersionMap).when(scmReader).getConfiguredVersions();
		
		Mockito.doReturn("201405").when(sonarReader).getDateOfLastSonarAnalyse("v1");
		
		Mockito.doReturn(100).when(sonarReader).getSizeOfClass("201405", "someClass");
		Mockito.doReturn(50).when(scmReader).getNumberOfLOCtouched("v1", "v0", "someClass");

		Mockito.doNothing().when(sonarRunner).runSonar("v1");
		Mockito.doNothing().when(sonarRunner).runSonar("v0");
		
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader, sonarRunner);
		

		HashMap<String, Integer> analyzedRow = ana.getTechnicalDebtRowForRevision("v1", "v0", "someClass",2);
		
		HashMap<String,Integer> expectedRow = new HashMap<String, Integer>();
		expectedRow.put("size", 100);
		expectedRow.put("numberDefects", 2);
		expectedRow.put("locTouched", 50);
		
		Assert.assertEquals(expectedRow, analyzedRow);
	}
	
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
		Mockito.doReturn("20141001").when(sonarReader).getDateOfLastSonarAnalyse("v1");
		
		HashMap<String, Integer> violationsPerRuleClass1V1 = new HashMap<String, Integer>();
		violationsPerRuleClass1V1.put("r1", 1);
		
		Mockito.doReturn(violationsPerRuleClass1V1).when(sonarReader).getNumberOfViolationsPerRule("20141001", "class1");
		
		HashMap<Integer, String> mapOfBugsRelatedToTheirVersion = new HashMap<Integer, String>();
		mapOfBugsRelatedToTheirVersion.put(10001, "v1");
		
		Mockito.doReturn(mapOfBugsRelatedToTheirVersion).when(issueTrackerReader).getMapOfBugsRelatedToTheirVersion();
		
		HashMap<String, List<String>> commitMessagesAndTouchedFilesForEachRevision = new HashMap<String, List<String>>();
		List<String> touchedFilesRev1 = new ArrayList<String>();
		touchedFilesRev1.add("class1blabla");
		
		commitMessagesAndTouchedFilesForEachRevision.put("this is a bug 10001", touchedFilesRev1);
		
		Mockito.doReturn(commitMessagesAndTouchedFilesForEachRevision).when(scmReader).getCommitMessagesAndTouchedFilesForEachRevision("someBranch");
		
		
		Mockito.doReturn(500).when(sonarReader).getSizeOfClass("20141001", "class1");

		Mockito.doNothing().when(sonarRunner).runSonar("v1");
		
		Analyzer testAna = new Analyzer(sonarReader, issueTrackerReader, scmReader, sonarRunner);
		
		HashMap<String, HashMap<String, Integer>> expectedTable = new HashMap<String, HashMap<String,Integer>>();
		
		HashMap<String, Integer> class1v1_row = new HashMap<String, Integer>();
		class1v1_row.put("numberDefects", 0);
		class1v1_row.put("locTouched", 0);
		class1v1_row.put("size", 500);
		class1v1_row.put("r1", 1);
		
		expectedTable.put("class1_v1", class1v1_row);
		
		HashMap<String, HashMap<String, Integer>> actualTable = testAna.getTechnicalDebtTable();
		Assert.assertEquals(expectedTable, actualTable);
	}
	
	@Test 
	public void getTechnicalDebtTableTest() throws IOException, ConfigurationException, InvalidRemoteException, TransportException, GitAPIException, RedmineException, VersionIdentifierConflictException, ConfiguredVersionNotExistInSonarException, UnequalNumberOfVersionsException, KeyNotFoundException
	{
		List<String> allResources = new ArrayList<String>();
		allResources.add("class1");
		allResources.add("class2");
		
		List<String> allRules = new ArrayList<String>();
		allRules.add("r1");
		allRules.add("r2");
		
		List<String> sonarVersions = new ArrayList<String>();
		sonarVersions.add("v1");
		sonarVersions.add("v2");
		
		List<String> issueTrackerVersions = new ArrayList<String>();
		issueTrackerVersions.add("v1");
		issueTrackerVersions.add("v2");
		
		List<String> scmVersions = new ArrayList<String>();
		scmVersions.add("v1");
		scmVersions.add("v2");
		
		Mockito.doReturn(scmVersions).when(scmReader).getConfiguredVersions();
		Mockito.doReturn(issueTrackerVersions).when(issueTrackerReader).getConfiguredVersions();
		Mockito.doReturn(sonarVersions).when(sonarReader).getConfiguredVersions();
		
		Mockito.doReturn(allResources).when(sonarReader).getListOfAllResources();
		Mockito.doReturn(allRules).when(api).getListOfAllRules();
		Mockito.doReturn(0).when(scmReader).getNumberOfLOCtouched("v1", "0", "class1");
		Mockito.doReturn(150).when(scmReader).getNumberOfLOCtouched("v2", "v1", "class1");
		Mockito.doReturn(0).when(scmReader).getNumberOfLOCtouched("v1", "0", "class2");
		Mockito.doReturn(150).when(scmReader).getNumberOfLOCtouched("v2", "v1", "class2");
		Mockito.doReturn("someBranch").when(scmReader).getHeadBranch();
		
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
		
		Mockito.doReturn("20141001").when(sonarReader).getDateOfLastSonarAnalyse("v1");
		Mockito.doReturn("20141002").when(sonarReader).getDateOfLastSonarAnalyse("v2");
		
		Mockito.doReturn(violationsPerRuleClass1V1).when(sonarReader).getNumberOfViolationsPerRule("20141001", "class1");
		Mockito.doReturn(violationsPerRuleClass1V2).when(sonarReader).getNumberOfViolationsPerRule("20141002", "class1");
		Mockito.doReturn(violationsPerRuleClass2V1).when(sonarReader).getNumberOfViolationsPerRule("20141001", "class2");
		Mockito.doReturn(violationsPerRuleClass2V2).when(sonarReader).getNumberOfViolationsPerRule("20141002", "class2");
		
		HashMap<Integer, String> mapOfBugsRelatedToTheirVersion = new HashMap<Integer, String>();
		mapOfBugsRelatedToTheirVersion.put(10001, "v1");
		mapOfBugsRelatedToTheirVersion.put(10002, "v1");
		mapOfBugsRelatedToTheirVersion.put(10003, "v1");
		mapOfBugsRelatedToTheirVersion.put(10004, "v2");
		mapOfBugsRelatedToTheirVersion.put(10005, "v2");
		
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
		
		Mockito.doReturn(commitMessagesAndTouchedFilesForEachRevision).when(scmReader).getCommitMessagesAndTouchedFilesForEachRevision("someBranch");
		
		Mockito.doReturn(500).when(sonarReader).getSizeOfClass("20141001", "class1");
		Mockito.doReturn(550).when(sonarReader).getSizeOfClass("20141002", "class1");
		Mockito.doReturn(500).when(sonarReader).getSizeOfClass("20141001", "class2");
		Mockito.doReturn(550).when(sonarReader).getSizeOfClass("20141002", "class2");

		Mockito.doNothing().when(sonarRunner).runSonar("v1");
		Mockito.doNothing().when(sonarRunner).runSonar("v2");
		
		Analyzer testAna = new Analyzer(sonarReader, issueTrackerReader, scmReader, sonarRunner);
		
		HashMap<String, HashMap<String, Integer>> expectedTable = new HashMap<String, HashMap<String,Integer>>();
		
		HashMap<String, Integer> class1v1_row = new HashMap<String, Integer>();
		class1v1_row.put("numberDefects", 1);
		class1v1_row.put("locTouched", 0);
		class1v1_row.put("size", 500);
		class1v1_row.put("r1", 1);
		class1v1_row.put("r2", 0);
		
		HashMap<String, Integer> class1v2_row = new HashMap<String, Integer>();
		class1v2_row.put("numberDefects", 1);
		class1v2_row.put("locTouched", 150);
		class1v2_row.put("size", 550);
		class1v2_row.put("r1", 5);
		class1v2_row.put("r2", 7);
		
		HashMap<String, Integer> class2v1_row = new HashMap<String, Integer>();
		class2v1_row.put("numberDefects", 3);
		class2v1_row.put("locTouched", 0);
		class2v1_row.put("size", 500);
		class2v1_row.put("r1", 13);
		class2v1_row.put("r2", 9);

		HashMap<String, Integer> class2v2_row = new HashMap<String, Integer>();
		class2v2_row.put("numberDefects", 2);
		class2v2_row.put("locTouched", 150);
		class2v2_row.put("size", 550);
		class2v2_row.put("r1", 0);
		class2v2_row.put("r2", 7);
		
		expectedTable.put("class1_v1", class1v1_row);
		expectedTable.put("class1_v2", class1v2_row);
		expectedTable.put("class2_v1", class2v1_row);
		expectedTable.put("class2_v2", class2v2_row);
		
		HashMap<String, HashMap<String, Integer>> actualTable = testAna.getTechnicalDebtTable();
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
