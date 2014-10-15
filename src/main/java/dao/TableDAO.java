package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import exceptions.PropertyNotFoundException;


/**
 * 
 * Table DAO that enables the access to the information stored in the table. Table structure looks like this:
 * {"version1" : {ResourceInfoRow1, ResourceInfoRow2}} For more information have a look into ResourceInfoRowDAO
 *
 */
public class TableDAO {
	LinkedHashMap<String, List<ResourceInfoRowDAO>> table;
	public TableDAO(LinkedHashMap<String, List<ResourceInfoRowDAO>> table)
	{
		this.table = table;
	}
	
	public LinkedHashMap<String, List<ResourceInfoRowDAO>> getTable()
	{
		return table;
	}
	
	/**
	 * Filters the table by certain criteria as: 
	 * The first row of the table is not relevant because the size and violations are always 0 at the beginning.
	 * Another rule is that the rows where loc touched is equal to 0 are not relevant. 
	 */
	public void filterTable()
	{
		if(table.isEmpty())
			return;
		
		String key = table.keySet().iterator().next();
		table.remove(key);
		HashMap<String, List<ResourceInfoRowDAO>> rememberDeletionMap = new HashMap<String, List<ResourceInfoRowDAO>>();
		for(String version : table.keySet())
		{
			List<ResourceInfoRowDAO> listOfdeleteTargetResources = new ArrayList<ResourceInfoRowDAO>();
			for(ResourceInfoRowDAO resourceMap : table.get(version))
			{
				if(!isResourceRowRelevant(resourceMap))
				{
					listOfdeleteTargetResources.add(resourceMap);
				}
			}
			rememberDeletionMap.put(version, listOfdeleteTargetResources);
		}
		for(String version : rememberDeletionMap.keySet())
		{
			for(ResourceInfoRowDAO resource : rememberDeletionMap.get(version))
			{
				table.get(version).remove(resource);
			}
		}
	}
	
	private boolean isResourceRowRelevant(ResourceInfoRowDAO resourceRow)
	{
		return !(resourceRow.getLocTouched()==null || resourceRow.getLocTouched() == 0 || resourceRow.getSize()==0);
	}

	public Integer getNumberOfDefectsThroughoutAllVersions()
	{
		Integer numberDefects = 0;
		for(String version : table.keySet())
		{
			for(ResourceInfoRowDAO row : table.get(version))
			{
				numberDefects += row.getNumberDefects();
			}
		}
		return numberDefects;
	}
	
	
	/**
	 * 
	 * @return a set of all rules that have been analzyed by sonar.
	 */
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

	
	public List<ResourceInfoRowDAO> getResourceInfoRowsForVersion(String version)
	{
		return table.get(version);
	}
	
	@Override
	public String toString() 
	{
		StringBuilder tableString =  new StringBuilder();
		tableString.append("########### TABLE ###########\n");
		for(String version : table.keySet())
		{
			tableString.append(version);
			tableString.append("\n");
			for(ResourceInfoRowDAO row : table.get(version))
			{
				tableString.append(row.toString());
				tableString.append("\n");
			}
		}
		
		return tableString.toString();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
			return true;
		
		if(!(obj instanceof TableDAO))
			return false;
		
		TableDAO object = (TableDAO) obj;
		
		return object.table.equals(table);
	}
	
	public int hashCode() {
		int result = 17;
		result = 31 * result + table.hashCode();
		return result;
		}

}
