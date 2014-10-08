package mind;

import interfaces.SonarWebApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import exceptions.ResourceNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class SonarReaderImplTest {

	@Mock
	SonarWebApi api;
	
	@Test
	public void test_getNumberOfViolationsPerRule_success() throws IOException
	{
		List<String> rules = new ArrayList<String>();
		rules.add("r1");
		rules.add("r2");
		Mockito.doReturn(rules).when(api).getListOfAllRules();
		
		List<String> resources = new ArrayList<String>();
		resources.add("classname");
		Mockito.doReturn(resources).when(api).getListOfAllResources();
		
		
		Mockito.doReturn(50).when(api).getNumberOfViolationsOfSpecificRuleForResource("classname", "r1");
		Mockito.doReturn(25).when(api).getNumberOfViolationsOfSpecificRuleForResource("classname", "r2");

		SonarReaderImpl sreader = new SonarReaderImpl(api);
		
		HashMap<String, Integer> expectedViolationsPerRule= new HashMap<String, Integer>();
		expectedViolationsPerRule.put("r1", 50);
		expectedViolationsPerRule.put("r2", 25);
		
		Assert.assertEquals(expectedViolationsPerRule, sreader.getNumberOfViolationsPerRule("classname"));
	}
	
	@Test
	public void test_getNumberOfViolationsPerRule_noViolations() throws IOException
	{
		
		List<String> rules = new ArrayList<String>();
		rules.add("r1");
		Mockito.doReturn(rules).when(api).getListOfAllRules();
		
		List<String> resources = new ArrayList<String>();
		resources.add("classname");
		Mockito.doReturn(resources).when(api).getListOfAllResources();
		
		
		Mockito.doReturn(0).when(api).getNumberOfViolationsOfSpecificRuleForResource("classname", "r1");
		
			
		SonarReaderImpl sreader = new SonarReaderImpl(api);
		
		HashMap<String, Integer> expectedViolationsPerRule= new HashMap<String, Integer>();
		expectedViolationsPerRule.put("r1", 0);
		
		Assert.assertEquals(expectedViolationsPerRule, sreader.getNumberOfViolationsPerRule("classname"));
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void test_getNumberOfViolationsPerRule_noSuchResource() throws IOException
	{
		
		List<String> rules = new ArrayList<String>();
		rules.add("r1");
		Mockito.doReturn(rules).when(api).getListOfAllRules();
		List<String> resources = new ArrayList<String>();
		resources.add("classname");
		Mockito.doReturn(resources).when(api).getListOfAllResources();
		
		
		Mockito.doReturn(50).when(api).getNumberOfViolationsOfSpecificRuleForResource("classname", "r1");
		Mockito.doReturn(25).when(api).getNumberOfViolationsOfSpecificRuleForResource("classname", "r2");

		Mockito.doThrow(ResourceNotFoundException.class).when(api).getNumberOfViolationsOfSpecificRuleForResource("classname", "r1");
		
		SonarReaderImpl sreader = new SonarReaderImpl(api);
		
		HashMap<String, Integer> expectedViolationsPerRule= new HashMap<String, Integer>();
		expectedViolationsPerRule.put("r1", 0);
		
		Assert.assertEquals(expectedViolationsPerRule, sreader.getNumberOfViolationsPerRule("classname"));
	}
}
