package testutils;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

import dao.ResourceInfoRow;

public class TestUtils {
	public static ResourceInfoRow getResourceInfoRow(String className, Integer numberDefects, Integer locTouched, Integer size,  ImmutableMap<String, Integer> violations)
	{
		
		HashMap<String, Integer> violationsHashMap = new HashMap<String, Integer>();
		for(String rule : violations.keySet())
		{
			violationsHashMap.put(rule, violations.get(rule));
		}
		ResourceInfoRow row = new ResourceInfoRow(className);
		row.setNumberOfDefects(numberDefects);
		row.setLocTouched(locTouched);
		row.setSize(size);
		row.setViolationsPerRule(violationsHashMap);
		return row;
	}
}
