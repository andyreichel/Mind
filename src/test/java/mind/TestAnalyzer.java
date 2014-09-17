package mind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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
	public void getTechnicalDebtRowForRevisionTest_successfull() throws IOException
	{
		Mockito.doReturn(100).when(sonarReader).getSizeOfClass("1", "someClass");
		Mockito.doReturn(50).when(scmReader).getNumberOfLOCtouched("1", "someClass");
		Mockito.doReturn(2).when(scmReader).getNumberOfDefectsRelatedToClass("1", "someClass", issueTrackerReader);
		HashMap<String, Integer> violationsPerRule = new HashMap<String, Integer>();
		violationsPerRule.put("r1", 1);
		violationsPerRule.put("r2", 0);
		
		Mockito.doReturn(violationsPerRule).when(sonarReader).getNumberOfViolationsPerRule("1", "someClass");
		
		Analyzer ana = new Analyzer(sonarReader, api, issueTrackerReader, scmReader);
		HashMap<String, Integer> analyzedRow = ana.getTechnicalDebtRowForRevision("1", "someClass");
		
		HashMap<String,Integer> expectedRow = new HashMap<String, Integer>();
		expectedRow.put("r1", 1);
		expectedRow.put("r2", 0);
		expectedRow.put("size", 100);
		expectedRow.put("numberDefects", 2);
		expectedRow.put("locTouched", 50);
		
		Assert.assertEquals(expectedRow, analyzedRow);
	}
	
	@Test
	public void getTechnicalDebtRowForRevisionTest_noViolations() throws IOException
	{
		Mockito.doReturn(100).when(sonarReader).getSizeOfClass("1", "someClass");
		Mockito.doReturn(50).when(scmReader).getNumberOfLOCtouched("1", "someClass");
		Mockito.doReturn(2).when(scmReader).getNumberOfDefectsRelatedToClass("1", "someClass", issueTrackerReader);
			
		Analyzer ana = new Analyzer(sonarReader, api, issueTrackerReader, scmReader);
		HashMap<String, Integer> analyzedRow = ana.getTechnicalDebtRowForRevision("1", "someClass");
		
		HashMap<String,Integer> expectedRow = new HashMap<String, Integer>();
		expectedRow.put("size", 100);
		expectedRow.put("numberDefects", 2);
		expectedRow.put("locTouched", 50);
		
		Assert.assertEquals(expectedRow, analyzedRow);
	}
	
	@Test 
	public void getTechnicalDebtTableTest() throws IOException, ConfigurationException, InvalidRemoteException, TransportException, GitAPIException
	{
		List<String> allResources = new ArrayList<String>();
		allResources.add("class1");
		allResources.add("class2");
		
		List<String> allRules = new ArrayList<String>();
		allRules.add("r1");
		allRules.add("r2");
		
		HashMap<String, String> versions = new HashMap<String, String>();
		
		versions.put("v1", "20141001");
		versions.put("v2", "20141002");
		
		Mockito.doReturn(allResources).when(api).getListOfAllResources();
		Mockito.doReturn(versions).when(api).getMapOfAllVersionsOfProject();
		Mockito.doReturn(allRules).when(api).getListOfAllRules();
		Mockito.doReturn(100).when(scmReader).getNumberOfLOCtouched("20141001", "class1");
		Mockito.doReturn(150).when(scmReader).getNumberOfLOCtouched("20141002", "class1");
		Mockito.doReturn(100).when(scmReader).getNumberOfLOCtouched("20141001", "class2");
		Mockito.doReturn(150).when(scmReader).getNumberOfLOCtouched("20141002", "class2");
		
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
		
		Mockito.doReturn(5).when(scmReader).getNumberOfDefectsRelatedToClass("20141001", "class1", issueTrackerReader);
		Mockito.doReturn(7).when(scmReader).getNumberOfDefectsRelatedToClass("20141002", "class1", issueTrackerReader);
		Mockito.doReturn(5).when(scmReader).getNumberOfDefectsRelatedToClass("20141001", "class2", issueTrackerReader);
		Mockito.doReturn(3).when(scmReader).getNumberOfDefectsRelatedToClass("20141002", "class2", issueTrackerReader);
		
		Mockito.doReturn(500).when(sonarReader).getSizeOfClass("20141001", "class1");
		Mockito.doReturn(550).when(sonarReader).getSizeOfClass("20141002", "class1");
		Mockito.doReturn(500).when(sonarReader).getSizeOfClass("20141001", "class2");
		Mockito.doReturn(550).when(sonarReader).getSizeOfClass("20141002", "class2");

		Analyzer testAna = new Analyzer(sonarReader, api, issueTrackerReader, scmReader);
		
		HashMap<String, HashMap<String, Integer>> expectedTable = new HashMap<String, HashMap<String,Integer>>();
		
		HashMap<String, Integer> class1v1_row = new HashMap<String, Integer>();
		class1v1_row.put("numberDefects", 5);
		class1v1_row.put("locTouched", 100);
		class1v1_row.put("size", 500);
		class1v1_row.put("r1", 1);
		class1v1_row.put("r2", 0);
		
		HashMap<String, Integer> class1v2_row = new HashMap<String, Integer>();
		class1v2_row.put("numberDefects", 7);
		class1v2_row.put("locTouched", 150);
		class1v2_row.put("size", 550);
		class1v2_row.put("r1", 5);
		class1v2_row.put("r2", 7);
		
		HashMap<String, Integer> class2v1_row = new HashMap<String, Integer>();
		class2v1_row.put("numberDefects", 5);
		class2v1_row.put("locTouched", 100);
		class2v1_row.put("size", 500);
		class2v1_row.put("r1", 13);
		class2v1_row.put("r2", 9);

		HashMap<String, Integer> class2v2_row = new HashMap<String, Integer>();
		class2v2_row.put("numberDefects", 3);
		class2v2_row.put("locTouched", 150);
		class2v2_row.put("size", 550);
		class2v2_row.put("r1", 0);
		class2v2_row.put("r2", 7);
		
		expectedTable.put("class1_v1", class1v1_row);
		expectedTable.put("class1_v2", class1v2_row);
		expectedTable.put("class2_v1", class2v1_row);
		expectedTable.put("class2_v2", class2v2_row);
		
		HashMap<String, HashMap<String, Integer>> actualTable = testAna.getTechnicalDebtTable();
		System.out.println(expectedTable.get("class1_v1").get("numberDefects"));
		Assert.assertEquals(expectedTable, actualTable);
	}
}
