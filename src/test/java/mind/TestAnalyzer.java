package mind;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	@Test
	public void getTechnicalDebtRowForRevisionTest_successfull() throws IOException, NoSuchBranchException
	{
		Entry<String, String> version1 = new AbstractMap.SimpleEntry<String, String>("1","201405");
		Entry<String, String> version0 = new AbstractMap.SimpleEntry<String, String>("0","201305");
		Mockito.doReturn(100).when(sonarReader).getSizeOfClass(version1.getValue(), "someClass");
		Mockito.doReturn(50).when(scmReader).getNumberOfLOCtouched(version1.getKey(), version0.getKey(), "someClass");
		HashMap<String, Integer> violationsPerRule = new HashMap<String, Integer>();
		violationsPerRule.put("r1", 1);
		violationsPerRule.put("r2", 0);
		
		Mockito.doReturn(violationsPerRule).when(sonarReader).getNumberOfViolationsPerRule("201405", "someClass");
		
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader);
		HashMap<String, Integer> analyzedRow = ana.getTechnicalDebtRowForRevision(version1, version0, "someClass", 2);
		
		HashMap<String,Integer> expectedRow = new HashMap<String, Integer>();
		expectedRow.put("r1", 1);
		expectedRow.put("r2", 0);
		expectedRow.put("size", 100);
		expectedRow.put("numberDefects", 2);
		expectedRow.put("locTouched", 50);
		
		Assert.assertEquals(expectedRow, analyzedRow);
	}
	
	@Test
	public void getTechnicalDebtRowForRevisionTest_noViolations() throws IOException, NoSuchBranchException
	{
		Entry<String, String> version1 = new AbstractMap.SimpleEntry<String, String>("v1","201405");
		Entry<String, String> version0 = new AbstractMap.SimpleEntry<String, String>("v0","201305");
		Mockito.doReturn(100).when(sonarReader).getSizeOfClass(version1.getValue(), "someClass");
		Mockito.doReturn(50).when(scmReader).getNumberOfLOCtouched(version1.getKey(), version0.getKey(), "someClass");
			
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader);
		

		HashMap<String, Integer> analyzedRow = ana.getTechnicalDebtRowForRevision(version1, version0, "someClass",2);
		
		HashMap<String,Integer> expectedRow = new HashMap<String, Integer>();
		expectedRow.put("size", 100);
		expectedRow.put("numberDefects", 2);
		expectedRow.put("locTouched", 50);
		
		Assert.assertEquals(expectedRow, analyzedRow);
	}
	
	@Test(expected = VersionIdentifierConflictException.class)
	public void getTechnicalDebtTableTest_versionInSonarAndIssueTrackerAreNotEqual() throws IOException, ConfigurationException, InvalidRemoteException, TransportException, GitAPIException, RedmineException, VersionIdentifierConflictException
	{
		List<String> allResources = new ArrayList<String>();
		allResources.add("class1");
		
		List<String> allRules = new ArrayList<String>();
		allRules.add("r1");
		
		List<AbstractMap.SimpleEntry<String, String>> allVersions = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
		Entry<String, String> version1 = new AbstractMap.SimpleEntry<String, String>("v1-stable","20141001");
		Entry<String, String> time0 = new AbstractMap.SimpleEntry<String, String>("0","0");
		allVersions.add(new AbstractMap.SimpleEntry<String, String>(time0.getKey(), time0.getValue()));
		allVersions.add(new AbstractMap.SimpleEntry<String, String>(version1.getKey(), version1.getValue()));
		
		
		Mockito.doReturn(allResources).when(api).getListOfAllResources();
		Mockito.doReturn(allVersions).when(api).getMapOfAllVersionsOfProject();
		Mockito.doReturn(allRules).when(api).getListOfAllRules();
		Mockito.doReturn(0).when(scmReader).getNumberOfLOCtouched(version1.getKey(), time0.getKey(), "class1");
		Mockito.doReturn("someBranch").when(scmReader).getHeadBranch();
		
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

		Analyzer testAna = new Analyzer(sonarReader, issueTrackerReader, scmReader);
		
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
	public void getTechnicalDebtTableTest_resourcesFromCommitAreNotTheSameAsInSonar() throws IOException, ConfigurationException, InvalidRemoteException, TransportException, GitAPIException, RedmineException, VersionIdentifierConflictException
	{
		List<String> allResources = new ArrayList<String>();
		allResources.add("class1");
		
		List<String> allRules = new ArrayList<String>();
		allRules.add("r1");
		
		List<AbstractMap.SimpleEntry<String, String>> allVersions = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
		Entry<String, String> version1 = new AbstractMap.SimpleEntry<String, String>("v1","20141001");
		Entry<String, String> time0 = new AbstractMap.SimpleEntry<String, String>("0","0");
		allVersions.add(new AbstractMap.SimpleEntry<String, String>(time0.getKey(), time0.getValue()));
		allVersions.add(new AbstractMap.SimpleEntry<String, String>(version1.getKey(), version1.getValue()));
		
		
		Mockito.doReturn(allResources).when(sonarReader).getListOfAllResources();
		Mockito.doReturn(allVersions).when(sonarReader).getMapOfAllVersionsOfProject();
		Mockito.doReturn(allRules).when(api).getListOfAllRules();
		Mockito.doReturn(0).when(scmReader).getNumberOfLOCtouched(version1.getKey(), time0.getKey(), "class1");
		Mockito.doReturn("someBranch").when(scmReader).getHeadBranch();
		
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

		Analyzer testAna = new Analyzer(sonarReader, issueTrackerReader, scmReader);
		
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
	public void getTechnicalDebtTableTest() throws IOException, ConfigurationException, InvalidRemoteException, TransportException, GitAPIException, RedmineException, VersionIdentifierConflictException
	{
		List<String> allResources = new ArrayList<String>();
		allResources.add("class1");
		allResources.add("class2");
		
		List<String> allRules = new ArrayList<String>();
		allRules.add("r1");
		allRules.add("r2");
		
		List<AbstractMap.SimpleEntry<String, String>> allVersions = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
		Entry<String, String> version1 = new AbstractMap.SimpleEntry<String, String>("v1","20141001");
		Entry<String, String> version2 = new AbstractMap.SimpleEntry<String, String>("v2","20141002");
		Entry<String, String> time0 = new AbstractMap.SimpleEntry<String, String>("0","0");
		allVersions.add(new AbstractMap.SimpleEntry<String, String>(time0.getKey(), time0.getValue()));
		allVersions.add(new AbstractMap.SimpleEntry<String, String>(version1.getKey(), version1.getValue()));
		allVersions.add(new AbstractMap.SimpleEntry<String, String>(version2.getKey(), version2.getValue()));
		
		
		Mockito.doReturn(allResources).when(sonarReader).getListOfAllResources();
		Mockito.doReturn(allVersions).when(sonarReader).getMapOfAllVersionsOfProject();
		Mockito.doReturn(allRules).when(api).getListOfAllRules();
		Mockito.doReturn(0).when(scmReader).getNumberOfLOCtouched(version1.getKey(), time0.getKey(), "class1");
		Mockito.doReturn(150).when(scmReader).getNumberOfLOCtouched(version2.getKey(), version1.getKey(), "class1");
		Mockito.doReturn(0).when(scmReader).getNumberOfLOCtouched(version1.getKey(), time0.getKey(), "class2");
		Mockito.doReturn(150).when(scmReader).getNumberOfLOCtouched(version2.getKey(), version1.getKey(), "class2");
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

		Analyzer testAna = new Analyzer(sonarReader, issueTrackerReader, scmReader);
		
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
	public void getMapOfNumberOfDefectsRelatedToResourceTest_successfull() throws RedmineException, NoHeadException, IOException, GitAPIException, VersionIdentifierConflictException
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
		
		List<AbstractMap.SimpleEntry<String, String>> versionMap = new ArrayList<AbstractMap.SimpleEntry<String,String>>();
		AbstractMap.SimpleEntry<String, String> version1 = new AbstractMap.SimpleEntry<String, String>("1.0", "201415");
		AbstractMap.SimpleEntry<String, String> version2 = new AbstractMap.SimpleEntry<String, String>("1.1", "201416");
		
		versionMap.add(version1);
		versionMap.add(version2);
		
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
		
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader);
		Assert.assertEquals(expectedMapOfNumberOfDefectsRelatedToResource, ana.getMapOfNumberOfDefectsRelatedToResource(versionMap,resources, "someBranch"));
	}
	
	@Test
	public void getMapOfNumberOfDefectsRelatedToResourceTest_referenceOfBugInMultipleMessages() throws RedmineException, NoHeadException, IOException, GitAPIException, VersionIdentifierConflictException
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
		
		List<AbstractMap.SimpleEntry<String, String>> versionMap = new ArrayList<AbstractMap.SimpleEntry<String,String>>();
		AbstractMap.SimpleEntry<String, String> version1 = new AbstractMap.SimpleEntry<String, String>("1.0", "201415");
		
		versionMap.add(version1);
		
		HashMap<String, HashMap<String, Integer>> expectedMapOfNumberOfDefectsRelatedToResource = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, Integer> version10NumberOfDefectsRelatedToResource = new HashMap<String, Integer>();
		version10NumberOfDefectsRelatedToResource.put("project:path/file1", 1);
		version10NumberOfDefectsRelatedToResource.put("project:path/file2", 2);
		version10NumberOfDefectsRelatedToResource.put("project:path/file3", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file4", 2);
		version10NumberOfDefectsRelatedToResource.put("project:path/file5", 0);
		expectedMapOfNumberOfDefectsRelatedToResource.put("1.0", version10NumberOfDefectsRelatedToResource);
		
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader);
		Assert.assertEquals(expectedMapOfNumberOfDefectsRelatedToResource, ana.getMapOfNumberOfDefectsRelatedToResource(versionMap,resources, "someBranch"));
	}
	
	@Test
	public void getMapOfNumberOfDefectsRelatedToResourceTest_VersionOfRedmineAndSonarAreNotEqual() throws RedmineException, NoHeadException, IOException, GitAPIException, VersionIdentifierConflictException
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
		
		List<AbstractMap.SimpleEntry<String, String>> versionMap = new ArrayList<AbstractMap.SimpleEntry<String,String>>();
		AbstractMap.SimpleEntry<String, String> version1 = new AbstractMap.SimpleEntry<String, String>("1.0", "201415");
		
		versionMap.add(version1);
		
		HashMap<String, HashMap<String, Integer>> expectedMapOfNumberOfDefectsRelatedToResource = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, Integer> version10NumberOfDefectsRelatedToResource = new HashMap<String, Integer>();
		version10NumberOfDefectsRelatedToResource.put("project:path/file1", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file2", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file3", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file4", 0);
		version10NumberOfDefectsRelatedToResource.put("project:path/file5", 0);
		expectedMapOfNumberOfDefectsRelatedToResource.put("1.0", version10NumberOfDefectsRelatedToResource);
		
		Analyzer ana = new Analyzer(sonarReader, issueTrackerReader, scmReader);
		Assert.assertEquals(expectedMapOfNumberOfDefectsRelatedToResource, ana.getMapOfNumberOfDefectsRelatedToResource(versionMap,resources, "someBranch"));
	}
}
