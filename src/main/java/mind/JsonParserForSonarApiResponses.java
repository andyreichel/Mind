package mind;

import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParserForSonarApiResponses {
	public static String getDateOfLastSonarAnalyse(String version, String json) throws JSONException
	{
		JSONArray versions = new JSONArray(json);
		
		JSONObject obj = (JSONObject) versions.get(0);
		return obj.getString("dt");
	}
	
	public static int getNloc(String json)
	{
		return getMetricFromJson(json);
	}
	
	public static int getNumberOfViolationsOfSpecificRuleForResource(String json)
	{
		return getMetricFromJson(json);
	}
	
	private static int getMetricFromJson(String json)
	{
		JSONArray violationsMetric = new JSONArray(json);
		JSONArray cells = ((JSONObject)violationsMetric.get(0)).getJSONArray("cells");
		if(cells.length()==0)
		{
			throw new JSONException("no entry found.");
		}
		JSONObject metric = ((JSONObject)cells.get(0));
		return ((JSONArray)metric.get("v")).getInt(0);
	}
}
