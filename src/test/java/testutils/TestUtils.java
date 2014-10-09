package testutils;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

import dao.ResourceInfoRow;

public class TestUtils {
	public static ResourceInfoRow getResourceInfoRow(String className, Integer numberDefects, Integer locTouched, Integer size,  ImmutableMap<String, Integer> violations)
	{
		
		ResourceInfoRow row = new ResourceInfoRow(className);
		HashMap<String, Integer> violationsHashMap = new HashMap<String, Integer>();
		if(violations == null)
		{
			row.setViolationsPerRule(null);
		}else
		{
			for(String rule : violations.keySet())
			{
				violationsHashMap.put(rule, violations.get(rule));
			}
		}

		row.setNumberOfDefects(numberDefects);
		row.setLocTouched(locTouched);
		row.setSize(size);
		row.setViolationsPerRule(violationsHashMap);
		return row;
	}
}
