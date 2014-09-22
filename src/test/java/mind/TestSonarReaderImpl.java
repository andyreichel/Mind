package mind;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mind.RuleNotFoundException;
import mind.SonarReaderImpl;
import mind.SonarWebApi;

import org.junit.Assert;
import org.junit.Test;

public class TestSonarReaderImpl {

	@Test
	public void getNumberOfViolationsPerRuleTest() throws IOException
	{
		SonarWebApi api = new SonarWebApi(){
			public List<String> getListOfAllRules() throws IOException {
				List<String> rules = new ArrayList<String>();
				rules.add("r1");
				rules.add("r2");
				return rules;
			}

			public List<String> getListOfAllResources() throws IOException {
				List<String> resources = new ArrayList<String>();
				resources.add("classname");
				resources.add("class2");
				return resources;
			}

			public int getNumberOfViolationsOfSpecificRuleForResource(String version, String resourceKey, String rule) throws IOException {
				
				if(rule.equals("r1"))
					return 50;
				if(rule.equals("r2"))
					return 25;
				throw new RuleNotFoundException();
			}

			public List<AbstractMap.SimpleEntry<String, String>> getMapOfAllVersionsOfProject() {
				return null;
			}

			public int getSizeOfResource(String resourceKey, String versionDate) {
				// TODO Auto-generated method stub
				return 0;
			}};
			
		SonarReaderImpl sreader = new SonarReaderImpl(api);
		
		HashMap<String, Integer> expectedViolationsPerRule= new HashMap<String, Integer>();
		expectedViolationsPerRule.put("r1", 50);
		expectedViolationsPerRule.put("r2", 25);
		
		Assert.assertEquals(expectedViolationsPerRule, sreader.getNumberOfViolationsPerRule("hey", "classname"));
	}
	
	@Test
	public void getNumberOfViolationsPerRuleTest_noViolations() throws IOException
	{
		SonarWebApi api = new SonarWebApi(){
			public List<String> getListOfAllRules() throws IOException {
				List<String> rules = new ArrayList<String>();
				rules.add("r1");
				return rules;
			}

			public List<String> getListOfAllResources() throws IOException {
				List<String> resources = new ArrayList<String>();
				resources.add("classname");
				return resources;
			}

			public int getNumberOfViolationsOfSpecificRuleForResource(String version, String resourceKey, String rule) throws IOException {
				
				if(rule.equals("r1"))
					return 0;
				throw new RuleNotFoundException();
			}

			public List<AbstractMap.SimpleEntry<String, String>> getMapOfAllVersionsOfProject() {
				return null;
			}

			public int getSizeOfResource(String resourceKey, String versionDate) {
				return 0;
			}};
			
		SonarReaderImpl sreader = new SonarReaderImpl(api);
		
		HashMap<String, Integer> expectedViolationsPerRule= new HashMap<String, Integer>();
		expectedViolationsPerRule.put("r1", 0);
		
		Assert.assertEquals(expectedViolationsPerRule, sreader.getNumberOfViolationsPerRule("hey", "classname"));
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void getNumberOfViolationsPerRuleTest_noSuchResource() throws IOException
	{
		SonarWebApi api = new SonarWebApi(){
			public List<String> getListOfAllRules() throws IOException {
				List<String> rules = new ArrayList<String>();
				rules.add("r1");
				return rules;
			}

			public List<String> getListOfAllResources() throws IOException {
				List<String> resources = new ArrayList<String>();
				resources.add("classname");
				return resources;
			}

			public int getNumberOfViolationsOfSpecificRuleForResource(String version, String resourceKey, String rule) throws IOException {
				
				throw new ResourceNotFoundException(resourceKey);
			}

			public List<AbstractMap.SimpleEntry<String, String>> getMapOfAllVersionsOfProject() {
				return null;
			}

			public int getSizeOfResource(String resourceKey, String versionDate) {
				return 0;
			}};
			
		SonarReaderImpl sreader = new SonarReaderImpl(api);
		
		HashMap<String, Integer> expectedViolationsPerRule= new HashMap<String, Integer>();
		expectedViolationsPerRule.put("r1", 0);
		
		Assert.assertEquals(expectedViolationsPerRule, sreader.getNumberOfViolationsPerRule("hey", "classname"));
	}
}
