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
		JSONArray violationsMetric = new JSONArray(json);
		JSONArray cells = ((JSONObject)violationsMetric.get(0)).getJSONArray("msr");
		if(cells.length()==0)
		{
			throw new JSONException("no entry found.");
		}
		JSONObject metric = ((JSONObject)cells.get(0));
		String val = metric.get("val").toString();
		return (int)Math.floor(Double.valueOf(val));
	}
	
	public static int getNumberOfViolationsOfSpecificRuleForResource(String json)
	{
		JSONObject violationsMetric = new JSONObject(json);
		return ((JSONObject)violationsMetric.get("paging")).getInt("fTotal");
	}
}
