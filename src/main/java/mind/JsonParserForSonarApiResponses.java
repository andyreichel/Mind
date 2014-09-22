package mind;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParserForSonarApiResponses {
	public static List<AbstractMap.SimpleEntry<String, String>> getMapOfAllVersions(String json) throws JSONException
	{
		JSONArray versions = new JSONArray(json);
		//HashMap<String, String> allVersions = new HashMap<String, String>();
		List<AbstractMap.SimpleEntry<String, String>> allVersions = new ArrayList<AbstractMap.SimpleEntry<String,String>>();
		
		for(int i = 0; i <versions.length(); i++)
		{
			JSONObject obj = (JSONObject) versions.get(i);
			//allVersions.put(obj.getString("n"), obj.getString("dt"));
			allVersions.add(new AbstractMap.SimpleEntry<String, String>(obj.getString("n"), obj.getString("dt")));
		}
		
		return allVersions;
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
