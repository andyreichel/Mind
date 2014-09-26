package mind;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestSonarReaderImpl {

	@Mock
	SonarWebApi api;
	
	@Test
	public void getNumberOfViolationsPerRuleTest() throws IOException
	{
		List<String> rules = new ArrayList<String>();
		rules.add("r1");
		rules.add("r2");
		Mockito.doReturn(rules).when(api).getListOfAllRules();
		
		List<String> resources = new ArrayList<String>();
		resources.add("classname");
		Mockito.doReturn(resources).when(api).getListOfAllResources();
		
		
		Mockito.doReturn(50).when(api).getNumberOfViolationsOfSpecificRuleForResource("hey", "classname", "r1");
		Mockito.doReturn(25).when(api).getNumberOfViolationsOfSpecificRuleForResource("hey", "classname", "r2");

		SonarReaderImpl sreader = new SonarReaderImpl(api);
		
		HashMap<String, Integer> expectedViolationsPerRule= new HashMap<String, Integer>();
		expectedViolationsPerRule.put("r1", 50);
		expectedViolationsPerRule.put("r2", 25);
		
		Assert.assertEquals(expectedViolationsPerRule, sreader.getNumberOfViolationsPerRule("hey", "classname"));
	}
	
	@Test
	public void getNumberOfViolationsPerRuleTest_noViolations() throws IOException
	{
		
		List<String> rules = new ArrayList<String>();
		rules.add("r1");
		Mockito.doReturn(rules).when(api).getListOfAllRules();
		
		List<String> resources = new ArrayList<String>();
		resources.add("classname");
		Mockito.doReturn(resources).when(api).getListOfAllResources();
		
		
		Mockito.doReturn(0).when(api).getNumberOfViolationsOfSpecificRuleForResource("hey", "classname", "r1");
		
			
		SonarReaderImpl sreader = new SonarReaderImpl(api);
		
		HashMap<String, Integer> expectedViolationsPerRule= new HashMap<String, Integer>();
		expectedViolationsPerRule.put("r1", 0);
		
		Assert.assertEquals(expectedViolationsPerRule, sreader.getNumberOfViolationsPerRule("hey", "classname"));
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void getNumberOfViolationsPerRuleTest_noSuchResource() throws IOException
	{
		
		List<String> rules = new ArrayList<String>();
		rules.add("r1");
		Mockito.doReturn(rules).when(api).getListOfAllRules();
		
		List<String> resources = new ArrayList<String>();
		resources.add("classname");
		Mockito.doReturn(resources).when(api).getListOfAllResources();
		
		
		Mockito.doReturn(50).when(api).getNumberOfViolationsOfSpecificRuleForResource("hey", "classname", "r1");
		Mockito.doReturn(25).when(api).getNumberOfViolationsOfSpecificRuleForResource("hey", "classname", "r2");

		Mockito.doThrow(ResourceNotFoundException.class).when(api).getNumberOfViolationsOfSpecificRuleForResource("hey", "classname", "r1");
		
		SonarReaderImpl sreader = new SonarReaderImpl(api);
		
		HashMap<String, Integer> expectedViolationsPerRule= new HashMap<String, Integer>();
		expectedViolationsPerRule.put("r1", 0);
		
		Assert.assertEquals(expectedViolationsPerRule, sreader.getNumberOfViolationsPerRule("hey", "classname"));
	}
	
	@Test
	public void getMapOfAllConfiguredVersionsOfProjectTest_successfull() throws IOException, ConfiguredVersionNotExistInSonarException
	{
		LinkedHashMap<String, String> allVersions = new LinkedHashMap<String, String>();
		allVersions.put("version1", "2014");
		
		List<String> allConfiguredVersions = new ArrayList<String>();
		allConfiguredVersions.add("version1");
		
		Mockito.doReturn(allConfiguredVersions).when(api).getConfiguredVersions();
		Mockito.doReturn(allVersions).when(api).getMapOfAllVersionsOfProject();
		
		LinkedHashMap<String, String> expectedMapOfAllConfiguredVersionsOfProject = new LinkedHashMap<String, String>();
		expectedMapOfAllConfiguredVersionsOfProject.put("version1", "2014");
		
		SonarReaderImpl sreader = new SonarReaderImpl(api);
		Assert.assertEquals(expectedMapOfAllConfiguredVersionsOfProject, sreader.getMapOfAllConfiguredVersionsOfProject());
	}
	
	@Test
	public void getMapOfAllConfiguredVersionsOfProjectTest_onlySubsetConfigured() throws IOException, ConfiguredVersionNotExistInSonarException
	{
		LinkedHashMap<String, String> allVersions = new LinkedHashMap<String, String>();
		allVersions.put("version1", "2014");
		allVersions.put("version2", "2014");
		
		List<String> allConfiguredVersions = new ArrayList<String>();
		allConfiguredVersions.add("version1");
		
		Mockito.doReturn(allConfiguredVersions).when(api).getConfiguredVersions();
		Mockito.doReturn(allVersions).when(api).getMapOfAllVersionsOfProject();
		
		LinkedHashMap<String, String> expectedMapOfAllConfiguredVersionsOfProject = new LinkedHashMap<String, String>();
		expectedMapOfAllConfiguredVersionsOfProject.put("version1", "2014");
		
		SonarReaderImpl sreader = new SonarReaderImpl(api);
		Assert.assertEquals(expectedMapOfAllConfiguredVersionsOfProject, sreader.getMapOfAllConfiguredVersionsOfProject());
	}
	
	@Test(expected=ConfiguredVersionNotExistInSonarException.class)
	public void getMapOfAllConfiguredVersionsOfProjectTest_configuredVersionsDoNotExistInSonar() throws IOException, ConfiguredVersionNotExistInSonarException
	{
		LinkedHashMap<String, String> allVersions = new LinkedHashMap<String, String>();
		allVersions.put("version1", "2014");
		
		List<String> allConfiguredVersions = new ArrayList<String>();
		allConfiguredVersions.add("blubb");
		
		Mockito.doReturn(allConfiguredVersions).when(api).getConfiguredVersions();
		Mockito.doReturn(allVersions).when(api).getMapOfAllVersionsOfProject();
		
		
		SonarReaderImpl sreader = new SonarReaderImpl(api);
		sreader.getMapOfAllConfiguredVersionsOfProject();
	}
}
