package mind;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonParserForSonarApiResponses {
	@Test
	public void getMapOfAllVersions_successFull()
	{
		String testJson = "[{\"id\":\"36\",\"rk\":\"Typo3\",\"n\":\"4.0\",\"c\":\"Version\",\"dt\":\"2014-09-11T10:11:40-0400\"},"
				+ "{\"id\":\"35\",\"rk\":\"Typo3\",\"n\":\"3.8\",\"c\":\"Version\",\"dt\":\"2014-09-11T10:11:07-0400\"},"
				+ "{\"id\":\"34\",\"rk\":\"Typo3\",\"n\":\"3.7\",\"c\":\"Version\",\"dt\":\"2014-09-11T10:10:37-0400\"},"
				+ "{\"id\":\"33\",\"rk\":\"Typo3\",\"n\":\"3.6\",\"c\":\"Version\",\"dt\":\"2014-09-11T10:10:08-0400\"},"
				+ "{\"id\":\"31\",\"rk\":\"Typo3\",\"n\":\"1.0\",\"c\":\"Version\",\"dt\":\"2014-09-10T15:38:12-0400\"}]";
		
		List<AbstractMap.SimpleEntry<String, String>> expectedMap = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
		expectedMap.add(new AbstractMap.SimpleEntry<String, String>("4.0", "2014-09-11T10:11:40-0400"));
		expectedMap.add(new AbstractMap.SimpleEntry<String, String>("3.8", "2014-09-11T10:11:07-0400"));
		expectedMap.add(new AbstractMap.SimpleEntry<String, String>("3.7", "2014-09-11T10:10:37-0400"));
		expectedMap.add(new AbstractMap.SimpleEntry<String, String>("3.6", "2014-09-11T10:10:08-0400"));
		expectedMap.add(new AbstractMap.SimpleEntry<String, String>("1.0", "2014-09-10T15:38:12-0400"));
		
		Assert.assertEquals(expectedMap, JsonParserForSonarApiResponses.getMapOfAllVersions(testJson));
	}
	
	@Test(expected=JSONException.class)
	public void getMapOfAllVersions_notValidJson()
	{
		String testJson = "gruetze";
		
		JsonParserForSonarApiResponses.getMapOfAllVersions(testJson);
	}
	
	@Test
	public void getMapOfAllVersions_emptyJson()
	{
		String testJson = "[]";
		
		List<AbstractMap.SimpleEntry<String, String>> expectedMap = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
		
		Assert.assertEquals(expectedMap, JsonParserForSonarApiResponses.getMapOfAllVersions(testJson));
	}
	
	@Test
	public void getMapOfAllVersions_oneElementOnly()
	{
		String testJson = "[{\"id\":\"36\",\"rk\":\"Typo3\",\"n\":\"4.0\",\"c\":\"Version\",\"dt\":\"2014-09-11T10:11:40-0400\"}]";
		
		List<AbstractMap.SimpleEntry<String, String>> expectedMap = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
		expectedMap.add(new AbstractMap.SimpleEntry<String, String>("4.0", "2014-09-11T10:11:40-0400"));
		
		Assert.assertEquals(expectedMap, JsonParserForSonarApiResponses.getMapOfAllVersions(testJson));
	}
	
	@Test
	public void getNlocTest_successfull()
	{
		String testJson = "[{\"cols\":[{\"metric\":\"ncloc\"}],\"cells\":[{\"d\":\"2014-09-11T10:11:40-0400\",\"v\":[109]}]}]";
		
		int expectedNloc=109;
		Assert.assertEquals(expectedNloc, JsonParserForSonarApiResponses.getNloc(testJson));
	}
	
	@Test(expected=JSONException.class)
	public void getNlocTest_notFoundMetric()
	{
		String testJson = "[{\"cols\":[{\"metric\":\"ncloc\"}],\"cells\":[]}]";
		
		JsonParserForSonarApiResponses.getNloc(testJson);
	}
	
	@Test(expected=JSONException.class)
	public void getNlocTest_invalidJson()
	{
		String testJson = "]";
		
		JsonParserForSonarApiResponses.getNloc(testJson);
	}
	
	@Test(expected=JSONException.class)
	public void getNlocTest_emptyJson()
	{
		String testJson = "[]";
		
		JsonParserForSonarApiResponses.getNloc(testJson);
	}
	
	@Test
	public void getNumberOfViolationsOfSpecificRuleForResourceTest()
	{
		String testJson = "[{\"cols\":[{\"metric\":\"violations\"}],\"cells\":[{\"d\":\"2014-09-11T10:11:40-0400\",\"v\":[28]}]}]";
		
		Assert.assertEquals(JsonParserForSonarApiResponses.getNumberOfViolationsOfSpecificRuleForResource(testJson), 28);
	}
}
