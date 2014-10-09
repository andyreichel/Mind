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
	
	public HashMap<String, Integer> getViolationsMap()
	{
		return violationsPerRule;
	}
	
	public boolean equals(Object obj)
	{
		if(obj == this)
			return true;
		
		if(!(obj instanceof ResourceInfoRow))
			return false;
		
		ResourceInfoRow object = (ResourceInfoRow) obj;
		
		return 	areObjectsEqual(this.numberDefects, object.getNumberDefects()) &&
				areObjectsEqual(this.locTouched, object.getLocTouched()) &&
				areObjectsEqual(this.size, object.getSize()) &&
				areObjectsEqual(this.resourceName, object.getResourceName()) &&
				areObjectsEqual(this.violationsPerRule, object.getViolationsMap());
		
	}
	
	private boolean areObjectsEqual(Object one, Object two)
	{
		if(one!=null)
		{
			return one.equals(two);
		}else return two==null;
	}
	
	public int hashCode() {
		int result = 17;
		result = 31 * result + size ;
		result = 31 * result + locTouched;
		result = 31 * result + numberDefects;
		result = 31 * result + resourceName.hashCode();
		result = 31 * result + violationsPerRule.hashCode();
		return result;
		}
	
	public String toString()
	{
		StringBuilder rowString = new StringBuilder();
		rowString.append(resourceName);
		rowString.append("\n");
		rowString.append("Lines of code touched: " + locTouched);
		rowString.append("\tnumber of defects: " + numberDefects);
		rowString.append("\tsize of resource: " + size);
		for(String rule : violationsPerRule.keySet())
		{
			rowString.append("\t\t" + rule+ ": " + violationsPerRule.get(rule));	
		}
		return rowString.toString();
	}
}
