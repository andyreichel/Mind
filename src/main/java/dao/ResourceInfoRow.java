package dao;

import java.util.HashMap;
import java.util.Set;

import exceptions.PropertyNotFoundException;

public class ResourceInfoRow {
	private Integer size;
	private Integer locTouched;
	private Integer numberDefects;
	private HashMap<String, Integer> violationsPerRule;
	private String resourceName;
	
	public ResourceInfoRow(String resourceName)
	{
		this.resourceName = resourceName;
	}
	
	public void setSize(Integer size)
	{
		this.size = size;
	}
	
	public void setLocTouched(Integer locTouched)
	{
		this.locTouched = locTouched;
	}
	
	public void setNumberOfDefects(Integer numberOfDefects)
	{
		this.numberDefects = numberOfDefects;
	}
	
	public void setViolationsPerRule(HashMap<String, Integer> violationsPerRule)
	{
		this.violationsPerRule = violationsPerRule;
	}
	
	public Integer getLocTouched()
	{
		return locTouched;
	}
	
	public Integer getSize()
	{
		return size;
	}
	
	public Integer getNumberDefects()
	{
		return numberDefects;
	}
	
	public String getResourceName()
	{
		return resourceName;
	}
	
	public Set<String> getAllRuleKeys()
	{
		return violationsPerRule.keySet();
	}
	
	public Integer getNumberOfViolationsForRule(String rule) throws PropertyNotFoundException
	{
		if(!violationsPerRule.containsKey(rule))
			throw new PropertyNotFoundException(rule + " not found in rules");
		return violationsPerRule.get(rule);
	}
	
	public boolean isEqual()
	{
		return true;
	}
}
