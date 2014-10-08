package dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class TableDAO {
	LinkedHashMap<String, List<ResourceInfoRow>> table;
	public TableDAO(LinkedHashMap<String, List<ResourceInfoRow>> table)
	{
		this.table = table;
	}
	
	public LinkedHashMap<String, List<ResourceInfoRow>> getTable()
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
			for(ResourceInfoRow resourceMap : table.get(version))
			{
				if(isResourceRowRelevant(resourceMap))
				{
					listOfdeleteTargetResources.add(resourceMap.getResourceName());
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
	
	private boolean isResourceRowRelevant(ResourceInfoRow resourceRow)
	{
		return resourceRow.getLocTouched()==null || resourceRow.getLocTouched() == 0 || resourceRow.getSize()==0;
	}

	
	public Set<String> getAllRulesInTable()
	{
		
		if (table.size() == 0) {
			return new HashSet<String>();
		}
		String firstVersion = table.keySet().iterator().next();
		
		if(table.get(firstVersion).size() == 0)
			return new HashSet<String>();
		
		
		Set<String> rowContent = table.get(firstVersion).get(0).getAllRuleKeys();
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
	
	public Set<String> getVersions()
	{
		return table.keySet();
	}
	
	public List<ResourceInfoRow> getResourceInfoRowsForVersion(String version)
	{
		return table.get(version);
	}
	
	
}
