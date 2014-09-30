package mind;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonParserForSonarApiResponses {
	@Test
	public void getDateOfLastSonarAnalyse_successFull()
	{
		String testJson = "[{\"id\":\"36\",\"rk\":\"Typo3\",\"n\":\"4.0\",\"c\":\"Version\",\"dt\":\"2014-09-11T10:11:40-0400\"},"
						+ "{\"id\":\"36\",\"rk\":\"Typo3\",\"n\":\"4.1\",\"c\":\"Version\",\"dt\":\"2014-09-11T10:11:40-0400\"}]";
		
		
		Assert.assertEquals("2014-09-11T10:11:40-0400", JsonParserForSonarApiResponses.getDateOfLastSonarAnalyse("4.0", testJson));
	}
	
	@Test(expected=JSONException.class)
	public void getMapOfAllVersions_notValidJson()
	{
		String testJson = "gruetze";
		
		JsonParserForSonarApiResponses.getDateOfLastSonarAnalyse("1.0", testJson);
	}
	
	@Test(expected=NoSuchSonarVersionException.class)
	public void getMapOfAllVersions_emptyJson() throws NoSuchSonarVersionException
	{
		String testJson = "[]";
		
		try
		{
			JsonParserForSonarApiResponses.getDateOfLastSonarAnalyse("1.0", testJson);	
		}catch(JSONException e)
		{
			throw new NoSuchSonarVersionException("No version could be extracted", e);
		}
		
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
