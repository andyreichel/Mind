package mind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableFilter {
	public static void filterTable(HashMap<String, HashMap<String, HashMap<String, Integer>>> table)
	{
		if(table.isEmpty())
			return;
		
		String key = table.keySet().iterator().next();
		table.remove(key);
		HashMap<String, List<String>> rememberDeletionMap = new HashMap<String, List<String>>();
		for(String version : table.keySet())
		{
			List<String> listOfdeleteTargetResources = new ArrayList<String>();
			for(Map.Entry<String, HashMap<String, Integer>> resourceMap : table.get(version).entrySet())
			{
				if(resourceMap.getValue().get("locTouched")==null || resourceMap.getValue().get("size")==0)
				{
					listOfdeleteTargetResources.add(resourceMap.getKey());
				}
			}
			rememberDeletionMap.put(version, listOfdeleteTargetResources);
		}
		for(String version : rememberDeletionMap.keySet())
		{
			for(String resource : rememberDeletionMap.get(version))
			{
				table.get(version).remove(resource);
			}
		}
		
	}
}
