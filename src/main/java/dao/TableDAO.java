package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exceptions.PropertyNotFoundException;

public class TableDAO {
	LinkedHashMap<String, HashMap<String, HashMap<String, Integer>>> table;
	public TableDAO(LinkedHashMap<String, HashMap<String, HashMap<String, Integer>>> table)
	{
		this.table = table;
	}
	
	public LinkedHashMap<String, HashMap<String, HashMap<String, Integer>>> getTable()
	{
		return table;
	}
	
	public void filterTable()
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
				if(resourceMap.getValue().get("locTouched")==null || resourceMap.getValue().get("locTouched") == 0 || resourceMap.getValue().get("size")==0)
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
	
	public Double[] getViolationDensityDencityColumnForRule(String rule) throws PropertyNotFoundException
	{
		return calculateQuotientBetweenTwoVariablesOfTableRow(rule, "size");
	}
	
	public Double[] getDefectInjectionFrequencyColumnForRule() throws PropertyNotFoundException
	{
		return calculateQuotientBetweenTwoVariablesOfTableRow("numberDefects", "locTouched");
	}
	
	public Set<String> getAllRulesInTable()
	{
		if (table.size() == 0) {
			return new HashSet<String>();
		}
		String firstVersion = table.keySet().iterator().next();
		
		if(table.get(firstVersion).size() == 0)
			return new HashSet<String>();
		
		String firstResource = table.get(firstVersion).keySet().iterator().next();
		
		Set<String> rowContent = table.get(firstVersion).get(firstResource).keySet();
		Set<String> allRules = new HashSet<String>();
		for(String key : rowContent)
		{
			if(!(key.equals("size") || key.equals("locTouched") || key.equals("numberDefects")))
			{
				allRules.add(key);
			}
		}
		
		return allRules;
	}
	
	private Double[] calculateQuotientBetweenTwoVariablesOfTableRow(String var1, String var2) throws PropertyNotFoundException
	{
		List<Double> defectInjectionFrequencyColumn = new ArrayList<Double>();
		for(String version : table.keySet())
		{
			for(HashMap<String, Integer> valueMapForResource : table.get(version).values())
			{
				if(!valueMapForResource.containsKey(var1))
					throw new PropertyNotFoundException(var1 + " could not be found in table.");
				double var1Value = valueMapForResource.get(var1);
				
				if(!valueMapForResource.containsKey(var2))
					throw new PropertyNotFoundException(var2 + " could not be found in table.");
				double var2Value = valueMapForResource.get(var2);
				defectInjectionFrequencyColumn.add(var1Value/var2Value);
			}
		}
		Double[] array = new Double[defectInjectionFrequencyColumn.size()];
		for(int i = 0; i < defectInjectionFrequencyColumn.size(); i++) array[i] = defectInjectionFrequencyColumn.get(i);
		return array;
	}
}
