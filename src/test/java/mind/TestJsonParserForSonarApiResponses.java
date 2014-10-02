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
		//String testJson = "[{\"cols\":[{\"metric\":\"ncloc\"}],\"cells\":[{\"d\":\"2014-09-11T10:11:40-0400\",\"v\":[109]}]}]";
		String testJson = "[{\"id\":5161,\"key\":\"Typo3:typo3/sysext/frontend/Classes/ContentObject/ContentObjectRenderer.php\",\"name\":\"ContentObjectRenderer.php\",\"scope\":\"FIL\",\"qualifier\":\"FIL\",\"date\":\"2014-10-01T11:55:44-0400\",\"creationDate\":\"2014-10-01T11:26:37-0400\",\"lname\":\"typo3/sysext/frontend/Classes/ContentObject/ContentObjectRenderer.php\",\"lang\":\"php\",\"msr\":[{\"key\":\"ncloc\",\"val\":4795.0,\"frmt_val\":\"4.795\"}]}]";
		
		int expectedNloc=4795;
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
		//String testJson = "[{\"cols\":[{\"metric\":\"violations\"}],\"cells\":[{\"d\":\"2014-09-11T10:11:40-0400\",\"v\":[28]}]}]";
		String testJson = "{\"maxResultsReached\":false,\"paging\":{\"pageIndex\":1,\"pageSize\":100,\"total\":2,\"fTotal\":\"2\",\"pages\":1},\"issues\":[{\"key\":\"0bd730dc-8ccf-4ab6-85e2-ba949ab40bf7\",\"component\":\"Typo3:index.php\",\"componentId\":593,\"project\":\"Typo3\",\"rule\":\"php:S1192\",\"status\":\"CLOSED\",\"resolution\":\"FIXED\",\"severity\":\"MINOR\",\"message\":\"Define a constant instead of duplicating this literal \\\"PATH_tslib\\\" 3 times.\",\"line\":54,\"debt\":\"10min\",\"creationDate\":\"2014-10-01T11:14:45-0400\",\"updateDate\":\"2014-10-01T11:26:10-0400\",\"fUpdateAge\":\"2 hours\",\"closeDate\":\"2014-10-01T11:26:10-0400\"},{\"key\":\"14b5bc08-8cc7-4de0-8138-547b0cd1bfc7\",\"component\":\"Typo3:index.php\",\"componentId\":593,\"project\":\"Typo3\",\"rule\":\"php:S1192\",\"status\":\"CLOSED\",\"resolution\":\"FIXED\",\"severity\":\"MINOR\",\"message\":\"Define a constant instead of duplicating this literal \\\"ORIG_PATH_TRANSLATED\\\" 4 times.\",\"line\":47,\"debt\":\"10min\",\"creationDate\":\"2014-10-01T11:14:45-0400\",\"updateDate\":\"2014-10-01T11:26:10-0400\",\"fUpdateAge\":\"2 hours\",\"closeDate\":\"2014-10-01T11:26:10-0400\"}],\"components\":[{\"key\":\"Typo3\",\"id\":11,\"qualifier\":\"TRK\",\"name\":\"Typo3\",\"longName\":\"Typo3\"},{\"key\":\"Typo3:index.php\",\"id\":593,\"qualifier\":\"FIL\",\"name\":\"index.php\",\"longName\":\"index.php\",\"path\":\"index.php\",\"projectId\":11,\"subProjectId\":11}],\"projects\":[{\"key\":\"Typo3\",\"id\":11,\"qualifier\":\"TRK\",\"name\":\"Typo3\",\"longName\":\"Typo3\"}],\"rules\":[{\"key\":\"php:S1192\",\"name\":\"String literals should not be duplicated\",\"desc\":\"<p>\\n  Duplicated string literals make the process of refactoring error-prone, since you must be sure to update all occurrences.\\n</p>\\n<p>\\n  On the other hand, constants can be referenced from many places, but only need to be updated in a single place.\\n</p>\\n\\n<h2>Noncompliant Code Example</h2>\\n<pre>\\nfunction run() {\\n  prepare('action1');          // Non-Compliant - 'action1' is duplicated 3 times\\n  execute('action1');\\n  release('action1');\\n}\\n</pre>\\n\\n<h2>Compliant Solution</h2>\\n<pre>\\nACTION_1 = 'action1';\\n\\nfunction run() {\\n  prepare(ACTION_1);\\n  execute(ACTION_1);\\n  release(ACTION_1);\\n}\\n</pre>\\n\\n<h2>Exceptions</h2>\\n<p>To prevent generating some false-positives, literals having less than 5 characters are excluded.</p>\",\"status\":\"READY\"}],\"users\":[]}";
		//String testJson = \"{\"maxResultsReached\":false,\"paging\":{\"pageIndex\":1,\"pageSize\":100,\"total\":2,\"fTotal\":\"2\",\"pages\":1},\"issues\":[{\"key\":\"0bd730dc-8ccf-4ab6-85e2-ba949ab40bf7\",\"component\":\"Typo3:index.php\",\"componentId\":593,\"project\":\"Typo3\",\"rule\":\"php:S1192\",\"status\":\"CLOSED\",\"resolution\":\"FIXED\",\"severity\":\"MINOR\",\"message\":\"Define a constant instead of duplicating this literal \\\"PATH_tslib\\\" 3 times.\",\"line\":54,\"debt\":\"10min\",\"creationDate\":\"2014-10-01T11:14:45-0400\",\"updateDate\":\"2014-10-01T11:26:10-0400\",\"fUpdateAge\":\"2 hours\",\"closeDate\":\"2014-10-01T11:26:10-0400\"},{\"key\":\"14b5bc08-8cc7-4de0-8138-547b0cd1bfc7\",\"component\":\"Typo3:index.php\",\"componentId\":593,\"project\":\"Typo3\",\"rule\":\"php:S1192\",\"status\":\"CLOSED\",\"resolution\":\"FIXED\",\"severity\":\"MINOR\",\"message\":\"Define a constant instead of duplicating this literal \\\"ORIG_PATH_TRANSLATED\\\" 4 times.\",\"line\":47,\"debt\":\"10min\",\"creationDate\":\"2014-10-01T11:14:45-0400\",\"updateDate\":\"2014-10-01T11:26:10-0400\",\"fUpdateAge\":\"2 hours\",\"closeDate\":\"2014-10-01T11:26:10-0400\"}],\"components\":[{\"key\":\"Typo3\",\"id\":11,\"qualifier\":\"TRK\",\"name\":\"Typo3\",\"longName\":\"Typo3\"},{\"key\":\"Typo3:index.php\",\"id\":593,\"qualifier\":\"FIL\",\"name\":\"index.php\",\"longName\":\"index.php\",\"path\":\"index.php\",\"projectId\":11,\"subProjectId\":11}],\"projects\":[{\"key\":\"Typo3\",\"id\":11,\"qualifier\":\"TRK\",\"name\":\"Typo3\",\"longName\":\"Typo3\"}],\"rules\":[{\"key\":\"php:S1192\",\"name\":\"String literals should not be duplicated\",\"desc\":\"<p>\n  Duplicated string literals make the process of refactoring error-prone, since you must be sure to update all occurrences.\n</p>\n<p>\n  On the other hand, constants can be referenced from many places, but only need to be updated in a single place.\n</p>\n\n<h2>Noncompliant Code Example</h2>\n<pre>\nfunction run() {\n  prepare('action1');          // Non-Compliant - 'action1' is duplicated 3 times\n  execute('action1');\n  release('action1');\n}\n</pre>\n\n<h2>Compliant Solution</h2>\n<pre>\nACTION_1 = 'action1';\n\nfunction run() {\n  prepare(ACTION_1);\n  execute(ACTION_1);\n  release(ACTION_1);\n}\n</pre>\n\n<h2>Exceptions</h2>\n<p>To prevent generating some false-positives, literals having less than 5 characters are excluded.</p>\",\"status\":\"READY\"}],\"users\":[]}";
		
		Assert.assertEquals(JsonParserForSonarApiResponses.getNumberOfViolationsOfSpecificRuleForResource(testJson), 2);
	}
}
